package com.example.erwancastioni.todo.authentication

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LoginForm(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String
)
