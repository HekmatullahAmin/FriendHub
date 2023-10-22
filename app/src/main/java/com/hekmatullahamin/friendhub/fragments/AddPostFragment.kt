package com.hekmatullahamin.friendhub.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.hekmatullahamin.friendhub.R
import com.hekmatullahamin.friendhub.databinding.FragmentAddPostBinding
import com.hekmatullahamin.friendhub.utils.ProgressBarUtil
import com.hekmatullahamin.friendhub.utils.SnackBarUtil
import com.hekmatullahamin.friendhub.viewmodels.HubViewModel

class AddPostFragment : Fragment() {
    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private val hubViewModel: HubViewModel by activityViewModels()

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    //    If we already don't have permission then ask for it
    private val requestStoragePermission: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launchGalleryIntent()
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        It is generally considered best practice to initialize activity result launchers in the onCreate method of the
//        fragment or activity where they will be used. This ensures that the launchers are initialized before they
//                are needed, and that they are properly cleaned up when they are no longer needed.
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val imageData = result.data
                if (imageData != null) {
                    val imageUri = imageData.data
//                    we set the post Image in our viewmodel class to bind it with the postImageView of FragmentAddPost
                    hubViewModel.setNewImageOfPost(imageUri!!)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        takePermissionToAccessGallery()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = hubViewModel
            fragment = this@AddPostFragment

        }

//        if user select a new image for his/her post then populate the post image in addPostFragment
        hubViewModel.newPost.observe(viewLifecycleOwner) { uri ->
            binding.imageUri = uri
        }

        hubViewModel.postWithCaptionSuccess.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                ProgressBarUtil.hideProgressBar()
                findNavController().navigate(R.id.actionHomeFragment)
            } else {
                ProgressBarUtil.hideProgressBar()
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.upload_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    //    To check if we have permission to access gallery otherwise take permission
    private fun takePermissionToAccessGallery() {
        val permission = Manifest.permission.READ_MEDIA_IMAGES
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            launchGalleryIntent()
        } else {
            requestStoragePermission.launch(permission)
        }
    }

    //    in order to go to our gallery
    private fun launchGalleryIntent() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(galleryIntent)
    }

    //    when share is clicked then upload image to storage and downloadUri and caption to firestore
    fun sharePost() {
        val caption = binding.postCaption.text.toString().trim()
        if (!hubViewModel.isFieldEmpty(caption)) {
            hubViewModel.setCaption(caption)
            hubViewModel.uploadPost(requireContext())
        } else {
            SnackBarUtil.showSnackBar(
                requireContext(),
                requireActivity().findViewById(android.R.id.content),
                resources.getString(R.string.no_caption_match_error),
                true
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requestStoragePermission.unregister()
        _binding = null
    }
}