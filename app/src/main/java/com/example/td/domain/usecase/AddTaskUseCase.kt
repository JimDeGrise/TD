package com.example.td.domain.usecase

import com.example.td.domain.model.Task
import com.example.td.domain.repository.TaskRepository

class AddTaskUseCase(private val repository: TaskRepository) {
    suspend operator fun invoke(task: Task) = repository.addTask(task)
}
