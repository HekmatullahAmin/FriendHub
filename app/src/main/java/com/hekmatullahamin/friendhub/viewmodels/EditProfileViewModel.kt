package com.hekmatullahamin.friendhub.viewmodels

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hekmatullahamin.friendhub.R
import com.hekmatullahamin.friendhub.firestore.FireStoreHandler
import com.hekmatullahamin.friendhub.model.User
import com.hekmatullahamin.friendhub.utils.Constants
import com.hekmatullahamin.friendhub.utils.ProgressBarUtil
import com.hekmatullahamin.friendhub.utils.SnackBarUtil
import com.hekmatullahamin.friendhub.utils.UpdateCallBack
import com.hekmatullahamin.friendhub.utils.UploadProfilePictureCallback
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel(), UpdateCallBack, UploadProfilePictureCallback {

    private var _userUpdateSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val userUpdateSuccess: LiveData<Boolean> get() = _userUpdateSuccess

    private var _currentUser: MutableLiveData<User> = MutableLiveData()
    val currentUser: LiveData<User> get() = _currentUser


    //    If profile picture changed then upload to storage then update other details otherwise doesn't call uploadProfilePicture
//        function
    private var isProfilePictureChanged: Boolean = false
    fun profilePictureChanged(isChanged: Boolean) {
        isProfilePictureChanged = isChanged
    }

    fun setCurrentUser(user: User) {
        _currentUser.value = user
    }

    //    To check if name field in edit profile fragment is empty or not other are optional and display
    //    snackbar accordingly.
    fun isNameFieldIsEmptyToUpdateProfile(
        name: String,
        view: View,
        context: Context
    ): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                SnackBarUtil.showSnackBar(
                    context,
                    view,
                    context.resources.getString(R.string.enter_name_error_message),
                    true
                )
                false
            }

            else -> false
        }
    }

    //    this funtion is called from edit profile activity
    fun updateUser(context: Context) {
        ProgressBarUtil.showProgressBar(
            context,
            context.resources.getString(R.string.progress_bar_updating)
        )

        if (isProfilePictureChanged) {
            //        first upload photo to storage then on success update other details
            currentUser.value?.let { uploadProfilePicture(it) }
        } else {
            updateUserOtherDetails()
        }
    }

    //    we upload image to firebase storage and upon success update user other details
    private fun uploadProfilePicture(user: User) {
//        the uri is passed from editProfileActivity as string so have to change it to Uri in order to upload it to Firabase

        val uri = Uri.parse(user.userProfile)
        val fireStoreHandler = FireStoreHandler.getFireStoreHandler()
        fireStoreHandler.setUploadProfileCallBack(this)
        viewModelScope.launch { fireStoreHandler.uploadProfilePictureToStorage(uri, user.userId) }

    }

    //    we only update user other details cause the profile is not changed
    private fun updateUserOtherDetails() {
        val userHashMap: HashMap<String, Any> = hashMapOf()
        val user = currentUser.value
        user?.apply {
            userHashMap[Constants.USER_ID] = userId
            userHashMap[Constants.USER_NAME] = userName
            userHashMap[Constants.USER_EMAIL] = userEmail
//            we want to put a download uri in our firestore like: https://dafsdf not just a content from our device
            userHashMap[Constants.USER_PROFILE] = user.userProfile
            userHashMap[Constants.USER_BIO] = userBio
        }
        viewModelScope.launch {
            val fireStoreHandler = FireStoreHandler.getFireStoreHandler()
            fireStoreHandler.updateUser(userHashMap)
            fireStoreHandler.setUpdateCallBack(this@EditProfileViewModel)
        }
    }

    override fun onUploadSuccess(imageUri: Uri) {
        currentUser.value?.userProfile = imageUri.toString()
//        to set it false after we updated the profile picture in futture we may not change it
        profilePictureChanged(false)
        updateUserOtherDetails()
    }

    override fun onUploadFailure() {
        //TODO display a toast that image upload failed
        ProgressBarUtil.hideProgressBar()
    }

    //    we say userUpdated successfully and then will observe it on EditProfileActivity to navigate
    //    to another screen
    override fun onUpdateSuccess() {
        _userUpdateSuccess.value = true
    }

    override fun onUpdateFailed() {
        _userUpdateSuccess.value = false
    }

}