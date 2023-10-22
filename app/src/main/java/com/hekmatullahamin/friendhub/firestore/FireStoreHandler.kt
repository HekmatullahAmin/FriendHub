package com.hekmatullahamin.friendhub.firestore

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.hekmatullahamin.friendhub.model.Comment
import com.hekmatullahamin.friendhub.model.Post
import com.hekmatullahamin.friendhub.model.User
import com.hekmatullahamin.friendhub.utils.Constants
import com.hekmatullahamin.friendhub.utils.GetAllUserCallback
import com.hekmatullahamin.friendhub.utils.GetCommentCallback
import com.hekmatullahamin.friendhub.utils.GetCurrentUserPostsCallback
import com.hekmatullahamin.friendhub.utils.GetPostCallback
import com.hekmatullahamin.friendhub.utils.GetUserCallback
import com.hekmatullahamin.friendhub.utils.ProgressBarUtil
import com.hekmatullahamin.friendhub.utils.RegistrationCallBack
import com.hekmatullahamin.friendhub.utils.UpdateCallBack
import com.hekmatullahamin.friendhub.utils.UpdatePostCallback
import com.hekmatullahamin.friendhub.utils.UploadCommentCallback
import com.hekmatullahamin.friendhub.utils.UploadPostImageCallback
import com.hekmatullahamin.friendhub.utils.UploadPostWithCaptionCallback
import com.hekmatullahamin.friendhub.utils.UploadProfilePictureCallback
import java.lang.reflect.Array

class FireStoreHandler {
    private val _firebaseAuth: FirebaseAuth = Firebase.auth
    val firebaseAuth: FirebaseAuth get() = _firebaseAuth
    private val firebaseFireStore: FirebaseFirestore = Firebase.firestore

    //    val firebaseFireStore: FirebaseFirestore get() = _firebaseFireStore
    private val firebaseStorage: FirebaseStorage = Firebase.storage
//    val firebaseStorage: FirebaseStorage get() = _firebaseStorage


    //    to use it when request register user and override the functions which is implemented in authenticationViewModel
    private var registrationCallBack: RegistrationCallBack? = null
    fun setRegistrationCallback(callBack: RegistrationCallBack) {
        registrationCallBack = callBack
    }

    //    to use it when request to update user profile and override the functions which is implemented in EditProfileViewModel
    private var updateCallBack: UpdateCallBack? = null
    fun setUpdateCallBack(callBack: UpdateCallBack) {
        updateCallBack = callBack
    }

    //    to use it when request to get user and override the functions which is implemented in HubviewModel
    private var getUserCallBack: GetUserCallback? = null
    fun setGetUserCallback(callback: GetUserCallback) {
        getUserCallBack = callback
    }

    private var uploadProfileCallBack: UploadProfilePictureCallback? = null
    fun setUploadProfileCallBack(callback: UploadProfilePictureCallback) {
        uploadProfileCallBack = callback
    }

    private var uploadPostImageCallBack: UploadPostImageCallback? = null
    fun setUploadPostCallback(callback: UploadPostImageCallback) {
        uploadPostImageCallBack = callback
    }

    private var uploadPostWithCaptionCallback: UploadPostWithCaptionCallback? = null
    fun setUploadPostWithCaptionCallback(callback: UploadPostWithCaptionCallback) {
        uploadPostWithCaptionCallback = callback
    }

    private var getPostCallBack: GetPostCallback? = null
    fun setGetPostCallback(callback: GetPostCallback) {
        getPostCallBack = callback
    }

    private var uploadCommentCallBack: UploadCommentCallback? = null
    fun setUploadCommentCallback(callback: UploadCommentCallback) {
        uploadCommentCallBack = callback
    }

    private var getCommentsCallBack: GetCommentCallback? = null
    fun setGetCommentsCallBack(callback: GetCommentCallback) {
        getCommentsCallBack = callback
    }

    private var updatePostCallBack: UpdatePostCallback? = null
    fun setUpdatePostCallBack(callBack: UpdatePostCallback) {
        updatePostCallBack = callBack
    }

    private var getCurrentUserPostsCallBack: GetCurrentUserPostsCallback? = null
    fun setGetCurrentUserPostCallback(callback: GetCurrentUserPostsCallback) {
        getCurrentUserPostsCallBack = callback
    }

    private var getAllUsersCallBack: GetAllUserCallback? = null
    fun setGetAllUsersCallBack(callback: GetAllUserCallback) {
        getAllUsersCallBack = callback
    }

    companion object {
        @Volatile
        private var INSTANCE: FireStoreHandler? = null
        fun getFireStoreHandler(): FireStoreHandler {
            return INSTANCE ?: synchronized(this) {
                FireStoreHandler()
            }
        }
    }

    fun getCurrentUserId(): String {
        val currentUser = _firebaseAuth.currentUser
        var currentUserId: String = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId
    }

    fun getCurrentUser(currentUserId: String) {
        firebaseFireStore.collection(Constants.USERS).document(currentUserId).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                getUserCallBack?.onGetUserSuccess(user!!)
            }.addOnFailureListener {
                getUserCallBack?.onGetUserFailure()
            }
    }

    fun getAllUsers() {
        firebaseFireStore.collection(Constants.USERS)
            .get()
            .addOnSuccessListener { document ->
                val users = mutableListOf<User>()
                for (documentTypeUser in document.documents) {
                    val user = documentTypeUser.toObject(User::class.java)!!
                    users.add(user)
                }
                getAllUsersCallBack?.onGetUsersSuccess(users)
            }
            .addOnFailureListener {
                getAllUsersCallBack?.onGetUserFailure()
            }
    }

    fun registerUser(user: User) {
        firebaseFireStore.collection(Constants.USERS).document(user.userId)
            .set(user, SetOptions.merge()).addOnSuccessListener {
                registrationCallBack?.onRegistrationSuccess()
            }.addOnFailureListener {
                registrationCallBack?.onRegistrationFailure()
            }
    }

    fun updateUser(userHashMap: HashMap<String, Any>) {
        firebaseFireStore.collection(Constants.USERS)
            .document(userHashMap[Constants.USER_ID].toString()).update(userHashMap)
            .addOnSuccessListener {
                updateCallBack?.onUpdateSuccess()
            }.addOnFailureListener {
                updateCallBack?.onUpdateFailed()
            }
    }

    fun uploadProfilePictureToStorage(imageUri: Uri, userId: String) {
//        this is the reference to profile_picture folder
        val reference = firebaseStorage.reference.child(Constants.PROFILE_PICTURES)
//        We put this reference because whenever user update his profile picture it should substitute the older
//                one not uploading new and keep the previous profile
        val imageReference = reference.child(Constants.PROFILE_PICTURES + userId)
        imageReference.putFile(imageUri).addOnSuccessListener { taskSnapshot ->
            // The image upload is success
            // Get the downloadable url from the task snapshot
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                uploadProfileCallBack?.onUploadSuccess(downloadUri)
            }?.addOnFailureListener {
                //TODO on failure message
                uploadProfileCallBack?.onUploadFailure()
            }
        }.addOnFailureListener {
            uploadProfileCallBack?.onUploadFailure()
        }
    }

    fun uploadPostImageToStorage(postUri: Uri) {
        val reference = firebaseStorage.reference.child(Constants.POSTS)
        val imageReference = reference.child(Constants.POSTS + System.currentTimeMillis())
        imageReference.putFile(postUri).addOnSuccessListener { taskSnapshot ->
            taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { downloadUri ->
                uploadPostImageCallBack?.onUploadPostImageSuccess(downloadUri)
            }?.addOnFailureListener {
                uploadPostImageCallBack?.onUploadPostImageFailure()
            }
        }.addOnFailureListener {
            //TODO onFailure message
            uploadPostImageCallBack?.onUploadPostImageFailure()
        }
    }

    fun uploadPostWithCaptionToFirestore(postUri: String, postCaption: String, user: User) {
        firebaseFireStore.collection(Constants.POSTS)
            .add(Post(postCaption = postCaption, postImage = postUri, user = user))
            .addOnSuccessListener {
                uploadPostWithCaptionCallback?.onUploadPostWithCaptionSuccess()
            }.addOnFailureListener {
                uploadPostWithCaptionCallback?.onUploadPostWithCaptionFailure()
            }
    }

    fun updatePost(postHashMap: HashMap<String, Any>) {
        firebaseFireStore.collection(Constants.POSTS)
            .document(postHashMap[Constants.POST_ID].toString())
            .update(postHashMap)
            .addOnSuccessListener {
                updatePostCallBack?.onPostUpdateSuccess()
            }
            .addOnFailureListener {
                updatePostCallBack?.onPostUpdateFailure()
            }
    }

    fun getPostList() {
        firebaseFireStore.collection(Constants.POSTS).get().addOnSuccessListener { document ->
            val postList: ArrayList<Post> = ArrayList()
            // A for loop as per the list of documents to convert them into Products ArrayList.
            for (documentTypePost in document.documents) {
                val post = documentTypePost.toObject(Post::class.java)
                if (post != null) {
                    post.postId = documentTypePost.id
                    postList.add(post)
                }
            }
            getPostCallBack?.onGetPostSuccess(postList)
        }.addOnFailureListener {
            getPostCallBack?.onGetPostFailure()
        }
    }

    fun uploadCommentToFirestore(comment: Comment) {
        firebaseFireStore.collection(Constants.COMMENT).add(comment).addOnSuccessListener {
            uploadCommentCallBack?.onCommentUploadSuccess()
        }.addOnFailureListener {
            uploadCommentCallBack?.onCommentUploadFailure()
        }
    }

    fun getPostComments(postId: String) {
        firebaseFireStore.collection(Constants.COMMENT).whereEqualTo(Constants.POST_ID, postId)
            .get()
            .addOnSuccessListener { document ->
                val commentList: ArrayList<Comment> = ArrayList()
                for (documentTypeComment in document.documents) {
                    val comment = documentTypeComment.toObject(Comment::class.java)
                    if (comment != null) {
                        comment.commentId = documentTypeComment.id
                        commentList.add(comment)
                    }
                }
                getCommentsCallBack?.onGetCommentsSuccess(commentList)
            }
            .addOnFailureListener {
                getCommentsCallBack?.onGetCommentsFailure()
            }
    }

    fun getSpecificUserPosts() {
        firebaseFireStore.collection(Constants.POSTS)
            .whereEqualTo(Constants.USER_USER_ID, getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val list: ArrayList<Post> = ArrayList()
                for (documentTypePost in document.documents) {
                    val post = documentTypePost.toObject(Post::class.java)
                    if (post != null) {
                        list.add(post)
                    }
                }
                getCurrentUserPostsCallBack?.onGetCurrentUserPostsSuccess(list)
            }
            .addOnFailureListener {
                getCurrentUserPostsCallBack?.onGetCurrentUserPostsFailure()
            }
    }

    fun getSpecificUserLikedPosts(userId: String) {
        firebaseFireStore.collection(Constants.POSTS)
            .whereArrayContains(Constants.POST_LIKED_USER_IDS_LISTS, userId)
            .get()
            .addOnSuccessListener { document ->
                val list: ArrayList<Post> = ArrayList()
                for (documentTypePost in document.documents) {
                    val post = documentTypePost.toObject(Post::class.java)
                    if (post != null) {
                        list.add(post)
                    }
                }
                getCurrentUserPostsCallBack?.onGetCurrentUserPostsSuccess(list)
            }
            .addOnFailureListener {
                getCurrentUserPostsCallBack?.onGetCurrentUserPostsFailure()
            }
    }
}
