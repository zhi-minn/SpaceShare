package com.example.spaceshare.ui.viewmodel

import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.UserRepository
import com.example.spaceshare.manager.FCMTokenManager
import com.example.spaceshare.models.User
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.nulabinc.zxcvbn.Zxcvbn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepo: UserRepository
) : ViewModel() {

    companion object {
        private val TAG = this::class.simpleName
    }

    data class AuthResult(
        val isSuccess: Boolean,
        val message: String,
        val admin: Boolean = false
    )

    private val _loginStatus = MutableLiveData<AuthResult>()
    val loginStatus: LiveData<AuthResult> = _loginStatus

    private val _registerStatus = MutableLiveData<AuthResult>()
    val registerStatus: LiveData<AuthResult> = _registerStatus

    lateinit var oneTapClient: SignInClient
    lateinit var signInRequest: BeginSignInRequest
    lateinit var signUpRequest: BeginSignInRequest
    var showOneTapUI = true

    var adminUsers: List<String> = listOf()

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginStatus.value = AuthResult(false, "Please enter both email and password.")
            return
        }

        runBlocking {
            adminUsers = userRepo.getAdminUsers()
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && adminUsers.contains(user.email)) {
                        _loginStatus.value = AuthResult(true, "Admin account.", true)
                    } else if (user != null && user.isEmailVerified) {
                        _loginStatus.value = AuthResult(true, "Successfully logged in.")
                    } else {
                        _loginStatus.value = AuthResult(false, "Please verify your email first.")
                    }
                } else {
                    _loginStatus.value =
                        AuthResult(false, "Authentication failed. ${task.exception?.message}")
                }
            }
    }

    fun processGoogleSSOCredential(data: Intent?) {
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(data)
            val idToken = credential.googleIdToken
            when {
                idToken != null -> {
                    // Got an ID token from Google. Use it to authenticate with Firebase.
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    Log.d(TAG, "Got ID token.")

                    var firstName = credential.givenName
                    var lastName = credential.familyName

                    if (firstName == null)
                        firstName = ""
                    if (lastName == null)
                        lastName = ""

                    loginWithSSOCredential(firebaseCredential, firstName, lastName)
                }

                else -> {
                    // Shouldn't happen.
                    Log.d(TAG, "No ID token!")
                }
            }
        } catch (e: ApiException) {
            when (e.statusCode) {
                CommonStatusCodes.CANCELED -> {
                    Log.d(TAG, "One-tap dialog was closed.")
                    // Don't re-prompt the user.
                    showOneTapUI = false
                }

                CommonStatusCodes.NETWORK_ERROR -> {
                    Log.d(TAG, "One-tap encountered a network error.")
                    // Try again or just ignore.
                }

                else -> {
                    Log.d(
                        TAG, "Couldn't get credential from result." +
                                " (${e.localizedMessage})"
                    )
                }
            }
        }
    }

    private fun loginWithSSOCredential(
        firebaseCredential: AuthCredential,
        firstName: String,
        lastName: String
    ) {
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    _loginStatus.value = AuthResult(true, "Successfully logged in.")

                    // Create user in collection for custom fields
                    val user = auth.currentUser
                    createUserInCollection(
                        user?.uid,
                        firstName,
                        lastName
                    )
                } else {
                    // Sign in failed.
                    _loginStatus.value =
                        AuthResult(false, "Authentication failed. ${task.exception?.message}")
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    fun register(firstName: String, lastName: String, email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _registerStatus.value = AuthResult(false, "Please enter both email and password.")
            return
        }

        val passwordScore = Zxcvbn().measure(password).score
        if (passwordScore == 0) {
            _registerStatus.value = AuthResult(false, "Please use a more secure password.")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    // Create user in collection for custom fields
                    createUserInCollection(user?.uid, firstName, lastName)

                    // Update display name
                    val displayName = "$firstName $lastName"
                    user?.updateProfile(buildProfileUpdateRequest(displayName))
                        ?.addOnCompleteListener { updateTask ->
                            sendEmailVerification(user)
                            if (updateTask.isSuccessful) {
                                _registerStatus.value = AuthResult(true, "Verification email sent")
                            } else {
                                _registerStatus.value = AuthResult(
                                    false,
                                    "Registration successful but failed to update display name."
                                )
                            }
                        }
                } else {
                    _registerStatus.value =
                        AuthResult(false, "Registration failed. ${task.exception?.message}")
                }
            }
    }

    fun updateUserFcmToken() {
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            viewModelScope.launch {
                val curUser = userRepo.getUserById(it)
                FCMTokenManager.getToken()?.let {
                    if (curUser != null) {
                        curUser.fcmToken = it
                        userRepo.setUser(curUser)
                    }
                }
            }
        }
    }

    fun removeFcmTokenIfMatch() {
        FirebaseAuth.getInstance().currentUser?.uid?.let {
            viewModelScope.launch {
                val curUser = userRepo.getUserById(it)
                FCMTokenManager.getToken()?.let {
                    if (curUser != null && curUser.fcmToken == it) {
                        curUser.fcmToken = ""
                        userRepo.setUser(curUser)
                    }
                }
            }
        }
    }

    private fun createUserInCollection(userId: String?, firstName: String?, lastName: String?) {
        if (userId != null && firstName != null && lastName != null) {
            val user = User(userId, firstName, lastName)
            viewModelScope.launch {
                try {
                    if (userRepo.getUserById(userId) == null) {
                        userRepo.setUser(user)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error creating user in collection: ${e.message}", e)
                }
            }
        }
    }

    private fun buildProfileUpdateRequest(displayName: String): UserProfileChangeRequest {
        return UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
    }

    private fun sendEmailVerification(user: FirebaseUser?) {
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _registerStatus.value = AuthResult(true, "Verification email sent.")
                } else {
                    _registerStatus.value = AuthResult(false, "Failed to send verification email.")
                }
            }
    }

    fun logout() {
        auth.signOut()
    }
}