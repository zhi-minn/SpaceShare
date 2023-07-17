package com.example.spaceshare.ui.view

import MessageAdapter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spaceshare.R
import com.example.spaceshare.adapters.ScrollToBottomObserver
import com.example.spaceshare.databinding.DialogChatBinding
import com.example.spaceshare.models.Chat
import com.example.spaceshare.models.Message
import com.example.spaceshare.ui.viewmodel.ChatViewModel
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatDialogFragment(
    private val chat : Chat
) : DialogFragment() {

    @Inject
    lateinit var chatViewModel: ChatViewModel

    private lateinit var binding: DialogChatBinding
    private lateinit var navController: NavController
    private lateinit var adapter: MessageAdapter
    private lateinit var manager: LinearLayoutManager

    @RequiresApi(Build.VERSION_CODES.O)
    private val openDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {uri ->
            chatViewModel.sendImageMessage(uri, requireActivity())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.SearchAndFilterDialogStyle)
        chatViewModel.setChatDBRef(chat.id)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_chat, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = requireActivity().findNavController(R.id.main_nav_host_fragment)

        configureRecyclerView()
        configureButtons()
    }

    private fun configureRecyclerView() {
        val options = FirebaseRecyclerOptions.Builder<Message>()
            .setQuery(chatViewModel.getChatDBRef(), Message::class.java)
            .build()
        adapter = MessageAdapter(options, chatViewModel.getCurrentUsername())
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

    @RequiresApi(Build.VERSION_CODES.O)
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
            chatViewModel.sendMessage(textContent)
            binding.messageEditText.setText("")
        }

        // When the image button is clicked, launch the image picker
        binding.addMessageImageView.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }
    }
}