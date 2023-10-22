package com.hekmatullahamin.friendhub.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hekmatullahamin.friendhub.databinding.CustomCommentRowLayoutBinding
import com.hekmatullahamin.friendhub.databinding.CustomCommentsBottomSheetDialogBinding
import com.hekmatullahamin.friendhub.model.Comment

class CommentListAdapter :
    ListAdapter<Comment, CommentListAdapter.CommentViewHolder>(DiffCallback) {
    class CommentViewHolder(val binding: CustomCommentRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(theComment: Comment) {
            binding.comment = theComment
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val layout = CustomCommentRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentViewHolder(layout)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment)
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem.commentId == newItem.commentId
            }

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return (oldItem.commentText == newItem.commentText &&
                        oldItem.user == newItem.user)
            }
        }
    }
}