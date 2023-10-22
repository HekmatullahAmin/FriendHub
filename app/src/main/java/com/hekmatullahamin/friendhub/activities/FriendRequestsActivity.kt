package com.hekmatullahamin.friendhub.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.MenuHost
import com.hekmatullahamin.friendhub.R
import com.hekmatullahamin.friendhub.databinding.ActivityFriendRequestsBinding

class FriendRequestsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendRequestsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendRequestsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            activity = this@FriendRequestsActivity
        }

        setSupportActionBar(binding.friendsToolbar)
    }

    fun goToFindFriendScreen() {
        startActivity(Intent(this, FindFriendActivity::class.java))
    }
}