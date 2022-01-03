package com.example.erwancastioni.todo.authentication

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SignupForm(
    @SerialName("firstname")
    val firstName: String,
    @SerialName("lastname")
    val lastName: String,
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
    @SerialName("password_confirmation")
    val passwordConfirm: String
)
