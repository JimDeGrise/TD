package com.example.td.domain.usecase

import com.example.td.domain.model.Task
import com.example.td.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AddTaskUseCaseTest {

    private lateinit var addedTasks: MutableList<Task>
    private lateinit var useCase: AddTaskUseCase

    @Before
    fun setUp() {
        addedTasks = mutableListOf()
        val fakeRepository = object : TaskRepository {
            override fun getTasks(): Flow<List<Task>> = error("not used")
            override suspend fun addTask(task: Task) { addedTasks.add(task) }
            override suspend fun deleteTask(task: Task) = Unit
        }
        useCase = AddTaskUseCase(fakeRepository)
    }

    @Test
    fun `invoke delegates to repository with the given task`() = runTest {
        val task = Task(title = "Buy milk", description = "2% fat")
        useCase(task)
        assertEquals(1, addedTasks.size)
        assertEquals(task, addedTasks.first())
    }

    @Test
    fun `invoke stores title and description correctly`() = runTest {
        val task = Task(title = "Read book", description = "Chapter 3")
        useCase(task)
        assertEquals("Read book", addedTasks.first().title)
        assertEquals("Chapter 3", addedTasks.first().description)
    }

    @Test
    fun `invoke stores task with empty description by default`() = runTest {
        val task = Task(title = "Quick note")
        useCase(task)
        assertEquals("", addedTasks.first().description)
    }

    @Test
    fun `invoke can be called multiple times`() = runTest {
        useCase(Task(title = "Task 1"))
        useCase(Task(title = "Task 2"))
        useCase(Task(title = "Task 3"))
        assertEquals(3, addedTasks.size)
    }
}
