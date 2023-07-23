package com.example.spaceshare.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentUserEntryBinding
import com.example.spaceshare.ui.viewmodel.AdminViewModel
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserEntryFragment : Fragment() {

    private lateinit var binding: FragmentUserEntryBinding
    private lateinit var id: String
    private lateinit var navController: NavController

    @Inject
    lateinit var adminViewModel: AdminViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_entry, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        id = arguments?.getString("id").toString()
        val email = arguments?.getString("email")
        val firstName = arguments?.getString("firstName")
        val lastName = arguments?.getString("lastName")
        val governmentId = arguments?.getString("governmentId")
        val idStr = "ID: $id"

        binding.userEntryId.text = idStr
        binding.userEntryEmail.text = email
        binding.userEntryFirstName.text = firstName
        binding.userEntryLastName.text = lastName

        val storageRef =
            FirebaseStorage.getInstance().reference.child("ids/${governmentId}")

        Glide.with(requireContext())
            .load(storageRef)
            .into(binding.userGovernmentId)

        configureButtons()
    }

    private fun configureButtons() {
        binding.buttonAccept.setOnClickListener {
            adminViewModel.updateUserVerifiedStatus(id, 1)
            val text = "$id has been verified"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(requireContext(), text, duration) // in Activity
            toast.show()
            navController.popBackStack()
        }

        binding.buttonReject.setOnClickListener {
            adminViewModel.updateUserVerifiedStatus(id, 2)
            val text = "$id has been rejected"
            val duration = Toast.LENGTH_SHORT
            val toast = Toast.makeText(requireContext(), text, duration) // in Activity
            toast.show()
            navController.popBackStack()
        }

        binding.buttonBack.setOnClickListener {
            navController.popBackStack()
        }
    }
}