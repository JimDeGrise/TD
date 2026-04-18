package com.example.td

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.td.data.local.database.AppDatabase
import com.example.td.data.repository.TaskRepositoryImpl
import com.example.td.domain.usecase.AddTaskUseCase
import com.example.td.domain.usecase.CompleteTaskUseCase
import com.example.td.domain.usecase.DeleteTaskUseCase
import com.example.td.domain.usecase.GetTasksUseCase
import com.example.td.domain.usecase.UpdateTaskUseCase
import com.example.td.ui.splash.SplashScreen
import com.example.td.ui.task.TaskScreen
import com.example.td.ui.task.TaskViewModel
import com.example.td.ui.theme.TDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val repository = TaskRepositoryImpl(db.taskDao())
        val factory = TaskViewModel.Factory(
            GetTasksUseCase(repository),
            AddTaskUseCase(repository),
            DeleteTaskUseCase(repository),
            CompleteTaskUseCase(repository),
            UpdateTaskUseCase(repository)
        )

        setContent {
            TDTheme {
                var showSplash by rememberSaveable { mutableStateOf(true) }
                if (showSplash) {
                    SplashScreen(onFinished = { showSplash = false })
                } else {
                    val taskViewModel: TaskViewModel = viewModel(factory = factory)
                    TaskScreen(viewModel = taskViewModel)
                }
            }
        }
    }
}

