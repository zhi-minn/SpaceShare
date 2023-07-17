package com.example.spaceshare.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceshare.databinding.ChatItemBinding
import com.example.spaceshare.models.Chat

class ChatAdapter(
    private val itemClickListener: ItemClickListener
) : ListAdapter<Chat, ChatAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChatItemBinding.inflate(inflater, parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = getItem(position)
        holder.bind(chat)
    }

    inner class ViewHolder(private val binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chat: Chat) {
            // Bind the chat data to the views
            binding.chatItemTitle.text = chat.title ?: "Untitled"
            binding.chatLastMessage.text = chat.lastMessage

            // Set click listeners
            binding.chatItemContainer.setOnClickListener {
                itemClickListener.onItemClick(chat)
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(chat: Chat)
    }

    private class DiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }
}