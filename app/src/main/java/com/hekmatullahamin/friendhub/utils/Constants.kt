package com.hekmatullahamin.friendhub.utils

import com.hekmatullahamin.friendhub.model.User

object Constants {
    //    firebase collections
    const val USERS = "users"
    const val POSTS = "posts"
    const val COMMENT = "comment"

    //    user data fields
    const val USER_ID = "userId"
    const val USER_NAME = "userName"
    const val USER_EMAIL = "userEmail"
    const val USER_PROFILE = "userProfile"
    const val USER_BIO = "userBio"

    //   intent extra  and post user data field
    const val USER = "user"

    //    Firebase Storage references
    const val PROFILE_PICTURES = "profile_pictures"

    //    to only receive comments which belongs to this post only FireStoreHandler class getPostComments
//    and also post data field
    const val POST_ID = "postId"

    //    Post other data fields
    const val POST_CAPTION = "postCaption"
    const val POST_IMAGE = "postImage"
    const val POST_LIKES_COUNT = "postLikesCount"
    const val POST_COMMENTS_COUNT = "postCommentsCount"
    const val POST_LIKED_USER_IDS_LISTS = "likedUserIdsList"

    //    to access the user's userId of firestore, use this in getCurrentUserPosts functions
    const val USER_USER_ID = "user.userId"

}