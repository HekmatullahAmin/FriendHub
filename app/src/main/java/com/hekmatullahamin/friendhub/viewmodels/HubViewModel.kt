package com.hekmatullahamin.friendhub.viewmodels

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hekmatullahamin.friendhub.R
import com.hekmatullahamin.friendhub.firestore.FireStoreHandler
import com.hekmatullahamin.friendhub.model.Comment
import com.hekmatullahamin.friendhub.model.Post
import com.hekmatullahamin.friendhub.model.User
import com.hekmatullahamin.friendhub.utils.Constants
import com.hekmatullahamin.friendhub.utils.GetCommentCallback
import com.hekmatullahamin.friendhub.utils.GetCurrentUserPostsCallback
import com.hekmatullahamin.friendhub.utils.GetPostCallback
import com.hekmatullahamin.friendhub.utils.GetUserCallback
import com.hekmatullahamin.friendhub.utils.ProgressBarUtil
import com.hekmatullahamin.friendhub.utils.UpdatePostCallback
import com.hekmatullahamin.friendhub.utils.UploadCommentCallback
import com.hekmatullahamin.friendhub.utils.UploadPostImageCallback
import com.hekmatullahamin.friendhub.utils.UploadPostWithCaptionCallback
import kotlinx.coroutines.launch

class HubViewModel : ViewModel(), GetUserCallback, UploadPostImageCallback,
    UploadPostWithCaptionCallback, GetPostCallback, UploadCommentCallback, GetCommentCallback,
    UpdatePostCallback, GetCurrentUserPostsCallback {

    init {
//        We get user for once when it is called in home fragment. we will need later in our other fragments
//        in addPostFragment to upload this user with the post, and the home to populate views
        getTheCurrentUser()

        getCurrentUserPosts()
    }

    //    we need this to load profile fragment with it, when upload post add this as one of Post data field,
//    and when make a comment add this user as someone who give comment
    private var _currentUser: MutableLiveData<User> = MutableLiveData()
    val currentUser: LiveData<User> get() = _currentUser

    //    To use in profile fragment, and if user is fetched successfully or not from firestore display sth
    private var _currentUserSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val currentUserSuccess: LiveData<Boolean> get() = _currentUserSuccess

    //    we set the post Image in our viewmodel class to bind it with the postImageView of FragmentAddPost
    private var _newPost: MutableLiveData<Uri> = MutableLiveData()
    val newPost: LiveData<Uri> get() = _newPost

    //    we set the new image which is selected to post and that one we will store in firestore
    fun setNewImageOfPost(postUri: Uri) {
        _newPost.value = postUri
    }

    //    If our post is successfully upload to firstore then observe it in AddPostFragment
    private var _postWithCaptionSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val postWithCaptionSuccess: LiveData<Boolean> get() = _postWithCaptionSuccess


    //    we will use it in our homeFragment for our recycler view to use it as data
    private var _postsList: MutableLiveData<List<Post>?> = MutableLiveData()
    val postList: MutableLiveData<List<Post>?> get() = _postsList

    //    we will use it in our profile fragment for our recyclerview to use it as data
    private var _currentUserPostList: MutableLiveData<List<Post>> = MutableLiveData()
    val currentUserPostList: LiveData<List<Post>> get() = _currentUserPostList

    //    To observe it in homeFragment and accordingly dismis bottomSheetDialog or a Toast message
    private var _postUpdateSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val postUpdateSuccess: LiveData<Boolean> get() = _postUpdateSuccess

    //    to set caption later on used to upload it to our firebase storage
    private var caption: MutableLiveData<String> = MutableLiveData()
    fun setCaption(postCaption: String) {
        caption.value = postCaption
    }

    //    we set the current post which person give comment or liked and will increase the count of the post
    //    comments or likes by one when
    private var currentClickedPost: Post = Post()
    fun setCurrentClickedPost(post: Post) {
        currentClickedPost = post
    }

    //    use this in our Bottom Sheet RecyclerView in its xml file
    private var _commentList: MutableLiveData<List<Comment>> = MutableLiveData()
    val commentList: LiveData<List<Comment>> get() = _commentList

    private lateinit var context: Context

    //    when calls update post we use this that what user clicked like or comment if any then set it to that
    //    and increment that count
    private var shouldUpdateComment: Boolean = true
    fun setUpdatedCommentOrLike(updateComment: Boolean) {
        shouldUpdateComment = updateComment
    }

    //    if is set to true then dilike the post and remove from Post's' likedUserIdsList userId
    private var isLikedAlreadyClicked: Boolean = false
    fun setIsLickedClicked(isClicked: Boolean) {
        isLikedAlreadyClicked = isClicked
    }

    //    we receive the current user signed in in our app
    private fun getTheCurrentUser() {
        val fireStoreHandler = FireStoreHandler.getFireStoreHandler()
        fireStoreHandler.setGetUserCallback(this@HubViewModel)
        viewModelScope.launch {
            fireStoreHandler.getCurrentUser(fireStoreHandler.getCurrentUserId())
        }
    }


    //    To check if caption of our post or comment of our bottom sheet dialog is empty or not
    fun isFieldEmpty(text: String): Boolean = TextUtils.isEmpty(text)

    //    First we upload post to storage and then upload the downladable uri alogn with other details in firestore
    fun uploadPost(context: Context) {
        ProgressBarUtil.showProgressBar(
            context, context.resources.getString(R.string.progress_bar_uploading)
        )
        val fireStoreHandler = FireStoreHandler.getFireStoreHandler()
        fireStoreHandler.setUploadPostCallback(this)
        viewModelScope.launch {
            fireStoreHandler.uploadPostImageToStorage(newPost.value!!)
        }
    }

    //    after successfully upload of postImage in storage this will be called to upload caption and other details
    private fun uploadPostImageWithCaptionToFirestore(postUri: Uri) {
        val fireStoreHandler = FireStoreHandler.getFireStoreHandler()
        fireStoreHandler.setUploadPostWithCaptionCallback(this)
        viewModelScope.launch {
            fireStoreHandler.uploadPostWithCaptionToFirestore(
                postUri.toString(), caption.value!!, currentUser.value!!
            )
        }
    }

    //    To receive all post which made and populate the Home fragment
    fun getAllPosts(ctx: Context) {
        context = ctx
        ProgressBarUtil.showProgressBar(
            ctx, context.getString(R.string.progress_bar_loading)
        )
        val fireStoreHandler = FireStoreHandler.getFireStoreHandler()
        fireStoreHandler.setGetPostCallback(this)
        viewModelScope.launch { fireStoreHandler.getPostList() }
    }

    fun uploadCommentToFirestore(comment: Comment) {
//        by setting user to current user(logged in user) we mean that this user has commented
//        not the the one who posted this post
        comment.user = currentUser.value!!
        val fireStoreHandler = FireStoreHandler.getFireStoreHandler()
        fireStoreHandler.setUploadCommentCallback(this)
        viewModelScope.launch { fireStoreHandler.uploadCommentToFirestore(comment) }
    }

    fun getPostComments(context: Context, postId: String) {
        ProgressBarUtil.showProgressBar(
            context, context.getString(R.string.progress_bar_loading)
        )
        val fireStoreHandler = FireStoreHandler.getFireStoreHandler()
        fireStoreHandler.setGetCommentsCallBack(this)
        viewModelScope.launch { fireStoreHandler.getPostComments(postId) }
    }

    //    update the post comment or like
    fun updatePost() {
        val postHashMap: HashMap<String, Any> = hashMapOf()
        currentClickedPost.apply {
            postHashMap[Constants.POST_ID] = postId
            postHashMap[Constants.POST_CAPTION] = postCaption
            postHashMap[Constants.POST_IMAGE] = postImage
            postHashMap[Constants.USER] = user
            if (shouldUpdateComment) {
//                if user clicked comment then bring change only to comments
                postCommentsCount++
            } else {
//                if user clicked like then bring change only to like
                val likedUserIdsList: ArrayList<String> = currentClickedPost.likedUserIdsList
                val currentUserId = FireStoreHandler.getFireStoreHandler().getCurrentUserId()
//                if our post is already liked by user then dislike it and remove user id from list of likes
//                otherwise add it
                if (isLikedAlreadyClicked) {
                    postLikesCount--
                    likedUserIdsList.remove(currentUserId)
                } else {
                    postLikesCount++
                    likedUserIdsList.add(currentUserId)
                }
            }
            postHashMap[Constants.POST_LIKED_USER_IDS_LISTS] = likedUserIdsList
            postHashMap[Constants.POST_LIKES_COUNT] = postLikesCount
            postHashMap[Constants.POST_COMMENTS_COUNT] = postCommentsCount
        }
        val fireStoreHandler = FireStoreHandler.getFireStoreHandler()
        fireStoreHandler.setUpdatePostCallBack(this)
        viewModelScope.launch { fireStoreHandler.updatePost(postHashMap) }
    }

    //    to fetch those posts from firestore that this user posted or created
    fun getCurrentUserPosts() {
        val fireStoreHandler = FireStoreHandler.getFireStoreHandler()
        fireStoreHandler.setGetCurrentUserPostCallback(this)
        viewModelScope.launch { fireStoreHandler.getSpecificUserPosts() }
    }

    //    to fetch those posts from firestore that this user liked
    fun getCurrentUserLikedPosts() {
        val fireStoreHandler = FireStoreHandler.getFireStoreHandler()
        fireStoreHandler.setGetCurrentUserPostCallback(this)
        viewModelScope.launch { fireStoreHandler.getSpecificUserLikedPosts(fireStoreHandler.getCurrentUserId()) }
    }

    //    If user is fetched successfully from firestore then act accordingly
    override fun onGetUserSuccess(user: User) {
        _currentUserSuccess.value = true
        _currentUser.value = user
    }

    override fun onGetUserFailure() {
        _currentUserSuccess.value = false
    }

    //    If the post image is uploaded successfully in storage then call other function to upload other details
    override fun onUploadPostImageSuccess(postUri: Uri) {
        uploadPostImageWithCaptionToFirestore(postUri)
    }

    override fun onUploadPostImageFailure() {
        ProgressBarUtil.hideProgressBar()
    }

    //    when post image is uploaded successfully and then other details are stored successfully in firestore
//    then set _postWithCaptionSuccess to true to observe it in fragment
    override fun onUploadPostWithCaptionSuccess() {
        _postWithCaptionSuccess.value = true
    }

    override fun onUploadPostWithCaptionFailure() {
        _postWithCaptionSuccess.value = false
    }

    //    When receiving all of our post successfully then assing the postList value to one which received from firestore
//    and populate the home recyclerview
    override fun onGetPostSuccess(postList: List<Post>) {
        ProgressBarUtil.hideProgressBar()
        _postsList.value = postList
    }

    override fun onGetPostFailure() {
        ProgressBarUtil.hideProgressBar()
    }

    //    If our comment is successfully saved then call other function to update that post comments count to increment it
    override fun onCommentUploadSuccess() {
        updatePost()
    }

    override fun onCommentUploadFailure() {
        _postUpdateSuccess.value = false
    }

    //    When receiving all comments of the specific post successfully then set commentList value and then populate our
//    bottom sheet dialog recyclerview
    override fun onGetCommentsSuccess(comments: List<Comment>) {
        ProgressBarUtil.hideProgressBar()
        _commentList.value = comments
    }

    override fun onGetCommentsFailure() {
        ProgressBarUtil.hideProgressBar()
    }

    //    after that our post updated successfully then set _commentUploadSuccess to true or false then observe it
    override fun onPostUpdateSuccess() {
        _postUpdateSuccess.value = true
    }

    override fun onPostUpdateFailure() {
        _postUpdateSuccess.value = false
    }

    //    after that we successfully recieve current user posts then populate profile fragment recycler view
    override fun onGetCurrentUserPostsSuccess(postList: List<Post>) {
        _currentUserPostList.value = postList
    }

    override fun onGetCurrentUserPostsFailure() {
        ProgressBarUtil.hideProgressBar()
    }

    //    after that we successfully recieve current user liked posts then populate profile fragment recycler view
//    override fun onGetCurrentUserLikedPostsSuccess(postList: List<Post>) {
//        _currentUserPostList.value = postList
//    }
//
//    override fun onGetCurrentUserLikedPostsFailure() {
//        ProgressBarUtil.hideProgressBar()
//    }
}