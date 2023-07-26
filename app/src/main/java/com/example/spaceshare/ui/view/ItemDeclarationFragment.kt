package com.example.spaceshare.ui.view

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.example.spaceshare.R
import com.example.spaceshare.databinding.DialogItemDeclarationBinding
import com.example.spaceshare.enums.DeclareItemType
import dagger.hilt.android.AndroidEntryPoint

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
        configBindings()
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
            if (isChecked) {
                binding.clothingDetails.apply {
                    visibility = View.VISIBLE
                    setText(items[DeclareItemType.ClOTHING])
                }
            }
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.clothingDetails.visibility = View.VISIBLE
                    items[DeclareItemType.ClOTHING] = ""
                } else {
                    binding.clothingDetails.visibility = View.GONE
                    binding.clothingDetails.setText("")
                    removeItems(DeclareItemType.ClOTHING)
                }
            }
        }

        binding.documentsChoose.apply {
            isChecked = items.contains(DeclareItemType.BOOKS_AND_DOCUMENTS)
            if (isChecked) {
                binding.documentsDetails.apply {
                    visibility = View.VISIBLE
                    setText(items[DeclareItemType.BOOKS_AND_DOCUMENTS])
                }
            }
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.documentsDetails.visibility = View.VISIBLE
                    items[DeclareItemType.BOOKS_AND_DOCUMENTS] = ""
                } else {
                    binding.documentsDetails.visibility = View.GONE
                    binding.documentsDetails.setText("")
                    removeItems(DeclareItemType.BOOKS_AND_DOCUMENTS)
                }
            }
        }

        binding.furnitureChoose.apply {
            isChecked = items.contains(DeclareItemType.FURNITURE)
            if (isChecked) {
                binding.furnitureDetails.apply {
                    visibility = View.VISIBLE
                    setText(items[DeclareItemType.FURNITURE])
                }
            }
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.furnitureDetails.visibility = View.VISIBLE
                    items[DeclareItemType.FURNITURE] = ""
                } else {
                    binding.furnitureDetails.visibility = View.GONE
                    binding.furnitureDetails.setText("")
                    removeItems(DeclareItemType.FURNITURE)
                }
            }
        }

        binding.sportAndRecreationalChoose.apply {
            isChecked = items.contains(DeclareItemType.SPORT_AND_RECREATIONAL)
            if (isChecked) {
                binding.sportAndRecreationalDetails.apply {
                    visibility = View.VISIBLE
                    setText(items[DeclareItemType.SPORT_AND_RECREATIONAL])
                }
            }
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.sportAndRecreationalDetails.visibility = View.VISIBLE
                    items[DeclareItemType.SPORT_AND_RECREATIONAL] = ""
                } else {
                    binding.sportAndRecreationalDetails.visibility = View.GONE
                    binding.sportAndRecreationalDetails.setText("")
                    removeItems(DeclareItemType.SPORT_AND_RECREATIONAL)
                }
            }
        }

        binding.applianceChoose.apply {
            isChecked = items.contains(DeclareItemType.APPLIANCE)
            if (isChecked) {
                binding.applianceDetails.apply {
                    visibility = View.VISIBLE
                    setText(items[DeclareItemType.APPLIANCE])
                }
            }
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.applianceDetails.visibility = View.VISIBLE
                    items[DeclareItemType.APPLIANCE] = ""
                } else {
                    binding.applianceDetails.visibility = View.GONE
                    binding.applianceDetails.setText("")
                    removeItems(DeclareItemType.APPLIANCE)
                }
            }
        }

        binding.necessaryChoose.apply {
            isChecked = items.contains(DeclareItemType.DAILY_NECESSARY)
            if (isChecked) {
                binding.necessaryDetails.apply {
                    visibility = View.VISIBLE
                    setText(items[DeclareItemType.DAILY_NECESSARY])
                }
            }
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.necessaryDetails.visibility = View.VISIBLE
                    items[DeclareItemType.DAILY_NECESSARY] = ""
                } else {
                    binding.necessaryDetails.visibility = View.GONE
                    binding.necessaryDetails.setText("")
                    removeItems(DeclareItemType.DAILY_NECESSARY)
                }
            }
        }

        binding.mementosChoose.apply {
            isChecked = items.contains(DeclareItemType.MEMENTOS)
            if (isChecked) {
                binding.mementosDetails.apply {
                    visibility = View.VISIBLE
                    setText(items[DeclareItemType.MEMENTOS])
                }
            }
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked) {
                    binding.mementosDetails.visibility = View.VISIBLE
                    items[DeclareItemType.MEMENTOS] = ""
                } else {
                    binding.mementosDetails.visibility = View.GONE
                    binding.mementosDetails.setText("")
                    removeItems(DeclareItemType.MEMENTOS)
                }
            }
        }

        binding.otherChoose.apply {
            isChecked = items.contains(DeclareItemType.OTHERS)
            if (isChecked) {
                binding.otherDetails.apply {
                    visibility = View.VISIBLE
                  setText(items[DeclareItemType.OTHERS])
                }
            }
            setOnCheckedChangeListener {
                    _, isChecked ->
                if (isChecked && binding.otherDetails.text.toString() == "") {
                    binding.otherDetails.visibility = View.VISIBLE
                    items[DeclareItemType.OTHERS] = ""
                    binding.doneButton.isEnabled = false
                } else {
                    binding.otherDetails.visibility = View.GONE
                    binding.otherDetails.setText("")
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
            maxWidth = binding.clothingBox1.width
            addTextChangedListener {
                items[DeclareItemType.ClOTHING] = it.toString()
            }
        }

        binding.documentsDetails.apply {
            maxWidth = binding.documentsBox1.width
            addTextChangedListener {
                items[DeclareItemType.BOOKS_AND_DOCUMENTS] = it.toString()
            }
        }

        binding.furnitureDetails.apply {
            maxWidth = binding.furnitureBox1.width
            addTextChangedListener {
                items[DeclareItemType.FURNITURE] = it.toString()
            }
        }

        binding.sportAndRecreationalDetails.apply {
            maxWidth = binding.sportAndRecreationalBox1.width
            addTextChangedListener {
                items[DeclareItemType.SPORT_AND_RECREATIONAL] = it.toString()
            }
        }

        binding.applianceDetails.apply {
            maxWidth = binding.applianceBox1.width
            addTextChangedListener {
                items[DeclareItemType.APPLIANCE] = it.toString()
            }
        }

        binding.mementosDetails.apply {
            maxWidth = binding.mementosBox1.width
            addTextChangedListener {
                items[DeclareItemType.MEMENTOS] = it.toString()
            }
        }

        binding.necessaryDetails.apply {
            maxWidth = binding.necessaryBox1.width
            addTextChangedListener {
                items[DeclareItemType.DAILY_NECESSARY] = it.toString()
            }
        }

        binding.otherDetails.apply {
            maxWidth = binding.otherBox1.width
            addTextChangedListener {
                items[DeclareItemType.OTHERS] = it.toString()
                binding.doneButton.isEnabled =
                    !(it.toString() == "" && binding.otherChoose.isChecked)
            }
        }
    }
}