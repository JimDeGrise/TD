package com.example.td.data.repository

import com.example.td.data.local.dao.TaskDao
import com.example.td.data.local.entity.TaskEntity
import com.example.td.domain.model.Task
import com.example.td.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(private val dao: TaskDao) : TaskRepository {

    override fun getTasks(): Flow<List<Task>> =
        dao.getTasks().map { entities -> entities.map { it.toDomain() } }

    override suspend fun addTask(task: Task) =
        dao.insertTask(task.toEntity())

    override suspend fun deleteTask(task: Task) =
        dao.deleteTask(task.toEntity())

    private fun TaskEntity.toDomain() = Task(id = id, title = title, description = description, isCompleted = isCompleted, createdAt = createdAt)

    private fun Task.toEntity() = TaskEntity(id = id, title = title, description = description, isCompleted = isCompleted, createdAt = createdAt)
}
