package com.example.myapp.model

data class User(
    val userId: Int = 0,
    val name: String,
    val email: String,
    val role: String? = null
)
