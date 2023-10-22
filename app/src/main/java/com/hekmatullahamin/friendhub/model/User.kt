package com.hekmatullahamin.friendhub.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val userId: String,
    var userName: String,
    val userEmail: String,
    var userProfile: String = "",
    var userBio: String = "",
    var postsCount: Int = 0,
    var followersCount: Int = 0,
    var followingCount: Int = 0,
) : Parcelable {

    // Add a no-argument constructor
    constructor() : this("", "", ""/* other default values */)

}
