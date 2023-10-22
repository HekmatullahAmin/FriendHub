package com.hekmatullahamin.friendhub.activities

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.hekmatullahamin.friendhub.R
import com.hekmatullahamin.friendhub.databinding.ActivityEditProfileBinding
import com.hekmatullahamin.friendhub.model.User
import com.hekmatullahamin.friendhub.utils.Constants
import com.hekmatullahamin.friendhub.utils.ProgressBarUtil
import com.hekmatullahamin.friendhub.viewmodels.EditProfileViewModel

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private val editProfileViewModel: EditProfileViewModel by viewModels()

    private lateinit var user: User

    //    this will request to access gallery if it is not before granted
    private val requestStoragePermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launchGalleryIntent()
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageData = result.data
                    if (imageData != null) {
//                        In here setCurrentUser in viewmodel and in there because of observing BindingAdapter
                        //                        loadImge will be called
                        val imageUri = imageData.data.toString()
//
                        user.userProfile = imageUri
                        editProfileViewModel.profilePictureChanged(true)
                        editProfileViewModel.setCurrentUser(user)
                    }

                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.image_selection_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        //TODO: when we select image and comeback the bio got clear, so it should save it somewhere
        //        To receive what is passed through navigation
        if (intent.hasExtra(Constants.USER)) {
            user = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Constants.USER, User::class.java)!!
            } else {
                intent.getParcelableExtra<User>(Constants.USER)!!
            }
        }

//        we want to set the to populate the profile which we received from navArgs
        editProfileViewModel.setCurrentUser(user)

        binding.apply {
            viewModel = editProfileViewModel
            activity = this@EditProfileActivity
            lifecycleOwner = this@EditProfileActivity
        }

        //    if our user got updated successfully in EditProfileViewModel then go to Profile Fragment
        editProfileViewModel.userUpdateSuccess.observe(this) { isSuccessfullyUpdated ->
            if (isSuccessfullyUpdated) {
                ProgressBarUtil.hideProgressBar()
//                TODO go to profile fragment
                //TODO after first sign up should send current user to home
                val intent = Intent(this, HubActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                ProgressBarUtil.hideProgressBar()
                Toast.makeText(
                    this,
                    resources.getString(R.string.user_update_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

    //    First we need to check if permission to access gallery is granted
    fun takePermissionToAccessGallery() {
        val permission = Manifest.permission.READ_MEDIA_IMAGES
        if (ContextCompat.checkSelfPermission(
                this,
                permission,
            ) == PackageManager.PERMISSION_GRANTED
        ) launchGalleryIntent()
        else {
            requestStoragePermissionLauncher.launch(permission)
        }
    }

    private fun launchGalleryIntent() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        In case maybe device don't have gallery shouldn't crush just show a toast message
        try {
            pickImageLauncher.launch(galleryIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

    //    if user singUp for the first time or come from profile fragment so update details
    fun updateUserProfile() {
        val name = binding.userNameET.text.toString()
        val bio = binding.userBioET.text.toString()
        user.userName = name
        user.userBio = bio
//        if is not empty then call update user details
        if (!editProfileViewModel.isNameFieldIsEmptyToUpdateProfile(
                name,
                findViewById(android.R.id.content),
                this
            )
        ) {
//            we set current user in viemodel because when profile is uploaded then set user to current user for updating details
            editProfileViewModel.setCurrentUser(user)
//            editProfileViewModel.profilePictureChanged(false)
            editProfileViewModel.updateUser(this)
        }
    }


}