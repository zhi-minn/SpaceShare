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
import com.example.spaceshare.ui.viewmodel.MessagesViewModel
import com.example.spaceshare.utils.ImageLoaderUtil
import com.firebase.ui.database.FirebaseRecyclerOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Objects
import javax.inject.Inject

@AndroidEntryPoint
class ChatDialogFragment(
    private val chat: Chat,
    private val shouldRefreshChatsList: Boolean = false
) : DialogFragment() {

    @Inject
    lateinit var chatViewModel: ChatViewModel
    private lateinit var messagesViewModel: MessagesViewModel

    private lateinit var binding: DialogChatBinding
    private lateinit var navController: NavController
    private lateinit var adapter: MessageAdapter
    private lateinit var manager: LinearLayoutManager

    @RequiresApi(Build.VERSION_CODES.O)
    private val openDocument =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let { uri ->
                chatViewModel.sendImageMessage(uri, requireActivity())
                refreshParentChatsList()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.SearchAndFilterDialogStyle)

        chatViewModel.setChat(chat)

        // Set messagesViewModel to be the same one as the parent fragment's
        if (shouldRefreshChatsList) {
            messagesViewModel = (requireParentFragment() as MessagesFragment).messagesViewModel
        }
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

        configureUI()
        configureRecyclerView()
        configureButtons()
    }

    private fun configureUI() {
        binding.chatTitle.text = chat.title

        configureListingPreview()
    }

    private fun configureListingPreview() {
        if (chat.photoURL != null) {
            ImageLoaderUtil.loadImageIntoView(binding.listingImagePreview, chat.photoURL, false)
        }

        if (chat.hostId != null && chat.associatedListingId != null) {
            CoroutineScope(Dispatchers.IO).launch {
                // Execute repo tasks
                chatViewModel.getAssociatedListingFromRepo(chat.associatedListingId)
                chatViewModel.getHostUserFromRepo(chat.hostId)

                // Do UI updates
                withContext(Dispatchers.Main) {
                    binding.listingPrice.text = String.format("$%.2f CAD/day per cubic metre", chatViewModel.getAssociatedListingPrice())
                    binding.listingHost.text =
                        "Hosted by ${chatViewModel.getAssociatedListingHostName()}"
                    binding.listingLocation.text = chatViewModel.getAssociatedListingGeneralLocation()

                    binding.chatListingPreview.setOnClickListener {
                        val clientListingDialogFragment = ClientListingDialogFragment(chatViewModel.getAssociatedListing(), hideMessageHostButton = true)
                        clientListingDialogFragment.show(
                            Objects.requireNonNull(childFragmentManager),
                            "clientListingDialog")
                    }
                }
            }
        }
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
            // Grab text content and reset it back to empty first
            val textContent = binding.messageEditText.text.toString()
            binding.messageEditText.setText("")

            // Then do network requests
            chatViewModel.sendMessage(textContent)
            refreshParentChatsList()
        }

        // When the image button is clicked, launch the image picker
        binding.addMessageImageView.setOnClickListener {
            openDocument.launch(arrayOf("image/*"))
        }

        binding.btnClose.setOnClickListener {
            refreshParentChatsList()
            this.dismiss()
        }
    }

    private fun refreshParentChatsList() {
        if (shouldRefreshChatsList) {
            messagesViewModel.fetchChats()
        }
    }
}