package com.example.erwancastioni.todo.tasklist

interface TaskListListener {
    fun onClickDelete(task: Task)
    fun onClickEdit(task: Task)
    fun onClickShare(task: Task)
}