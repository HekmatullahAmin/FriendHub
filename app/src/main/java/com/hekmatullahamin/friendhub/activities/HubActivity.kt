package com.hekmatullahamin.friendhub.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.hekmatullahamin.friendhub.R
import com.hekmatullahamin.friendhub.databinding.ActivityHubBinding
import com.hekmatullahamin.friendhub.firestore.FireStoreHandler
import com.hekmatullahamin.friendhub.viewmodels.HubViewModel

class HubActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHubBinding

    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHubBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // for android 11 (api level 30) and higher
            window.insetsController?.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val bottomNavigationView = binding.hubNavigationBottom

//        want to perform additional operations on the NavHostFragment before getting the NavController
//        Custom Transitions, Configuration Changes ...
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.hubNavHostFragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)
    }
}