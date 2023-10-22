package com.hekmatullahamin.friendhub.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.hekmatullahamin.friendhub.R
import com.hekmatullahamin.friendhub.databinding.ActivityFriendProfileBinding
import com.hekmatullahamin.friendhub.model.User
import com.hekmatullahamin.friendhub.utils.Constants

class FriendProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFriendProfileBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFriendProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val friend = intent.getBundleExtra(Constants.USER)
        val user: User = intent.getParcelableExtra(Constants.USER, User::class.java)!!

        binding.apply {
            lifecycleOwner = this@FriendProfileActivity
            binding.user = user
        }
    }
}