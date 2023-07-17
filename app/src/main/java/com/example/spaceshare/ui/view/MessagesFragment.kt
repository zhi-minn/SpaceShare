package com.example.spaceshare.ui.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.spaceshare.ui.viewmodel.MessagesViewModel
import com.example.spaceshare.R
import MessageAdapter
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceshare.adapters.ScrollToBottomObserver
import com.example.spaceshare.databinding.FragmentMessagesBinding
import com.example.spaceshare.models.Message
import com.example.spaceshare.ui.viewmodel.ProfileViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessagesFragment : Fragment() {

    companion object {
        fun newInstance() = MessagesFragment()
    }

    @Inject
    lateinit var messagesViewModel: MessagesViewModel

    private lateinit var adapter: MessageAdapter

    private lateinit var navController: NavController
    private lateinit var binding: FragmentMessagesBinding
    private lateinit var manager: LinearLayoutManager

    private val openDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {uri ->
            messagesViewModel.sendImageMessage(uri, requireActivity())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_messages, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)

        configureRecyclerView()
        configureButtons()

    }

    private fun configureRecyclerView() {
        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(messagesViewModel.getMessagesDBref(), Message::class.java)
            .build()
        adapter = MessageAdapter(options, FirebaseAuth.getInstance().currentUser!!.displayName)
        binding.progressBar.visibility = ProgressBar.INVISIBLE
        manager = LinearLayoutManager(context)
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager
        binding.messageRecyclerView.adapter = adapter

        // Scroll down when a new message arrives
        adapter.registerAdapterDataObserver(
            ScrollToBottomObserver(binding.messageRecyclerView, adapter, manager)
        )

        adapter.startListening()
    }

    private fun configureButtons() {
        // Disable the send button when there's no text in the input field
        binding.messageEditText.doOnTextChanged { text, start, before, count ->
            if (text.toString().trim().isNotEmpty()) {
                binding.sendButton.isEnabled = true
                binding.sendButton.setImageResource(R.drawable.outline_send_24)
            } else {
                binding.sendButton.isEnabled = false
                binding.sendButton.setImageResource(R.drawable.outline_send_gray_24)
            }
        }

        // When the send button is clicked, send a text message
        binding.sendButton.setOnClickListener {

            val textContent = binding.messageEditText.text.toString()
            messagesViewModel.sendMessage(textContent)
            binding.messageEditText.setText("")
        }

        // When the image button is clicked, launch the image picker
        binding.addMessageImageView.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }
    }
}