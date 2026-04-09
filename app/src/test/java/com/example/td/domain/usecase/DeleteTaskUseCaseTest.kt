package com.example.td.domain.usecase

import com.example.td.domain.model.Task
import com.example.td.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DeleteTaskUseCaseTest {

    private lateinit var deletedTasks: MutableList<Task>
    private lateinit var useCase: DeleteTaskUseCase

    @Before
    fun setUp() {
        deletedTasks = mutableListOf()
        val fakeRepository = object : TaskRepository {
            override fun getTasks(): Flow<List<Task>> = error("not used")
            override suspend fun addTask(task: Task) = Unit
            override suspend fun deleteTask(task: Task) { deletedTasks.add(task) }
        }
        useCase = DeleteTaskUseCase(fakeRepository)
    }

    @Test
    fun `invoke delegates to repository with the given task`() = runTest {
        val task = Task(title = "Buy milk", description = "2% fat")
        useCase(task)
        assertEquals(1, deletedTasks.size)
        assertEquals(task, deletedTasks.first())
    }

    @Test
    fun `invoke passes the exact task to repository`() = runTest {
        val task = Task(id = 42, title = "Read book", description = "Chapter 3")
        useCase(task)
        assertEquals(task.id, deletedTasks.first().id)
        assertEquals(task.title, deletedTasks.first().title)
        assertEquals(task.description, deletedTasks.first().description)
    }

    @Test
    fun `invoke can delete multiple different tasks`() = runTest {
        val task1 = Task(id = 1, title = "Task 1")
        val task2 = Task(id = 2, title = "Task 2")
        useCase(task1)
        useCase(task2)
        assertEquals(2, deletedTasks.size)
    }

    @Test
    fun `invoke calls repository each time when deleting same task twice`() = runTest {
        val task = Task(id = 1, title = "Duplicate deletion")
        useCase(task)
        useCase(task)
        assertEquals(2, deletedTasks.size)
        assertEquals(task, deletedTasks[0])
        assertEquals(task, deletedTasks[1])
    }
}
