package com.hekmatullahamin.friendhub.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hekmatullahamin.friendhub.R
import com.hekmatullahamin.friendhub.adapters.CommentListAdapter
import com.hekmatullahamin.friendhub.databinding.CustomCommentsBottomSheetDialogBinding
import com.hekmatullahamin.friendhub.model.Comment
import com.hekmatullahamin.friendhub.model.Post
import com.hekmatullahamin.friendhub.viewmodels.HubViewModel

class MyBottomSheet(val post: Post) : BottomSheetDialogFragment() {
    private var _binding: CustomCommentsBottomSheetDialogBinding? = null
    val binding get() = _binding!!
    private val hubViewModel: HubViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CustomCommentsBottomSheetDialogBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myCommentTextInputLayout = binding.commentTextInputLayout
        myCommentTextInputLayout.setEndIconOnClickListener {
            uploadComment()
        }

        hubViewModel.getPostComments(requireContext(), post.postId)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = hubViewModel
            commentsRecyclerView.adapter = CommentListAdapter()
        }
    }

    private fun uploadComment() {
        val commentText = binding.commentET.text.toString().trim()
        if (hubViewModel.isFieldEmpty(commentText)) {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.no_comment_error),
                Toast.LENGTH_LONG
            ).show()
        } else {
            ProgressBarUtil.showProgressBar(
                requireContext(),
                resources.getString(R.string.progress_bar_uploading)
            )
//            we pass the id of post which is cliked in our PostListAdapter
            val tempComment =
                Comment(commentText = commentText, postId = post.postId)
            hubViewModel.setCurrentClickedPost(post)
            hubViewModel.uploadCommentToFirestore(tempComment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "MyBottomSheet"
    }
}