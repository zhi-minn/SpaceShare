package com.example.spaceshare.ui.view

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spaceshare.AuthActivity
import com.example.spaceshare.MainActivity
import com.example.spaceshare.R
import com.example.spaceshare.databinding.FragmentLoginBinding
import com.example.spaceshare.ui.viewmodel.AuthViewModel
import com.example.spaceshare.utils.ToastUtil
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    companion object {
        private val TAG = this::class.simpleName
    }

    private lateinit var binding: FragmentLoginBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var authViewModel: AuthViewModel

    private val REQ_ONE_TAP = 666  // Can be any integer unique to the Fragment

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

        configureGoogleSSO()
        configureObservers()
        configureButtons()
    }

    private fun configureGoogleSSO() {
        authViewModel.oneTapClient = Identity.getSignInClient(requireActivity())
        authViewModel.signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.default_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()
        authViewModel.signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.default_web_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
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

        binding.btnContinueWithGoogle.setOnClickListener {
            startGoogleSSO()
        }

        binding.btnSignupNav.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun startGoogleSSO() {
        authViewModel.oneTapClient.beginSignIn(authViewModel.signInRequest)
            .addOnSuccessListener(requireActivity()) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender,
                        REQ_ONE_TAP,
                        null, 0, 0, 0, null
                    )

                } catch (e: IntentSender.SendIntentException) {
                    Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(requireActivity()) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow
                authViewModel.oneTapClient.beginSignIn(authViewModel.signUpRequest)
                    .addOnSuccessListener(requireActivity()) { result ->
                        try {
                            startIntentSenderForResult(
                                result.pendingIntent.intentSender,
                                REQ_ONE_TAP,
                                null, 0, 0, 0, null
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                        }
                    }
                    .addOnFailureListener(requireActivity()) { e ->
                        // No Google Accounts found. Just continue presenting the signed-out UI.
                        Log.d(TAG, e.localizedMessage)
                    }
                Log.d(TAG, e.localizedMessage)
            }
    }

    private fun handleGoogleSSOResult(requestCode: Int, data: Intent?) {
        when (requestCode) {
            REQ_ONE_TAP -> {
                authViewModel.processGoogleSSOCredential(data)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handleGoogleSSOResult(requestCode, data)
    }
}