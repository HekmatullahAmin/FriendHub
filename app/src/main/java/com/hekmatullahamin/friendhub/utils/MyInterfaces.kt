package com.hekmatullahamin.friendhub.utils

import android.net.Uri
import com.hekmatullahamin.friendhub.model.Comment
import com.hekmatullahamin.friendhub.model.Post
import com.hekmatullahamin.friendhub.model.User


interface GetUserCallback {
    fun onGetUserSuccess(user: User)
    fun onGetUserFailure()
}

interface RegistrationCallBack {
    fun onRegistrationSuccess()
    fun onRegistrationFailure()
}

interface UpdateCallBack {
    fun onUpdateSuccess()
    fun onUpdateFailed()
}

interface UploadProfilePictureCallback {
    fun onUploadSuccess(imageUri: Uri)
    fun onUploadFailure()
}

interface UploadPostImageCallback {
    fun onUploadPostImageSuccess(postUri: Uri)
    fun onUploadPostImageFailure()
}

interface UploadPostWithCaptionCallback {
    fun onUploadPostWithCaptionSuccess()
    fun onUploadPostWithCaptionFailure()
}

interface GetPostCallback {
    fun onGetPostSuccess(postList: List<Post>)
    fun onGetPostFailure()
}

interface GetCurrentUserPostsCallback {
    fun onGetCurrentUserPostsSuccess(postList: List<Post>)
    fun onGetCurrentUserPostsFailure()
}
interface UploadCommentCallback {
    fun onCommentUploadSuccess()
    fun onCommentUploadFailure()
}

interface GetCommentCallback {
    fun onGetCommentsSuccess(comments: List<Comment>)
    fun onGetCommentsFailure()
}

interface UpdatePostCallback {
    fun onPostUpdateSuccess()
    fun onPostUpdateFailure()
}

interface GetAllUserCallback {
    fun onGetUsersSuccess(user: List<User>)
    fun onGetUserFailure()
}