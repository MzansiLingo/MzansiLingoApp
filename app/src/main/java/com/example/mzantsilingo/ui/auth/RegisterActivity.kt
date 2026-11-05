package com.example.mzantsilingo.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mzantsilingo.R
import com.example.mzantsilingo.data.model.repository.AuthRepository
import com.example.mzantsilingo.databinding.ActivityRegisterBinding
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
                Toast.makeText(this, getString(R.string.msg_fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                Toast.makeText(this, getString(R.string.msg_password_min), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Stop double taps while the request is running.
            binding.btnSignUp.isEnabled = false

            lifecycleScope.launch {
                val result = authRepository.register(fullName, email, username, password)

                result.onSuccess {
                    Toast.makeText(
                        this@RegisterActivity,
                        getString(R.string.msg_account_created_login),
                        Toast.LENGTH_LONG
                    ).show()

                    // After registering, the user logs in with the details they just used.
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.putExtra("EMAIL", email)
                    startActivity(intent)
                    finish()

                }.onFailure {
                    binding.btnSignUp.isEnabled = true
                    Toast.makeText(
                        this@RegisterActivity,
                        it.message ?: "Registration failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.tvLogIn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}