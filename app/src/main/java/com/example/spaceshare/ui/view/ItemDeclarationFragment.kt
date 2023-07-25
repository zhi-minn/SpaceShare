package com.example.spaceshare.ui.view

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogItemDeclarationBinding
import com.example.spaceshare.enums.DeclareItemType
import com.example.spaceshare.ui.viewmodel.ReservationViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ItemDeclarationFragment(
    private val parentFragment: ReservationPageDialogFragment
): DialogFragment() {
    companion object {
        private val TAG = this::class.simpleName
    }

    private lateinit var binding: DialogItemDeclarationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogItemDeclarationBinding.inflate(inflater, container, false)
        configButtons()
        Log.i(TAG, viewLifecycleOwner.toString())
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.GenericDialogStyle)
        val sizeDialog = Dialog(requireContext())
        sizeDialog.setContentView(R.layout.dialog_size_picker)
        sizeDialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    private fun setEmptyCheck() {
        binding.doneButton.isEnabled = !parentFragment.itemIsEmpty()
    }

    private fun configButtons() {
        binding.btnCloseListing.setOnClickListener {
            this.dismiss()
        }

        binding.clothingChoose.apply {
            isChecked = parentFragment.findItem(DeclareItemType.ClOTHING)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.clothingDetails.visibility = View.VISIBLE
                    parentFragment.addItems(DeclareItemType.ClOTHING, "")
                } else {
                    binding.clothingDetails.visibility = View.GONE
                    parentFragment.removeItems(DeclareItemType.ClOTHING)
                }
                setEmptyCheck()
            }
        }

        binding.documentsChoose.apply {
            isChecked = parentFragment.findItem(DeclareItemType.BOOKS_AND_DOCUMENTS)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    parentFragment.addItems(DeclareItemType.BOOKS_AND_DOCUMENTS, "")
                } else {
                    parentFragment.removeItems(DeclareItemType.BOOKS_AND_DOCUMENTS)
                }
                setEmptyCheck()
            }
        }

        binding.furnitureChoose.apply {
            isChecked = parentFragment.findItem(DeclareItemType.FURNITURE)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    parentFragment.addItems(DeclareItemType.FURNITURE, "")
                } else {
                    parentFragment.removeItems(DeclareItemType.FURNITURE)
                }
                setEmptyCheck()
            }
        }

        binding.sportAndRecreationalChoose.apply {
            isChecked = parentFragment.findItem(DeclareItemType.SPORT_AND_RECREATIONAL)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    parentFragment.addItems(DeclareItemType.SPORT_AND_RECREATIONAL, "")
                } else {
                    parentFragment.removeItems(DeclareItemType.SPORT_AND_RECREATIONAL)
                }
                setEmptyCheck()
            }
        }

        binding.applianceChoose.apply {
            isChecked = parentFragment.findItem(DeclareItemType.APPLIANCE)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    parentFragment.addItems(DeclareItemType.APPLIANCE, "")
                } else {
                    parentFragment.removeItems(DeclareItemType.APPLIANCE)
                }
                setEmptyCheck()
            }
        }

        binding.necessaryChoose.apply {
            isChecked = parentFragment.findItem(DeclareItemType.DAILY_NECESSARY)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    parentFragment.addItems(DeclareItemType.DAILY_NECESSARY, "")
                } else {
                    parentFragment.removeItems(DeclareItemType.DAILY_NECESSARY)
                }
                setEmptyCheck()
            }
        }

        binding.mementosChoose.apply {
            isChecked = parentFragment.findItem(DeclareItemType.MEMENTOS)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    parentFragment.addItems(DeclareItemType.MEMENTOS, "")
                } else {
                    parentFragment.removeItems(DeclareItemType.MEMENTOS)
                }
                setEmptyCheck()
            }
        }

        binding.otherChoose.apply {
            isChecked = parentFragment.findItem(DeclareItemType.OTHERS)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    parentFragment.addItems(DeclareItemType.OTHERS, "")
                } else {
                    parentFragment.removeItems(DeclareItemType.OTHERS)
                }
                setEmptyCheck()
            }
        }

        binding.doneButton.apply {
            setEmptyCheck()
            setOnClickListener {
                dismiss()
            }
        }
    }
}