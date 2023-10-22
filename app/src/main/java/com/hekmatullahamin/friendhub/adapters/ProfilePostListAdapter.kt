package com.hekmatullahamin.friendhub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hekmatullahamin.friendhub.databinding.CustomCurrentUserPostsLayoutBinding
import com.hekmatullahamin.friendhub.model.Post

class ProfilePostListAdapter :
    ListAdapter<Post, ProfilePostListAdapter.PostViewHolder>(DiffCallBack) {
    class PostViewHolder(val binding: CustomCurrentUserPostsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.post = post
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layout = CustomCurrentUserPostsLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }

    companion object {
        val DiffCallBack = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.postId == newItem.postId
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return (oldItem.postCaption == newItem.postCaption && oldItem.postImage == newItem.postImage &&
                        oldItem.postCommentsCount == newItem.postCommentsCount &&
                        oldItem.postLikesCount == newItem.postLikesCount &&
                        oldItem.user == newItem.user)
            }

        }
    }
}