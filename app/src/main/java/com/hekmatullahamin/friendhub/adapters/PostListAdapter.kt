package com.hekmatullahamin.friendhub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hekmatullahamin.friendhub.R
import com.hekmatullahamin.friendhub.databinding.CustomPostRowLayoutBinding
import com.hekmatullahamin.friendhub.model.Post

class PostListAdapter(
    private val userId: String,
    val onLikeClicked: (Post) -> Unit,
    val onCommentClicked: (Post) -> Unit
) :
    ListAdapter<Post, PostListAdapter.PostViewHolder>(DiffCallback) {
    class PostViewHolder(private val binding: CustomPostRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(thePost: Post, userId: String) {
            binding.apply {
                post = thePost
                currentUserId = userId
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val layout = CustomPostRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PostViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post: Post = getItem(position)
        holder.bind(post, userId)
        holder.itemView.findViewById<Button>(R.id.likeBtn).setOnClickListener {
            onLikeClicked(post)
        }
        holder.itemView.findViewById<Button>(R.id.commentBtn).setOnClickListener {
            onCommentClicked(post)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.postId == newItem.postId
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return (oldItem.postImage == newItem.postImage && oldItem.postCaption == newItem.postCaption && oldItem.postLikesCount == newItem.postLikesCount && oldItem.postCommentsCount == newItem.postCommentsCount)
            }
        }
    }
}