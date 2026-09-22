package com.example.mzantsilingo.data.model

// Holds the response returned by the API after a login attempt.
data class LoginResponse(
    // Message returned by the server.
    val message: String,

    // Details of the user who successfully logged in.
    val user: User
)