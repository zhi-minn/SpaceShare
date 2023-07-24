package com.example.spaceshare.ui.view

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogItemDeclarationBinding
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ItemDeclarationFragment(
    private val viewModel: ReservationViewModel
): DialogFragment() {
    companion object {
        private val TAG = this::class.simpleName
    }

    @Inject
    private lateinit var binding: DialogItemDeclarationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogItemDeclarationBinding.inflate(inflater, container, false)
        configButtons()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.GenericDialogStyle)
        val sizeDialog = Dialog(requireContext())
        sizeDialog.setContentView(R.layout.dialog_size_picker)
        sizeDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    private fun configButtons() {
        binding.btnCloseListing.setOnClickListener {

        }
        binding.clothingChoose.setOnClickListener {
            if (it.isSelected) {

            } else {

            }
        }

        binding.documentsChoose.setOnClickListener {
            if (it.isSelected) {

            } else {

            }
        }

    }
}