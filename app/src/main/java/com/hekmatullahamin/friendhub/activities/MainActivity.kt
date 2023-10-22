package com.hekmatullahamin.friendhub.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import com.google.api.Authentication
import com.google.firebase.ktx.Firebase
import com.hekmatullahamin.friendhub.R
import com.hekmatullahamin.friendhub.firestore.FireStoreHandler

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // for android 11 (api level 30) and higher
            window.insetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

//                To delay and display splash screen and then move to Authentication activity after 3 seconds
        Thread {
            try {
                Thread.sleep(3000)
            } catch (e: Exception) {
                throw e
            }
            startActivity(Intent(this, AuthenticationActivity::class.java))
            finish()
        }.start()
    }
}