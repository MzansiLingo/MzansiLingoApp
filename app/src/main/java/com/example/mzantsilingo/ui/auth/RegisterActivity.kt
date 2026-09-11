package com.example.mzantsilingo.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mzantsilingo.data.model.repository.AuthRepository
import com.example.mzantsilingo.databinding.ActivityRegisterBinding
import com.example.mzantsilingo.ui.language.LanguageSelectionActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUp.setOnClickListener {
            val fullName = binding.etFullName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = authRepository.register(fullName, email, username, password)
                result.onSuccess {
                    Toast.makeText(this@RegisterActivity, "Account created!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LanguageSelectionActivity::class.java))
                    finish()
                }.onFailure {
                    Toast.makeText(this@RegisterActivity, it.message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvLogIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}