package com.example.spaceshare.ui.view

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogEditProfileBinding
import com.example.spaceshare.enums.ProfileDetail
import com.example.spaceshare.ui.viewmodel.ProfileViewModel
import com.example.spaceshare.utils.PhoneNumberValidator
import com.example.spaceshare.utils.SnackbarUtil
import com.example.spaceshare.utils.StringUtil.isLettersOnly
import com.google.firebase.auth.FirebaseAuth

class EditProfileDialogFragment(
    private val detail: ProfileDetail,
    private val profileViewModel: ProfileViewModel
) : DialogFragment() {

    companion object {
        private val TAG = this::class.simpleName
    }

    // Consts
    private val NAME_TITLE = "Name"
    private val NAME_DESCRIPTION = "This name will be used to verify your identity. Changing it will require re-verification."
    private val PHONE_NUMBER_TITLE = "Phone number"
    private val PHONE_NUMBER_DESCRIPTION = "This will be provided to potential renters as a secondary contact"
    private val GOVERNMENT_ID_TITLE = "Government ID"
    private val GOVERNMENT_ID_DESCRIPTION = "This allows us to make sure you are real and provides the platform an assurance of trust and transparency."

    private lateinit var binding: DialogEditProfileBinding

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
            val imgUri = Uri.parse(uri.toString())
            profileViewModel.setFileName(imgUri)
            binding.governmentIdImageContainer.visibility = View.VISIBLE
            binding.governmentIdImage.setImageURI(uri)
            binding.btnUpdate.text = "UPLOAD"
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_edit_profile, container, false)

        configureUI()
        configureButtons()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.SlideLeftDialogStyle)
    }

    private fun configureUI() {
        when (detail) {
            ProfileDetail.NAME -> {
                binding.nameContainer.visibility = View.VISIBLE
                binding.detailLabel.text = NAME_TITLE
                binding.detailDescription.text = NAME_DESCRIPTION
                binding.firstName.setText(profileViewModel.userLiveData.value?.firstName)
                binding.lastName.setText(profileViewModel.userLiveData.value?.lastName)
            }
            ProfileDetail.PHONE_NUMBER -> {
                binding.phoneNumberContainer.visibility = View.VISIBLE
                binding.detailLabel.text = PHONE_NUMBER_TITLE
                binding.detailDescription.text = PHONE_NUMBER_DESCRIPTION
                binding.phoneNumber.setText(profileViewModel.userLiveData.value?.phoneNumber)
                binding.phoneNumber.addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {}

                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {}

                    override fun afterTextChanged(s: Editable) {
                        val text: String = binding.phoneNumber.text.toString()
                        val textLength: Int = binding.phoneNumber.text.length
                        if (text.endsWith("-") || text.endsWith(" ") || text.endsWith(" ")) return
                        if (textLength == 1) {
                            if (!text.contains("(")) {
                                binding.phoneNumber.setText(
                                    StringBuilder(text).insert(text.length - 1, "(").toString()
                                )
                                binding.phoneNumber.setSelection(binding.phoneNumber.text.length)
                            }
                        } else if (textLength == 5) {
                            if (!text.contains(")")) {
                                binding.phoneNumber.setText(
                                    StringBuilder(text).insert(text.length - 1, ")").toString()
                                )
                                binding.phoneNumber.setSelection(binding.phoneNumber.text.length)
                            }
                        } else if (textLength == 6) {
                            binding.phoneNumber.setText(
                                StringBuilder(text).insert(text.length - 1, " ").toString()
                            )
                            binding.phoneNumber.setSelection(binding.phoneNumber.text.length)
                        } else if (textLength == 10) {
                            if (!text.contains("-")) {
                                binding.phoneNumber.setText(
                                    StringBuilder(text).insert(text.length - 1, "-").toString()
                                )
                                binding.phoneNumber.setSelection(binding.phoneNumber.text.length)
                            }
                        } else if (textLength == 15) {
                            if (text.contains("-")) {
                                binding.phoneNumber.setText(
                                    StringBuilder(text).insert(text.length - 1, "-").toString()
                                )
                                binding.phoneNumber.setSelection(binding.phoneNumber.text.length)
                            }
                        } else if (textLength == 18) {
                            if (text.contains("-")) {
                                binding.phoneNumber.setText(
                                    StringBuilder(text).insert(text.length - 1, "-").toString()
                                )
                                binding.phoneNumber.setSelection(binding.phoneNumber.text.length)
                            }
                        }
                    }
                })
            }
            ProfileDetail.GOVERNMENT_ID -> {
                binding.governmentIdContainer.visibility = View.VISIBLE
                binding.detailLabel.text = GOVERNMENT_ID_TITLE
                binding.detailDescription.text = GOVERNMENT_ID_DESCRIPTION
                binding.btnUpdate.text = "Add Government ID"
            }
        }
    }

    private fun configureButtons() {
        binding.btnBack.setOnClickListener {
            this.dismiss()
        }

        binding.btnUpdate.setOnClickListener {
            when (detail) {
                ProfileDetail.NAME -> {
                    if (binding.firstName.text.isNullOrEmpty() || binding.lastName.text.isNullOrEmpty()) {
                        SnackbarUtil.showErrorSnackbar(binding.root, "Please ensure both first and last names are filled", resources)
                    }

                    val newFirstName = binding.firstName.text.toString()
                    val newLastName = binding.lastName.text.toString()
                    if (!newFirstName.isLettersOnly() ||
                            !newLastName.isLettersOnly()) {
                        SnackbarUtil.showErrorSnackbar(binding.root, "Invalid name", resources)
                    }

                    val curUser = profileViewModel.userLiveData.value
                    curUser?.let {
                        it.firstName = newFirstName
                        it.lastName = newLastName
                        it.isVerified = false
                        profileViewModel.updateUser(it)
                    }
                    this.dismiss()
                }
                ProfileDetail.PHONE_NUMBER -> {
                    val newPhoneNumber = binding.phoneNumber.text.toString()
                    if (!PhoneNumberValidator.isValidCanadianPhoneNumber(newPhoneNumber)) {
                        SnackbarUtil.showErrorSnackbar(binding.root, "Invalid phone number", resources)
                    }

                    val curUser = profileViewModel.userLiveData.value
                    curUser?.let {
                        it.phoneNumber = newPhoneNumber
                        profileViewModel.updateUser(it)
                    }
                    this.dismiss()
                }
                ProfileDetail.GOVERNMENT_ID -> {
                    if (binding.btnUpdate.text == "Add Government ID") {
                        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    else {
                        val imageUri = profileViewModel.fileNameLiveData.value
                        println("imageUri: $imageUri")
                        if (imageUri != null) {
                            val id = FirebaseAuth.getInstance().currentUser!!.uid
                            profileViewModel.updateGovernmentId(imageUri.toUri(), id) {
                                binding.governmentIdImage.setImageURI(null)
                                binding.governmentIdImageContainer.visibility = View.GONE
                                binding.btnUpdate.text = "Add Government ID"
                                this.dismiss()
                            }
                        }
                    }
                }
            }
        }

        binding.governmentIdRemove.setOnClickListener {
            binding.governmentIdImage.setImageURI(null)
            binding.governmentIdImageContainer.visibility = View.GONE
            binding.btnUpdate.text = "Add Government ID"
        }
    }
}