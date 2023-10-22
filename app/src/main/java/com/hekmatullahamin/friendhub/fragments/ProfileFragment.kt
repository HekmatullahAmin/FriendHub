package com.hekmatullahamin.friendhub.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.hekmatullahamin.friendhub.R
import com.hekmatullahamin.friendhub.activities.AuthenticationActivity
import com.hekmatullahamin.friendhub.activities.EditProfileActivity
import com.hekmatullahamin.friendhub.adapters.ProfilePostListAdapter
import com.hekmatullahamin.friendhub.databinding.FragmentProfileBinding
import com.hekmatullahamin.friendhub.firestore.FireStoreHandler
import com.hekmatullahamin.friendhub.utils.Constants
import com.hekmatullahamin.friendhub.utils.ProgressBarUtil
import com.hekmatullahamin.friendhub.viewmodels.HubViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val hubViewModel: HubViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ProgressBarUtil.showProgressBar(
            requireContext(),
            resources.getString(R.string.progress_bar_loading)
        )

        hubViewModel.currentUserSuccess.observe(viewLifecycleOwner) { isSuccessfullyFetchedUser ->
            if (!isSuccessfullyFetchedUser) {
                ProgressBarUtil.hideProgressBar()
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.user_fecthed_failed),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                ProgressBarUtil.hideProgressBar()
            }
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = hubViewModel
            fragment = this@ProfileFragment
            postsRecyclerView.adapter = ProfilePostListAdapter()
        }
    }

    fun goToEditProfileScreen() {
        val intent = Intent(requireContext(), EditProfileActivity::class.java)
        intent.putExtra(Constants.USER, hubViewModel.currentUser.value)
        startActivity(intent)
    }

    fun goToLoginScreen() {
        FireStoreHandler.getFireStoreHandler().firebaseAuth.signOut()
        startActivity(Intent(requireContext(), AuthenticationActivity::class.java))
        requireActivity().finish()
    }

    fun getCurrentUserPosts() {
        hubViewModel.getCurrentUserPosts()
    }

    fun getUserLikePost() {
        hubViewModel.getCurrentUserLikedPosts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}