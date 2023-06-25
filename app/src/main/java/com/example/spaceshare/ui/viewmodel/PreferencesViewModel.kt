package com.example.spaceshare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.PreferencesRepository
import com.example.spaceshare.interfaces.LocationInterface
import com.example.spaceshare.models.Preferences
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

class PreferencesViewModel @Inject constructor(
    private val repo: PreferencesRepository
): ViewModel(), LocationInterface {

    private val _preferencesLoaded: MutableLiveData<Boolean> = MutableLiveData()
    val preferencesLoaded: LiveData<Boolean> = _preferencesLoaded

    private val _preferencesLiveData: MutableLiveData<Preferences?> = MutableLiveData()
    val preferencesLiveData: LiveData<Preferences?> = _preferencesLiveData

    init {
        _preferencesLoaded.value = false
        instantiatePreferences()
    }

    fun setRadius(radius: Int) {
        var curPreferences = _preferencesLiveData.value
        if (curPreferences == null) {
            curPreferences = instantiatePreferences()
        }

        curPreferences.radius = radius
        _preferencesLiveData.value = curPreferences
    }

    override fun setLocation(latLng: LatLng) {
        var curPreferences = _preferencesLiveData.value
        if (curPreferences == null) {
            curPreferences = instantiatePreferences()
        }

        val geoPoint = GeoPoint(latLng.latitude, latLng.longitude)
        curPreferences.location = geoPoint
        _preferencesLiveData.value = curPreferences
    }

    fun updatePreferences() {
        viewModelScope.launch {
            val preferences = _preferencesLiveData.value
            if (preferences != null) {
                repo.updatePreferences(preferences)
            }
        }
    }

    fun fetchPreferences(userId: String) {
        viewModelScope.launch {
            val preferences = repo.getPreferences(userId)
            if (preferences != null) {
                _preferencesLiveData.value = preferences
                _preferencesLoaded.value = true
            } else {
                // TODO: Display preferences fetching error
            }
        }
    }

    private fun instantiatePreferences(): Preferences {
        val currentUser = FirebaseAuth.getInstance().currentUser
        _preferencesLiveData.value = Preferences(currentUser?.uid, currentUser?.email)
        return _preferencesLiveData.value!!
    }
}
