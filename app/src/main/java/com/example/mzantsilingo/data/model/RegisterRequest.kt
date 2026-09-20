package com.example.mzantsilingo.data.model

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val username: String,
    val password: String
)