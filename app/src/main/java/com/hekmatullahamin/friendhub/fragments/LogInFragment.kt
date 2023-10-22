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
import com.hekmatullahamin.friendhub.activities.HubActivity
import com.hekmatullahamin.friendhub.databinding.FragmentLogInBinding
import com.hekmatullahamin.friendhub.databinding.FragmentSignUpBinding
import com.hekmatullahamin.friendhub.utils.Constants
import com.hekmatullahamin.friendhub.utils.ProgressBarUtil
import com.hekmatullahamin.friendhub.viewmodels.AuthenticationViewModel

class LogInFragment : Fragment() {

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthenticationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLogInBinding.inflate(inflater, container, false)

        binding.apply {
            framgnet = this@LogInFragment
            lifecycleOwner = viewLifecycleOwner
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.userLoginSuccess.observe(viewLifecycleOwner) { isSuccessfullyLogedIn ->
            if (isSuccessfullyLogedIn) {
                ProgressBarUtil.hideProgressBar()
                val intent = Intent(requireContext(), HubActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                ProgressBarUtil.hideProgressBar()
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.login_failed),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun goToSignUpScreen() {
        findNavController().navigate(R.id.action_logInFragment_to_signUpFragment)
    }

    fun loginUser() {
        viewModel.isAnyFieldIsEmptyToSingIn(
            binding.userEmailET.text.toString().trim(),
            binding.userPasswordET.text.toString().trim(),
            requireActivity().findViewById(android.R.id.content),
            requireContext()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}