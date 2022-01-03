package com.example.erwancastioni.todo.authentication

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LoginResponse(
    @SerialName("token")
    val token: String
)
