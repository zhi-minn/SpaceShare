package com.example.spaceshare.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.spaceshare.R

class ProfileFragment : Fragment() {

    private lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()

        val btnSwitchMode = view.findViewById<TextView>(R.id.btnSwitchMode)
        btnSwitchMode.setOnClickListener {
            navController.navigate(R.id.action_profileFragment_to_createListingFragment)
        }
    }

}