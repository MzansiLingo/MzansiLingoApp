package com.example.mzantsilingo.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mzantsilingo.data.model.repository.AuthRepository
import com.example.mzantsilingo.databinding.ActivityLoginBinding
import com.example.mzantsilingo.ui.home.HomeActivity
import kotlinx.coroutines.launch
import com.example.mzantsilingo.data.local.UserSession

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val result = authRepository.login(email, password)
                result.onSuccess { user ->

                    val userId = user.id

                    if (userId == null) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Login succeeded, but user ID was not returned.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@onSuccess
                    }

                    UserSession.saveUserId(
                        this@LoginActivity,
                        userId
                    )

                    Toast.makeText(
                        this@LoginActivity,
                        "Login successful!",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                    finish()

                }.onFailure {

                    Toast.makeText(
                        this@LoginActivity,
                        it.message ?: "Login failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }
}