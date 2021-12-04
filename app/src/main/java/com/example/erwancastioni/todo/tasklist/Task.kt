package com.example.erwancastioni.todo.tasklist

import java.io.Serializable

data class Task(val id: String, val title: String, val description: String = "description") : Serializable