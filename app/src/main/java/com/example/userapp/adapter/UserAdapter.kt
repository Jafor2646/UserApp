package com.example.userapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.userapp.R
import com.example.userapp.data.entity.User
import com.example.userapp.databinding.ItemUserBinding

class UserAdapter(
    private val users: MutableList<User> = mutableListOf(),
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
            binding.executePendingBindings()

            if (!user.profilePhotoPath.isNullOrEmpty()) {
                Glide.with(binding.ivProfilePhoto.context)
                    .load(user.profilePhotoPath)
                    .placeholder(R.drawable.ic_person)
                    .into(binding.ivProfilePhoto)
            } else {
                binding.ivProfilePhoto.setImageResource(R.drawable.ic_person)
            }

            binding.root.setOnClickListener {
                onItemClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateList(newList: List<User>) {
        users.clear()
        users.addAll(newList)
        notifyDataSetChanged()
    }
}
