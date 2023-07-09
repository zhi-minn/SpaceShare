package com.example.spaceshare.ui.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogProfileDetailsBinding
import com.example.spaceshare.enums.ProfileDetail
import com.example.spaceshare.ui.viewmodel.ProfileViewModel
import com.google.firebase.storage.FirebaseStorage
import java.util.Objects

class ProfileDetailsDialogFragment(
    private val profileViewModel: ProfileViewModel
) : DialogFragment() {

    companion object {
        private val TAG = this::class.simpleName
    }
    private lateinit var binding: DialogProfileDetailsBinding
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            binding.progressBar.visibility = View.VISIBLE
            binding.profileDetailsContainer.visibility = View.GONE
            val imageUri = result.data?.data
            imageUri?.let {
                profileViewModel.updateUserPhoto(it) {
                    binding.progressBar.visibility = View.GONE
                    binding.profileDetailsContainer.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_profile_details, container, false)

        configureObservers()
        configureButtons()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.SlideUpDialogStyle)
    }

    private fun configureObservers() {
        profileViewModel.userLiveData.observe(viewLifecycleOwner) { user ->
            // Profile photo
            if (!user.photoPath.isNullOrEmpty()) {
                val storageRef =
                    FirebaseStorage.getInstance().reference.child("profiles/${user.photoPath}")

                Glide.with(requireContext())
                    .load(storageRef)
                    .into(binding.profileImage)
            }

            // Name
            val sb = StringBuilder()
            if (user.firstName.isNotEmpty()) sb.append(user.firstName)
            if (user.lastName.isNotEmpty()) sb.append(" ${user.lastName}")
            binding.legalName.text = sb.toString()

            // Phone number
            binding.phoneNumber.text = user.phoneNumber
        }
    }

    private fun configureButtons() {
        binding.btnBack.setOnClickListener {
            this.dismiss()
        }

        binding.btnEditPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImageLauncher.launch(intent)
        }

        binding.legalNameContainer.setOnClickListener {
            val editProfileDialogFragment = EditProfileDialogFragment(ProfileDetail.NAME, profileViewModel)
            editProfileDialogFragment.show(Objects.requireNonNull(childFragmentManager), "editProfileDialog")
        }

        binding.phoneNumberContainer.setOnClickListener {
            val editProfileDetail = EditProfileDialogFragment(ProfileDetail.PHONE_NUMBER, profileViewModel)
            editProfileDetail.show(Objects.requireNonNull(childFragmentManager), "editProfileDialog")
        }
    }
}