package com.example.erwancastioni.todo.tasklist

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Task(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String = "description") : java.io.Serializable