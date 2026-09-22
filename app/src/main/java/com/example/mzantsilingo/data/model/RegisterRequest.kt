package com.example.mzantsilingo.data.model

// Holds the information needed when creating a new account.
data class RegisterRequest(
    // User's full name.
    val fullName: String,

    // Email address used for the account.
    val email: String,

    // Username chosen by the user.
    val username: String,

    // Password entered during registration.
    val password: String
)