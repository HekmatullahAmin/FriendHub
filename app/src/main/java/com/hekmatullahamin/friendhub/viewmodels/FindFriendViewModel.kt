package com.hekmatullahamin.friendhub.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hekmatullahamin.friendhub.firestore.FireStoreHandler
import com.hekmatullahamin.friendhub.model.User
import com.hekmatullahamin.friendhub.utils.GetAllUserCallback
import kotlinx.coroutines.launch

class FindFriendViewModel : ViewModel(), GetAllUserCallback {
    private var _users: List<User> = mutableListOf()

    private var _allFilteredUsers: MutableLiveData<List<User>> = MutableLiveData()
    val allFilteredUsers: LiveData<List<User>> get() = _allFilteredUsers

    init {
        getAllUser()
    }

    private fun getAllUser() {
        val fireStoreHandler = FireStoreHandler.getFireStoreHandler()
        fireStoreHandler.setGetAllUsersCallBack(this)
        viewModelScope.launch {
            fireStoreHandler.getAllUsers()
        }
    }

    fun filterRecyclerView(s: CharSequence) {
        val input = s.toString().lowercase()
        val allUsers: List<User>? = _users
        val filteredUser: MutableList<User> = mutableListOf()
//        if the user clear search field then clear the filteredUser list in order to our recyclerview doesn't display
//        anything it should be clear
        if (input.isEmpty()) {
            filteredUser.clear()
        }
        if (allUsers != null && input.isNotEmpty()) {
            for (user in allUsers) {
                if (user.userName.lowercase().startsWith(input)) {
                    filteredUser.add(user)
                }
            }
        }
        _allFilteredUsers.value = filteredUser
    }

    override fun onGetUsersSuccess(users: List<User>) {
        _users = users
    }

    override fun onGetUserFailure() {
        TODO("Not yet implemented")
    }
}