package com.example.spaceshare.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spaceshare.R
import com.example.spaceshare.models.User

class UserAdapter(private val userList: ArrayList<User>, private val itemClickListener: ItemClickListener):
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = userList[position]

        val idStr = "ID: ${currentItem.id}"
        val emailStr = "Email: ${currentItem.email}"
        val firstNameStr = "Name: ${currentItem.firstName}"

        holder.idView.text = idStr
        holder.emailView.text = emailStr
        holder.firstNameView.text = firstNameStr
        holder.lastNameView.text = currentItem.lastName
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                if (bindingAdapterPosition >= 0) {
                    itemClickListener.onItemClick(bindingAdapterPosition)
                }
            }
        }
        val idView: TextView = itemView.findViewById(R.id.userId)
        val emailView: TextView = itemView.findViewById(R.id.userEmail)
        val firstNameView: TextView = itemView.findViewById(R.id.userFirstName)
        val lastNameView: TextView = itemView.findViewById(R.id.userLastName)
    }
}