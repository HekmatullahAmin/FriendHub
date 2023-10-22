package com.hekmatullahamin.friendhub.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    var postId: String = "",
    val postCaption: String = "",
    val postImage: String = "",
    val user: User = User(),
    var postLikesCount: Int = 0,
    var postCommentsCount: Int = 0,
    var likedUserIdsList: ArrayList<String> = arrayListOf()

) : Parcelable
