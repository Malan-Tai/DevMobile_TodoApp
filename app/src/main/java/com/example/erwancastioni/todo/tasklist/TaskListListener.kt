package com.example.erwancastioni.todo.tasklist

interface TaskListListener {
    fun onClickDelete(task: Task) : List<Task>
}