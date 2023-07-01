package com.example.spaceshare.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spaceshare.MainActivity
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentLoginBinding
import com.example.spaceshare.ui.viewmodel.AuthViewModel
import com.example.spaceshare.utils.ToastUtil
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var navController: NavController
    @Inject
    lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        configureObservers()
        configureButtons()
    }

    private fun configureObservers() {
        authViewModel.loginStatus.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                ToastUtil.showLongToast(requireContext(), result.message)
            }
        }
    }

    private fun configureButtons() {
        binding.btnLogin.setOnClickListener {
            authViewModel.login(binding.email.text.toString(), binding.password.text.toString())
        }

        binding.btnSignupNav.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

}