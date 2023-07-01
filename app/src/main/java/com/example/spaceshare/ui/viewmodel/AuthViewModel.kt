package com.example.spaceshare.ui.viewmodel

import android.content.IntentSender
import android.util.Log
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spaceshare.AuthActivity
import com.example.spaceshare.R
import com.example.spaceshare.manager.SharedPreferencesManager
import com.example.spaceshare.ui.view.LoginFragment
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import androidx.lifecycle.viewModelScope
import com.example.spaceshare.data.repository.UserRepository
import com.example.spaceshare.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nulabinc.zxcvbn.Zxcvbn
import kotlinx.coroutines.launch
import javax.inject.Inject


class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepo: UserRepository
): ViewModel() {

    companion object {
        private val TAG = this::class.simpleName
    }

    data class AuthResult(
        val isSuccess: Boolean,
        val message: String
    )
    private val _loginStatus = MutableLiveData<AuthResult>()
    val loginStatus: LiveData<AuthResult> = _loginStatus

    private val _registerStatus = MutableLiveData<AuthResult>()
    val registerStatus: LiveData<AuthResult> = _registerStatus

    private val db = Firebase.firestore

    lateinit var oneTapClient: SignInClient
    lateinit var signInRequest: BeginSignInRequest
    lateinit var signUpRequest: BeginSignInRequest
    var showOneTapUI = true

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _loginStatus.value = AuthResult(false, "Please enter both email and password.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        _loginStatus.value = AuthResult(true, "Successfully logged in.")
                    } else {
                        _loginStatus.value = AuthResult(false, "Please verify your email first.")
                    }
                } else {
                    _loginStatus.value = AuthResult(false, "Authentication failed. ${task.exception?.message}")
                }
            }
    }

    fun loginWithSSOCredential(firebaseCredential: AuthCredential) {
        auth.signInWithCredential(firebaseCredential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    _loginStatus.value = AuthResult(true, "Successfully logged in.")
                } else {
                    // Sign in failed.
                    _loginStatus.value = AuthResult(false, "Authentication failed. ${task.exception?.message}")
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
                                _registerStatus.value = AuthResult(false, "Registration successful but failed to update display name.")
                            }
                        }
                } else {
                    _registerStatus.value = AuthResult(false, "Registration failed. ${task.exception?.message}")
                }
            }
    }

    private fun createUserInCollection(userId: String?, firstName: String, lastName: String) {
        if (userId != null) {
            val user = User(userId, firstName, lastName)
            viewModelScope.launch {
                try {
                    userRepo.createUser(user)
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