package com.example.erwancastioni.todo.authentication

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.erwancastioni.todo.R
import com.example.erwancastioni.todo.databinding.FragmentLogInBinding
import com.example.erwancastioni.todo.network.Api
import kotlinx.coroutines.launch

class LogInFragment : Fragment() {
    val SHARED_PREF_TOKEN_KEY = "auth_token_key"

    private lateinit var binding: FragmentLogInBinding
    private val userWebService = Api.userWebService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logIn.setOnClickListener {

            val email = binding.loginMail.text.toString()
            val pwd = binding.loginPassword.text.toString()

            if (email == "" || pwd == "") {
                Toast.makeText(context, "Please fill in both fields", Toast.LENGTH_LONG).show()
            }
            else {
                val form = LoginForm(email, pwd)

                lifecycleScope.launch {
                    val response = userWebService.login(form)

                    if (response.isSuccessful) {
                        val loginResponse = response.body() as LoginResponse

                        PreferenceManager.getDefaultSharedPreferences(context).edit {
                            putString(SHARED_PREF_TOKEN_KEY, loginResponse.token)
                        }

                        findNavController().navigate(R.id.action_logInFragment_to_taskListFragment)
                    }
                    else {
                        Toast.makeText(context, "Connexion error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}