package com.example.td.domain.usecase

import com.example.td.domain.model.Task
import com.example.td.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksUseCase(private val repository: TaskRepository) {
    operator fun invoke(): Flow<List<Task>> = repository.getTasks()
}
