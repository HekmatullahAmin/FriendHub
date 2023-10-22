package com.hekmatullahamin.friendhub.fragments

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import com.hekmatullahamin.friendhub.R
import com.hekmatullahamin.friendhub.activities.FindFriendActivity
import com.hekmatullahamin.friendhub.activities.FriendRequestsActivity
import com.hekmatullahamin.friendhub.adapters.PostListAdapter
import com.hekmatullahamin.friendhub.databinding.FragmentHomeBinding
import com.hekmatullahamin.friendhub.firestore.FireStoreHandler
import com.hekmatullahamin.friendhub.utils.MyBottomSheet
import com.hekmatullahamin.friendhub.utils.ProgressBarUtil
import com.hekmatullahamin.friendhub.viewmodels.HubViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val hubViewModel: HubViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = binding.homeFragmentToolbar
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.home_fragment_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.friends -> {
                        if (isAdded && !activity?.isFinishing!!)
                            startActivity(Intent(requireContext(), FriendRequestsActivity::class.java))
                        true
                    }

                    R.id.messenger -> {
                        if (isAdded && !activity?.isFinishing!!)
                            Toast.makeText(
                                requireContext(),
                                "Messenger is clicked",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        hubViewModel.getAllPosts(requireContext())

        val currentUserId = FireStoreHandler.getFireStoreHandler().getCurrentUserId()
        var bottomSheet: MyBottomSheet? = null
        val adapter = PostListAdapter(currentUserId,
            onLikeClicked = { post ->
                if (post.likedUserIdsList.contains(currentUserId)) {
                    hubViewModel.setIsLickedClicked(true)
                } else {
                    hubViewModel.setIsLickedClicked(false)
                }
                hubViewModel.setUpdatedCommentOrLike(updateComment = false)
                hubViewModel.setCurrentClickedPost(post)
                hubViewModel.updatePost()
            },
            onCommentClicked = {
                hubViewModel.setUpdatedCommentOrLike(updateComment = true)
                bottomSheet = MyBottomSheet(it)
                bottomSheet!!.show(childFragmentManager, MyBottomSheet.TAG)
            }
        )

        hubViewModel.postUpdateSuccess.observe(viewLifecycleOwner) { isPostSuccessfullyUpdated ->
            // Check if the bottomSheet is already showing
            if (isPostSuccessfullyUpdated) {
                if (bottomSheet != null && bottomSheet!!.isAdded) {
                    bottomSheet?.dismiss()
                    ProgressBarUtil.hideProgressBar()
                }

//                in case of successfully commented and liked referesh the homeFragment by setting the postList
//                to what recieved recently from firestore
                hubViewModel.getAllPosts(requireContext())
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.upload_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = hubViewModel
            newsFeedRecyclerView.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}