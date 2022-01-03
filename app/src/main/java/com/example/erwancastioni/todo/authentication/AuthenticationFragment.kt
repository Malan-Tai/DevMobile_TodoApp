package com.example.erwancastioni.todo.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.erwancastioni.todo.R
import com.example.erwancastioni.todo.databinding.FragmentAuthenticationBinding

class AuthenticationFragment : Fragment() {
    private lateinit var binding: FragmentAuthenticationBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthenticationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUp.setOnClickListener {
            findNavController().navigate(R.id.action_authenticationFragment_to_signUpFragment)
        }

        binding.logIn.setOnClickListener {
            findNavController().navigate(R.id.action_authenticationFragment_to_logInFragment)
        }
    }
}