package com.example.spaceshare.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogProfileDetailsBinding
import com.example.spaceshare.ui.viewmodel.ProfileViewModel

class ProfileDetailsDialogFragment(
    private val profileViewModel: ProfileViewModel
) : DialogFragment() {

    companion object {
        private val TAG = this::class.simpleName
    }
    private lateinit var binding: DialogProfileDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_profile_details, container, false)

        configureBindings()
        configureButtons()

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.SlideUpDialogStyle)
    }

    private fun configureBindings() {

    }

    private fun configureButtons() {

    }
}