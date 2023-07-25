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
    private var items: MutableMap<DeclareItemType, String> = parentFragment.getItems()

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

    private fun update() {
        parentFragment.updateItems(items)
    }

    private fun removeItems(type: DeclareItemType) {
        try {
            items.remove(type) ?: throw Exception("Remove item Error!")
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    private fun configButtons() {
        binding.btnCloseListing.setOnClickListener {
            this.dismiss()
        }

        binding.clothingChoose.apply {
            isChecked = items.contains(DeclareItemType.ClOTHING)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.clothingDetails.visibility = View.VISIBLE
                    items[DeclareItemType.ClOTHING] = ""
                } else {
                    binding.clothingDetails.visibility = View.GONE
                    removeItems(DeclareItemType.ClOTHING)
                }
            }
        }

        binding.documentsChoose.apply {
            isChecked = items.contains(DeclareItemType.BOOKS_AND_DOCUMENTS)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.documentsDetails.visibility = View.VISIBLE
                    items[DeclareItemType.BOOKS_AND_DOCUMENTS] = ""
                } else {
                    binding.documentsDetails.visibility = View.GONE
                    removeItems(DeclareItemType.BOOKS_AND_DOCUMENTS)
                }
            }
        }

        binding.furnitureChoose.apply {
            isChecked = items.contains(DeclareItemType.FURNITURE)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.furnitureDetails.visibility = View.VISIBLE
                    items[DeclareItemType.FURNITURE] = ""
                } else {
                    binding.furnitureDetails.visibility = View.GONE
                    removeItems(DeclareItemType.FURNITURE)
                }
            }
        }

        binding.sportAndRecreationalChoose.apply {
            isChecked = items.contains(DeclareItemType.SPORT_AND_RECREATIONAL)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.sportAndRecreationalDetails.visibility = View.VISIBLE
                    items[DeclareItemType.SPORT_AND_RECREATIONAL] = ""
                } else {
                    binding.sportAndRecreationalDetails.visibility = View.GONE
                    removeItems(DeclareItemType.SPORT_AND_RECREATIONAL)
                }
            }
        }

        binding.applianceChoose.apply {
            isChecked = items.contains(DeclareItemType.APPLIANCE)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.applianceDetails.visibility = View.VISIBLE
                    items[DeclareItemType.APPLIANCE] = ""
                } else {
                    binding.applianceDetails.visibility = View.GONE
                    removeItems(DeclareItemType.APPLIANCE)
                }
            }
        }

        binding.necessaryChoose.apply {
            isChecked = items.contains(DeclareItemType.DAILY_NECESSARY)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.necessaryDetails.visibility = View.VISIBLE
                    items[DeclareItemType.DAILY_NECESSARY] = ""
                } else {
                    binding.necessaryDetails.visibility = View.GONE
                    removeItems(DeclareItemType.DAILY_NECESSARY)
                }
            }
        }

        binding.mementosChoose.apply {
            isChecked = items.contains(DeclareItemType.MEMENTOS)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.mementosDetails.visibility = View.VISIBLE
                    items[DeclareItemType.MEMENTOS] = ""
                } else {
                    binding.mementosDetails.visibility = View.GONE
                    removeItems(DeclareItemType.MEMENTOS)
                }
            }
        }

        binding.otherChoose.apply {
            isChecked = items.contains(DeclareItemType.OTHERS)
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.otherDetails.visibility = View.VISIBLE
                    items[DeclareItemType.OTHERS] = ""
                } else {
                    binding.otherDetails.visibility = View.GONE
                    removeItems(DeclareItemType.OTHERS)
                }
            }
        }

        binding.doneButton.apply {
            setOnClickListener {
                update()
                dismiss()
            }
        }
    }

    private fun configBindings() {
        binding.clothingDetails.apply {
            // maxWidth =
        }
    }
}