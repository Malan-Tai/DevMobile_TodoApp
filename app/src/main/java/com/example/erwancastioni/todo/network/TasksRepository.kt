package com.example.erwancastioni.todo.network

import com.example.erwancastioni.todo.tasklist.Task

class TasksRepository {
    private val tasksWebService = Api.tasksWebService

    suspend fun loadTasks(): List<Task>? {
        val response = tasksWebService.getTasks()
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun updateTask(task: Task) {
        tasksWebService.update(task)
    }

    suspend fun createTask(task: Task) {
        tasksWebService.create(task)
    }

    suspend fun deleteTask(task: Task) {
        tasksWebService.delete(task.id)
    }
}