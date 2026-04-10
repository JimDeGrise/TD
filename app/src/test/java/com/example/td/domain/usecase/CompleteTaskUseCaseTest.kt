package com.example.td.domain.usecase

import com.example.td.domain.model.Task
import com.example.td.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CompleteTaskUseCaseTest {

    private lateinit var updatedTasks: MutableList<Task>
    private lateinit var useCase: CompleteTaskUseCase

    @Before
    fun setUp() {
        updatedTasks = mutableListOf()
        val fakeRepository = object : TaskRepository {
            override fun getTasks(): Flow<List<Task>> = error("not used")
            override suspend fun addTask(task: Task) = Unit
            override suspend fun updateTask(task: Task) { updatedTasks.add(task) }
            override suspend fun deleteTask(task: Task) = Unit
        }
        useCase = CompleteTaskUseCase(fakeRepository)
    }

    @Test
    fun `invoke marks task as completed`() = runTest {
        val task = Task(id = 1, title = "Write tests", isCompleted = false)
        useCase(task)
        assertEquals(1, updatedTasks.size)
        assertTrue(updatedTasks.first().isCompleted)
    }

    @Test
    fun `invoke sets completedAt timestamp`() = runTest {
        val task = Task(id = 1, title = "Write tests", isCompleted = false)
        val before = System.currentTimeMillis()
        useCase(task)
        val after = System.currentTimeMillis()
        val completedAt = updatedTasks.first().completedAt
        assertNotNull(completedAt)
        assertTrue(completedAt!! in before..after)
    }

    @Test
    fun `invoke preserves task id and title`() = runTest {
        val task = Task(id = 42, title = "Important task", description = "Details")
        useCase(task)
        val result = updatedTasks.first()
        assertEquals(42L, result.id)
        assertEquals("Important task", result.title)
        assertEquals("Details", result.description)
    }

    @Test
    fun `invoke delegates to repository updateTask`() = runTest {
        useCase(Task(id = 1, title = "Task A"))
        useCase(Task(id = 2, title = "Task B"))
        assertEquals(2, updatedTasks.size)
    }
}
