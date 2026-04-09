package com.example.td.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.td.domain.model.Task
import com.example.td.domain.usecase.AddTaskUseCase
import com.example.td.domain.usecase.DeleteTaskUseCase
import com.example.td.domain.usecase.GetTasksUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    val tasks: StateFlow<List<Task>> = getTasksUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addTask(title: String, description: String = "") {
        if (title.isBlank()) return
        viewModelScope.launch {
            addTaskUseCase(Task(title = title.trim(), description = description.trim()))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(task)
        }
    }

    class Factory(
        private val getTasksUseCase: GetTasksUseCase,
        private val addTaskUseCase: AddTaskUseCase,
        private val deleteTaskUseCase: DeleteTaskUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            TaskViewModel(getTasksUseCase, addTaskUseCase, deleteTaskUseCase) as T
    }
}
