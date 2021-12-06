package com.example.erwancastioni.todo.network

import com.example.erwancastioni.todo.tasklist.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasksRepository {
    private val tasksWebService = Api.tasksWebService

    // Ces deux variables encapsulent la même donnée:
    // [_taskList] est modifiable mais privée donc inaccessible à l'extérieur de cette classe
    private val _taskList = MutableStateFlow<List<Task>>(value = emptyList())
    // [taskList] est publique mais non-modifiable:
    // On pourra seulement l'observer (s'y abonner) depuis d'autres classes
    public val taskList: StateFlow<List<Task>> = _taskList.asStateFlow()

    suspend fun refresh() {
        // Call HTTP (opération longue):
        val tasksResponse = tasksWebService.getTasks()
        // À la ligne suivante, on a reçu la réponse de l'API:
        if (tasksResponse.isSuccessful) {
            val fetchedTasks = tasksResponse.body()
            // on modifie la valeur encapsulée, ce qui va notifier ses Observers et donc déclencher leur callback
            if (fetchedTasks != null) _taskList.value = fetchedTasks
        }
    }

    suspend fun updateTask(task: Task) {
        val updateResponse = tasksWebService.update(task)
        if (updateResponse.isSuccessful) {
            val updatedTask = updateResponse.body()!!
            val oldTask = taskList.value.firstOrNull { it.id == updatedTask.id }
            if (oldTask != null) _taskList.value = taskList.value - oldTask + updatedTask
        }
    }

    suspend fun createTask(task: Task) {
        val createResponse = tasksWebService.create(task)
        if (createResponse.isSuccessful) {
            val newTask = createResponse.body()!!
            _taskList.value += newTask
        }
    }

    suspend fun deleteTask(task: Task) {
        val deleteResponse = tasksWebService.delete(task)
        if (deleteResponse.isSuccessful) {
            _taskList.value -= task
        }
    }
}