package com.example.spaceshare.ui.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spaceshare.R
import com.example.spaceshare.utils.ToastUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nulabinc.zxcvbn.Zxcvbn

class RegisterFragment : Fragment() {

    companion object {
        private val TAG = this::class.simpleName
    }

    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        auth = FirebaseAuth.getInstance()

        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        btnRegister.setOnClickListener {
            val email = view.findViewById<EditText>(R.id.registerEmail).text.toString()
            val password = view.findViewById<EditText>(R.id.registerPassword).text.toString()
            regiserUser(email, password)
        }
    }

    private fun regiserUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            ToastUtil.showShortToast(requireContext(), "Please enter both email and password.")
            return
        }

        val passwordScore = Zxcvbn().measure(password).score
        if (passwordScore == 0) {
            ToastUtil.showShortToast(requireContext(), "Please use a more secure password")
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "User successfully registered. Password score: %d".format(passwordScore))
                    sendEmailVerification(auth.currentUser)
                } else {
                    ToastUtil.showShortToast(requireContext(), "Registration failed. %s".format(task.exception?.message))
                }
                navController.navigate(R.id.action_registerFragment_to_loginFragment)
            }
    }

    private fun sendEmailVerification(user: FirebaseUser?) {
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    ToastUtil.showShortToast(requireContext(), "Verification email sent.")
                } else {
                    ToastUtil.showShortToast(requireContext(), "Failed to send verification email.")
                }
            }
    }
}