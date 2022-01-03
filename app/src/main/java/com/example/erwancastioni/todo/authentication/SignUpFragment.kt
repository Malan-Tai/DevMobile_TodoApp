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
import com.example.erwancastioni.todo.databinding.FragmentSignUpBinding
import com.example.erwancastioni.todo.network.Api
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val userWebService = Api.userWebService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUp.setOnClickListener {

            val firstname = binding.signupFirstName.text.toString()
            val lastname = binding.signupLastName.text.toString()
            val email = binding.signupEmail.text.toString()
            val pwd = binding.signupPassword.text.toString()
            val confirmpwd = binding.signupPasswordConfirm.text.toString()

            if (firstname == "" || lastname == "" || email == "" || pwd == "" || confirmpwd == "") {
                Toast.makeText(context, "Please fill in both fields", Toast.LENGTH_LONG).show()
            }
            else {
                val form = SignupForm(firstname, lastname, email, pwd, confirmpwd)

                lifecycleScope.launch {
                    val response = userWebService.signUp(form)

                    if (response.isSuccessful) {
                        val loginResponse = response.body() as LoginResponse

                        PreferenceManager.getDefaultSharedPreferences(context).edit {
                            putString(Api.SHARED_PREF_TOKEN_KEY, loginResponse.token)
                        }

                        findNavController().navigate(R.id.action_signUpFragment_to_taskListFragment)
                    }
                    else {
                        Toast.makeText(context, "Connexion error", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}