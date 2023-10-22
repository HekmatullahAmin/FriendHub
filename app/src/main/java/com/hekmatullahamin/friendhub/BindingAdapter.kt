package com.hekmatullahamin.friendhub

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.hekmatullahamin.friendhub.adapters.CommentListAdapter
import com.hekmatullahamin.friendhub.adapters.PersonListAdapter
import com.hekmatullahamin.friendhub.adapters.PostListAdapter
import com.hekmatullahamin.friendhub.adapters.ProfilePostListAdapter
import com.hekmatullahamin.friendhub.model.Comment
import com.hekmatullahamin.friendhub.model.Post
import com.hekmatullahamin.friendhub.model.User
import de.hdodenhof.circleimageview.CircleImageView

@BindingAdapter("loadProfileImage")
fun loadProfileImage(imageView: CircleImageView, imageUri: Any?) {

    imageUri?.let {
        Glide.with(imageView.rootView)
            .load(imageUri)
            .error(R.drawable.broken_image)
            .into(imageView)
    }
}

@BindingAdapter("loadImage")
fun loadImage(imageView: ImageView, imageUri: Any?) {

    imageUri?.let {
        Glide.with(imageView.rootView)
            .load(imageUri)
            .error(R.drawable.broken_image)
            .into(imageView)
    }
}

@BindingAdapter("postsRecyclerViewData")
fun bindPostsRecyclerView(recyclerView: RecyclerView, listData: List<Post>?) {
    val adapter = recyclerView.adapter as PostListAdapter
    listData?.let { adapter.submitList(listData) }
}

@BindingAdapter("commentsRecyclerViewData")
fun bindCommentsRecyclerView(recyclerView: RecyclerView, listData: List<Comment>?) {
    val adapter = recyclerView.adapter as CommentListAdapter
    listData?.let { adapter.submitList(listData) }
}

@BindingAdapter("profilePostsRecyclerViewData")
fun bindProfilePostsRecyclerView(recyclerView: RecyclerView, listData: List<Post>?) {
    val adapter = recyclerView.adapter as ProfilePostListAdapter
    listData?.let { adapter.submitList(listData) }
}


@BindingAdapter("setText")
fun setETText(editText: TextInputEditText, text: String) {
    editText.setText(text)
}

@BindingAdapter("personRecyclerViewData")
fun bindPersonNamesRecyclerView(recyclerView: RecyclerView, listData: List<User>?) {
    val adapter = recyclerView.adapter as PersonListAdapter
    listData?.let { adapter.submitList(listData) }
}

@BindingAdapter(value = ["likedUserIdsList", "currentUserId"], requireAll = true)
fun setIcon(button: MaterialButton, likedUserIdsList: ArrayList<String>?, currentUserId: String?) {
    if (currentUserId != null && likedUserIdsList != null) {
        button.iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        if (likedUserIdsList.contains(currentUserId)) {
            button.setIconResource(R.drawable.liked)
        } else {
            button.setIconResource(R.drawable.like)
        }
    }
}