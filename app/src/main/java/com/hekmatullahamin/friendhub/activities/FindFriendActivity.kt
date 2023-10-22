package com.hekmatullahamin.friendhub.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hekmatullahamin.friendhub.adapters.PersonListAdapter
import com.hekmatullahamin.friendhub.databinding.ActivityFindFriendBinding
import com.hekmatullahamin.friendhub.model.User
import com.hekmatullahamin.friendhub.utils.Constants
import com.hekmatullahamin.friendhub.viewmodels.FindFriendViewModel


class FindFriendActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFindFriendBinding
    private val findFriendViewModel: FindFriendViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFindFriendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = PersonListAdapter() { user ->
            goToUserProfileScreen(user)
        }

        setSupportActionBar(binding.findFriendToolbar)
        binding.apply {
            activity = this@FindFriendActivity
            viewModel = findFriendViewModel
            lifecycleOwner = this@FindFriendActivity
            personsRecyclerView.adapter = adapter
        }


        binding.searchPersonET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
//                populate recyclerView based on the name entered
                findFriendViewModel.filterRecyclerView(s)
            }

            override fun afterTextChanged(s: Editable) {}
        });
    }

    fun goToFriendRequestsScreen() {
        finish()
    }

    private fun goToUserProfileScreen(user: User) {
        val action = Intent(this, FriendProfileActivity::class.java)
        action.putExtra(Constants.USER, user)
        startActivity(action)
    }
}