package com.example.homework6

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.homework6.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferences = requireContext().getSharedPreferences(
            "user_preferences",
            Context.MODE_PRIVATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginEditText.setText(
            preferences.getString("login", "")
        )

        binding.passwordEditText.setText(
            preferences.getString("password", "")
        )

        binding.saveLoginButton.setOnClickListener {
            saveLoginData()
        }
    }

    private fun saveLoginData() {
        val login = binding.loginEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString()

        binding.loginLayout.error = null
        binding.passwordLayout.error = null

        var hasErrors = false

        if (login.isBlank()) {
            binding.loginLayout.error = getString(R.string.error_login)
            hasErrors = true
        }

        if (password.isBlank()) {
            binding.passwordLayout.error = getString(R.string.error_password)
            hasErrors = true
        }

        if (hasErrors) {
            return
        }

        preferences.edit()
            .putString("login", login)
            .putString("password", password)
            .apply()

        Toast.makeText(
            requireContext(),
            R.string.login_saved,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}