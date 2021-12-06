package com.example.erwancastioni.todo.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.erwancastioni.todo.tasklist.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TaskListViewModel : ViewModel() {
    private val repository = TasksRepository()

    private val _taskList = MutableStateFlow<List<Task>>(emptyList())
    public val taskList: StateFlow<List<Task>> = _taskList

    fun loadTasks() {
        viewModelScope.launch {
            _taskList.value = repository.loadTasks() ?: emptyList()
        }
    }
    fun deleteTask(task: Task) {
        val oldTask = _taskList.value.firstOrNull { it.id == task.id }
        if (oldTask != null) {
            viewModelScope.launch {
                repository.deleteTask(oldTask)
            }
            _taskList.value = _taskList.value - oldTask
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.createTask(task)
        }
        _taskList.value = _taskList.value + task
    }

    fun editTask(task: Task) {
        val oldTask = _taskList.value.firstOrNull { it.id == task.id }
        if (oldTask != null) {
            viewModelScope.launch {
                repository.updateTask(task)
            }
            _taskList.value = _taskList.value - oldTask + task
        }
    }
}