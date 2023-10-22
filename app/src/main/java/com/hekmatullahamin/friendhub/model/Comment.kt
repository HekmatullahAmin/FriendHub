package com.hekmatullahamin.friendhub.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    var commentId: String = "",
    val commentText: String = "",
    val postId: String = "",
    var user: User = User()
) : Parcelable
