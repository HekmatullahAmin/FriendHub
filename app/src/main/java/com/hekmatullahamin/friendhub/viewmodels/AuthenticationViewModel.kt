package com.hekmatullahamin.friendhub.viewmodels

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hekmatullahamin.friendhub.R
import com.hekmatullahamin.friendhub.firestore.FireStoreHandler
import com.hekmatullahamin.friendhub.model.User
import com.hekmatullahamin.friendhub.utils.ProgressBarUtil
import com.hekmatullahamin.friendhub.utils.RegistrationCallBack
import com.hekmatullahamin.friendhub.utils.SnackBarUtil
import kotlinx.coroutines.launch

class AuthenticationViewModel : ViewModel(), RegistrationCallBack {
    private var _userRegistrationSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val userRegistrationSuccess: LiveData<Boolean> get() = _userRegistrationSuccess

    private var _userLoginSuccess: MutableLiveData<Boolean> = MutableLiveData()
    val userLoginSuccess: LiveData<Boolean> get() = _userLoginSuccess

    private var _currentUser: MutableLiveData<User> = MutableLiveData()
    val currentUser: LiveData<User> get() = _currentUser

    //    To check if any field in sign up fragment is empty or not and display snackbar accordingly.
    fun isAnyFieldIsEmptyToSignUp(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        view: View,
        context: Context
    ) {
        when {
            TextUtils.isEmpty(name) -> {
                SnackBarUtil.showSnackBar(
                    context,
                    view,
                    context.resources.getString(R.string.enter_name_error_message),
                    true
                )
            }

            TextUtils.isEmpty(email) -> {
                SnackBarUtil.showSnackBar(
                    context,
                    view,
                    context.resources.getString(R.string.enter_email_error_message),
                    true
                )
            }

            TextUtils.isEmpty(password) -> {
                SnackBarUtil.showSnackBar(
                    context,
                    view,
                    context.resources.getString(R.string.enter_password_error_message),
                    true
                )
            }

            TextUtils.isEmpty(confirmPassword) -> {
                SnackBarUtil.showSnackBar(
                    context,
                    view,
                    context.resources.getString(R.string.enter_confirm_password_error_message),
                    true
                )
            }

            password != confirmPassword -> {
                SnackBarUtil.showSnackBar(
                    context,
                    view,
                    context.resources.getString(R.string.password_not_match_error),
                    true
                )
            }

            else -> {
//                if all fields are entered then create user
                viewModelScope.launch { createUser(name, email, password, context) }
            }
        }
    }

    private fun createUser(name: String, email: String, password: String, context: Context) {
//        show waiting progress bar when start creating user
        ProgressBarUtil.showProgressBar(context)
        FireStoreHandler.getFireStoreHandler().firebaseAuth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = User(
                    userId = FireStoreHandler.getFireStoreHandler().getCurrentUserId(),
                    userName = name,
                    userEmail = email
                )

//                We set current user here, later on we will use this to send it to edit profile fragment
                _currentUser.value = user

                /*TODO if this create a singleton instance then why by doint it this way I can't go
                to another fragment and nor my progress bar is dissmissed*/

//                FireStoreHandler.getFireStoreHandler().registerUser(user)
//                FireStoreHandler.getFireStoreHandler().setRegistrationCallback(this)
                val fireStoreHandler = FireStoreHandler.getFireStoreHandler()
                fireStoreHandler.registerUser(user)
                fireStoreHandler.setRegistrationCallback(this)
            } else {
                ProgressBarUtil.hideProgressBar()
                Toast.makeText(
                    context,
                    context.resources.getString(R.string.account_creation_failed),
                    Toast.LENGTH_LONG
                ).show()
            }

        }
    }

    fun isAnyFieldIsEmptyToSingIn(
        email: String,
        password: String,
        view: View,
        context: Context
    ) {
        when {
            TextUtils.isEmpty(email) -> {
                SnackBarUtil.showSnackBar(
                    context,
                    view,
                    context.resources.getString(R.string.enter_email_error_message),
                    true
                )
            }

            TextUtils.isEmpty(password) -> {
                SnackBarUtil.showSnackBar(
                    context,
                    view,
                    context.resources.getString(R.string.enter_password_error_message),
                    true
                )
            }

            else -> {
                ProgressBarUtil.showProgressBar(context)
                viewModelScope.launch { loginUser(email, password) }
            }
        }
    }

    private fun loginUser(email: String, password: String) {
        FireStoreHandler.getFireStoreHandler().firebaseAuth.signInWithEmailAndPassword(
            email,
            password
        )
            .addOnSuccessListener {
                _userLoginSuccess.value = true
            }
            .addOnFailureListener {
                _userLoginSuccess.value = false
            }
    }

    override fun onRegistrationSuccess() {
        _userRegistrationSuccess.value = true
    }

    override fun onRegistrationFailure() {
        _userRegistrationSuccess.value = false
    }
}