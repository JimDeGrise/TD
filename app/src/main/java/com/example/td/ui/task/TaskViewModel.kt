package com.example.td.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.td.domain.model.Task
import com.example.td.domain.usecase.AddTaskUseCase
import com.example.td.domain.usecase.CompleteTaskUseCase
import com.example.td.domain.usecase.DeleteTaskUseCase
import com.example.td.domain.usecase.GetTasksUseCase
import com.example.td.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val getTasksUseCase: GetTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {

    private val allTasks: StateFlow<List<Task>> = getTasksUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val activeTasks: StateFlow<List<Task>> = allTasks
        .map { tasks -> tasks.filter { !it.isCompleted } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val completedTasks: StateFlow<List<Task>> = allTasks
        .map { tasks -> tasks.filter { it.isCompleted } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _selectedTabIndex = MutableStateFlow(0)
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    fun setSelectedTab(index: Int) {
        _selectedTabIndex.value = index
    }

    fun addTask(title: String, description: String = "") {
        if (title.isBlank()) return
        viewModelScope.launch {
            addTaskUseCase(Task(title = title.trim(), description = description.trim()))
        }
    }

    fun completeTask(task: Task) {
        viewModelScope.launch {
            completeTaskUseCase(task)
        }
    }

    fun deleteTask(task: Task) {
        if (!task.isCompleted) return
        viewModelScope.launch {
            deleteTaskUseCase(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task)
        }
    }

    fun toggleStar(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task.copy(isStarred = !task.isStarred))
        }
    }

    class Factory(
        private val getTasksUseCase: GetTasksUseCase,
        private val addTaskUseCase: AddTaskUseCase,
        private val deleteTaskUseCase: DeleteTaskUseCase,
        private val completeTaskUseCase: CompleteTaskUseCase,
        private val updateTaskUseCase: UpdateTaskUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            TaskViewModel(getTasksUseCase, addTaskUseCase, deleteTaskUseCase, completeTaskUseCase, updateTaskUseCase) as T
    }
}

