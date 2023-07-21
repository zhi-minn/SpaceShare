package com.example.spaceshare.ui.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.consts.ListingConsts.RECOMMENDED_PRICING_SEARCH_RADIUS
import com.example.spaceshare.consts.ListingConsts.SPACE_OFFERING_LOWER_LIMIT
import com.example.spaceshare.consts.ListingConsts.SPACE_UPPER_LIMIT
import com.example.spaceshare.data.repository.FirebaseStorageRepository
import com.example.spaceshare.data.repository.ListingRepository
import com.example.spaceshare.data.repository.PreferencesRepository
import com.example.spaceshare.enums.Amenity
import com.example.spaceshare.interfaces.LocationInterface
import com.example.spaceshare.models.ImageModel
import com.example.spaceshare.models.Listing
import com.example.spaceshare.utils.GeocoderUtil
import com.example.spaceshare.utils.MailUtil
import com.example.spaceshare.utils.MathUtil
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

class ListingMetadataViewModel @Inject constructor(
    private val listingRepo: ListingRepository,
    private val preferencesRepo: PreferencesRepository,
    private val firebaseStorageRepo: FirebaseStorageRepository
) : ViewModel(), LocationInterface {

    companion object {
        private val TAG = this::class.simpleName
    }

    // Used to display in UI
    private val _images: MutableLiveData<MutableList<ImageModel>> = MutableLiveData(mutableListOf())
    val images: LiveData<MutableList<ImageModel>> = _images

    // Persist to storage
    private val _addImages: MutableLiveData<MutableList<ImageModel>> = MutableLiveData()

    // Delete from storage
    private val _deleteImages: MutableLiveData<MutableList<String>> = MutableLiveData()

    private val _spaceAvailable: MutableLiveData<Double> = MutableLiveData(0.5)
    val spaceAvailable: LiveData<Double> = _spaceAvailable

    private val _listingLiveData: MutableLiveData<Listing> = MutableLiveData()
    val listingLiveData: LiveData<Listing> = _listingLiveData

    private val _imageNewlyAdded: MutableLiveData<Boolean> = MutableLiveData(false)
    val imageNewlyAdded: LiveData<Boolean> = _imageNewlyAdded

    data class PublishResult(
        val isSuccess: Boolean,
        val listing: Listing? = null
    )

    private val _publishResult: MutableLiveData<PublishResult> = MutableLiveData()
    val publishResult: LiveData<PublishResult> = _publishResult

    data class ValidateResult(
        val isSuccess: Boolean,
        val message: String
    )

    private val _validateResult: MutableLiveData<ValidateResult> = MutableLiveData()
    val validateResult: LiveData<ValidateResult> = _validateResult

    private val _recommendedPrice: MutableLiveData<Double> = MutableLiveData()
    val recommendedPrice: LiveData<Double> = _recommendedPrice

    init {
        _listingLiveData.value = Listing(hostId = FirebaseAuth.getInstance().currentUser?.uid)
    }

    fun addImageUri(imageUri: Uri) {
        val newImage = ImageModel(localUri = imageUri)

        val newAddImages = _addImages.value ?: mutableListOf()
        newAddImages.add(newImage)
        _addImages.value = newAddImages

        val newImages = _images.value ?: mutableListOf()
        newImages.add(newImage)
        _images.value = newImages
        _imageNewlyAdded.value = true
    }

    fun removeImage(position: Int) {
        val newImages = _images.value ?: mutableListOf()
        val newAddImages = _addImages.value ?: mutableListOf()
        val newDeleteImages = _deleteImages.value ?: mutableListOf()
        val image = newImages[position]

        // First add to delete images if image removed currently exists on Firebase Storage
        if (!newAddImages.contains(image) &&
            (image.imagePath != null) &&
            !newDeleteImages.contains(image.imagePath)) {
            newDeleteImages.add(image.imagePath)
            _deleteImages.value = newDeleteImages
        }

        // Then remove from images to upload if exists
        newAddImages.remove(image)

        // Finally remove from list of images shown to user
        newImages.remove(image)
        _images.value = newImages
    }

    override fun setLocation(location: LatLng) {
        val newListing = _listingLiveData.value
        newListing?.let {
            it.location = GeoPoint(location.latitude, location.longitude)
            _listingLiveData.value = it
            updateRecommendedPricing()
        }
    }

    fun setListing(listing: Listing) {
        _listingLiveData.value = listing
        _images.value = listing.photos.map { ImageModel(imagePath = it) }.toMutableList()
        _spaceAvailable.value = listing.spaceAvailable
    }

    fun incrementSpaceAvailable() {
        if (_spaceAvailable.value?.plus(0.5)!! < SPACE_UPPER_LIMIT) {
            _spaceAvailable.value = _spaceAvailable.value?.plus(0.5)
            updateRecommendedPricing()
        }
    }

    fun decrementSpaceAvailable() {
        if (_spaceAvailable.value?.minus(0.5)!! >= SPACE_OFFERING_LOWER_LIMIT) {
            _spaceAvailable.value = _spaceAvailable.value?.minus(0.5)
            updateRecommendedPricing()
        }
    }

    fun postListing(
        title: String,
        price: Double,
        description: String,
        amenities: MutableList<Amenity>
    ) {
        val finalListing = _listingLiveData.value!!
        finalListing.title = title
        finalListing.price = price
        finalListing.description = description
        finalListing.spaceAvailable = _spaceAvailable.value ?: 0.0
        finalListing.amenities.clear()
        finalListing.amenities.addAll(amenities)
        // Set updatedAt field to null so Firebase will set it
        finalListing.updatedAt = null
        if (!validateListing(finalListing)) return
        _validateResult.value = ValidateResult(true, "Listing validated")

        viewModelScope.launch {
            // Upload images
            val imagesToUpload = _addImages.value

            if (!imagesToUpload.isNullOrEmpty()) {
                val uploadTasks = imagesToUpload.map { image ->
                    viewModelScope.async {
                        try {
                            image.localUri?.let {
                                firebaseStorageRepo.uploadFile("spaces", it)
                            }
                        } catch (e: Exception) {
                            // TODO: Handle file upload exception here
                            null
                        }
                    }
                }

                val imageNames = uploadTasks.awaitAll().filterNotNull()

                finalListing.photos.addAll(imageNames)
            }

            // Delete images
            val imagesToDelete = _deleteImages.value
            if (!imagesToDelete.isNullOrEmpty()) {
                imagesToDelete.forEach {
                    viewModelScope.async {
                        try {
                            firebaseStorageRepo.deleteFile("spaces", it)
                        } catch (e: Exception) {
                            null
                        }
                    }
                }
                finalListing.photos.removeAll(imagesToDelete)
            }

            // Upload listing
            try {
                listingRepo.postListing(finalListing)
                _publishResult.value = PublishResult(true, finalListing)
                Log.i(TAG, "Updated to ${_publishResult.value}")

                // Inform users whose preferences matches listing
                finalListing.location?.let {
                    preferencesRepo.getAllPreferences().forEach { preferences ->
                        // Don't send notification to yourself
                        if (FirebaseAuth.getInstance().currentUser?.email.equals(preferences.email)) {
                            return@forEach
                        }

                        // Only send notification to active, valid preferences
                        if (preferences.isActive && preferences.location != null && preferences.email != null) {
                            val distance =
                                MathUtil.calculateDistanceInKilometers(it, preferences.location!!)
                            if (distance <= preferences.radius) {
                                MailUtil.sendEmail(
                                    preferences.email, "New Listing Alert",
                                    getEmailBody(finalListing, distance)
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // TODO: Inform user error creating listing in UI
                Log.e(
                    "CreateListing",
                    "Error creating listing or sending email preferences: ${e.message}"
                )
                _publishResult.value = PublishResult(false)
            }
        }
    }

    private fun validateListing(listing: Listing): Boolean {
        val existingImageCount = _images.value?.size ?: 0
        val newImageCount = _addImages.value?.size ?: 0
        val deleteImageCount = _deleteImages.value?.size ?: 0
        val finalImageCount = existingImageCount + newImageCount - deleteImageCount
        if (finalImageCount <= 0) {
            _validateResult.value = ValidateResult(false, "Please upload at least 1 image")
            return false
        }

        if (listing.title.isEmpty() || listing.description.isEmpty()) {
            _validateResult.value = ValidateResult(false, "Please ensure all fields are filled")
            return false
        }

        if (listing.location == null) {
            _validateResult.value =
                ValidateResult(false, "Please select a location for your listing")
            return false
        }

        return true
    }

    private fun getEmailBody(listing: Listing, distance: Double): String {
        val sb = StringBuilder()
        val address =
            GeocoderUtil.getAddress(listing.location!!.latitude, listing.location!!.longitude)

        sb.append("There is a new listing matching your preferences at the following address:\n")
        sb.append("$address\n\n")
        sb.append("Here are some basic information regarding the listing:\n")
        sb.append("Title: ${listing.title}\n")
        sb.append("Description: ${listing.description}\n")
        sb.append("Price: ${listing.price} CAD/day\n")
        sb.append(
            "Approximate distance to preferred location: ${
                String.format(
                    "%.2f",
                    distance
                )
            } km"
        )

        return sb.toString()
    }

    fun updateRecommendedPricing() {
        viewModelScope.launch {
            val allListings = listingRepo.getAllListings()
            val filteredListings = applyFilters(allListings)

            val listOfPricesPerCubicMetre = filteredListings.map { listing ->
                listing.price / listing.spaceAvailable
            }

            if (listOfPricesPerCubicMetre.isNotEmpty()) {
                _recommendedPrice.value = listOfPricesPerCubicMetre.average() * _spaceAvailable.value!!
            }
        }
    }

    private fun applyFilters(listings: List<Listing>): List<Listing> {
        return listings.filter { listing ->
            filterForActiveListings(listing) &&
            filterForNonOwnListings(listing) &&
            filterByDistance(listing)
        }
    }

    private fun filterByDistance(listing: Listing): Boolean {
        return try {
            val dist = MathUtil.calculateDistanceInKilometers(
                _listingLiveData.value?.location!!,
                listing.location!!
            )
            dist <= RECOMMENDED_PRICING_SEARCH_RADIUS
        } catch (e: Exception) {
            Log.e(
                TAG,
                "Error calculating and filtering by Listing distance for id ${listing.id}: ${e.message}"
            )
            false
        }
    }

    private fun filterForActiveListings(listing: Listing): Boolean {
        return listing.isActive
    }

    private fun filterForNonOwnListings(listing: Listing): Boolean {
        return listing.hostId != FirebaseAuth.getInstance().currentUser?.uid
    }

    interface ListingMetadataDialogListener {
        fun onListingCreated(listing: Listing?)
    }
}