package com.example.mzantsilingo.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mzantsilingo.R
import com.example.mzantsilingo.data.local.UserSession
import com.example.mzantsilingo.data.model.User
import com.example.mzantsilingo.data.model.repository.AuthRepository
import com.example.mzantsilingo.databinding.ActivityLoginBinding
import com.example.mzantsilingo.ui.home.HomeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authRepository = AuthRepository()

    // Receives the result of the Google sign-in screen.
    private val googleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken == null) {
                    toast("Google did not return a sign-in token.")
                    return@registerForActivityResult
                }
                lifecycleScope.launch {
                    setLoading(true)
                    handleResult(authRepository.googleLogin(idToken))
                }
            } catch (e: ApiException) {
                toast("Google sign-in failed (code ${e.statusCode}).")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                toast("Please fill in all fields")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                setLoading(true)
                handleResult(authRepository.login(email, password))
            }
        }

        binding.btnGoogleSignIn.setOnClickListener {
            val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .build()
            val client = GoogleSignIn.getClient(this, options)
            // Sign out first so the account chooser always appears.
            client.signOut().addOnCompleteListener {
                googleLauncher.launch(client.signInIntent)
            }
        }

        binding.tvSignUp.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun handleResult(result: Result<User>) {
        setLoading(false)
        result.onSuccess { user ->
            val userId = user.id
            if (userId == null) {
                toast("Login succeeded, but the user ID was not returned.")
                return
            }
            UserSession.saveUserId(this, userId)
            toast("Login successful!")
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }.onFailure {
            toast(it.message ?: "Login failed")
        }
    }

    // Disables the buttons while a request is running so users don't double-tap.
    private fun setLoading(loading: Boolean) {
        binding.btnLogin.isEnabled = !loading
        binding.btnGoogleSignIn.isEnabled = !loading
        binding.btnLogin.text = if (loading) "Please wait..." else "Log In"
    }

    private fun toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}