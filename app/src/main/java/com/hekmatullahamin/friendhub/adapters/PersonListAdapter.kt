package com.hekmatullahamin.friendhub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hekmatullahamin.friendhub.databinding.CustomPersonRowLayoutBinding
import com.hekmatullahamin.friendhub.model.Post
import com.hekmatullahamin.friendhub.model.User

class PersonListAdapter(val onItemClicked: (User) -> Unit) :
    ListAdapter<User, PersonListAdapter.PersonViewHolder>(DiffCallBack) {
    class PersonViewHolder(val binding: CustomPersonRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonViewHolder {
        return PersonViewHolder(
            CustomPersonRowLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PersonViewHolder, position: Int) {
        val user = getItem(position)
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onItemClicked(user)
        }
    }

    companion object {
        val DiffCallBack = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return (oldItem.userName == newItem.userName && oldItem.userBio == newItem.userBio &&
                        oldItem.userEmail == newItem.userEmail && oldItem.userProfile == newItem.userProfile &&
                        oldItem.postsCount == newItem.postsCount && oldItem.followersCount == newItem.followersCount &&
                        oldItem.followingCount == newItem.followingCount)
            }
        }
    }
}