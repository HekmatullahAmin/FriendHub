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
import com.hekmatullahamin.friendhub.activities.EditProfileActivity
import com.hekmatullahamin.friendhub.databinding.FragmentSignUpBinding
import com.hekmatullahamin.friendhub.firestore.FireStoreHandler
import com.hekmatullahamin.friendhub.model.User
import com.hekmatullahamin.friendhub.utils.Constants
import com.hekmatullahamin.friendhub.utils.ProgressBarUtil
import com.hekmatullahamin.friendhub.viewmodels.AuthenticationViewModel

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthenticationViewModel by activityViewModels()

    //Use them to send to edit profile fragment in case of success register
    private lateinit var userName: String
    private lateinit var userEmail: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding.apply {
            fragment = this@SignUpFragment
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        response from our viewmodel
        viewModel.userRegistrationSuccess.observe(viewLifecycleOwner) { isSuccessfullyRegistered ->
            if (isSuccessfullyRegistered) {
                ProgressBarUtil.hideProgressBar()
                val intent = Intent(requireContext(), EditProfileActivity::class.java)
                intent.putExtra(Constants.USER, viewModel.currentUser.value)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                ProgressBarUtil.hideProgressBar()
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.account_creation_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    //    To go to login screen
    fun goToLogInScreen() {
        findNavController().navigate(R.id.action_signUpFragment_to_logInFragment)
    }

    //    TODO if user already exist display different Toast rather than failed to create account
    //    To validate if any field is empty by calling function from viewmodel class and then creating user there
    fun createUser() {
        binding.apply {
            userName = userNameET.text.toString().trim()
            userEmail = userEmailET.text.toString().trim()

            viewModel.isAnyFieldIsEmptyToSignUp(
                userName,
                userEmail,
                userPasswordET.text.toString().trim(),
                userConfirmPasswordET.text.toString().trim(),
                view = requireActivity().findViewById(android.R.id.content),
                requireContext()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}