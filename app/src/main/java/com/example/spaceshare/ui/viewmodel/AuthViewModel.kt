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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nulabinc.zxcvbn.Zxcvbn
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth
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

    fun register(email: String, password: String) {
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
                    sendEmailVerification(auth.currentUser)
                    val userProperties = hashMapOf("verified" to false)
                    db.collection("UserVerification").document(email)
                        .set(userProperties)
                } else {
                    _registerStatus.value = AuthResult(false, "Registration failed. ${task.exception?.message}")
                }
            }
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