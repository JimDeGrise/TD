package com.example.td.ui.task

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.td.R
import com.example.td.domain.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val taskCardColors = listOf(
    Color(0xFFE8F5E9), // sage green
    Color(0xFFE3F2FD), // steel blue
    Color(0xFFF3E5F5), // lavender
    Color(0xFFFFF3E0), // peach
    Color(0xFFE0F7FA), // teal
    Color(0xFFFCE4EC)  // rose
)

private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

private fun formatTimestamp(millis: Long): String = dateFormat.format(Date(millis))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val activeTasks by viewModel.activeTasks.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    val selectedTabIndex by viewModel.selectedTabIndex.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    val activeListState = rememberLazyListState()
    val completedListState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        },
        floatingActionButton = {
            if (selectedTabIndex == 0) {
                FloatingActionButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_task))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    text = { Text(stringResource(R.string.tab_active)) }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    text = { Text(stringResource(R.string.tab_completed)) }
                )
            }
            when (selectedTabIndex) {
                0 -> TaskList(
                    tasks = activeTasks,
                    listState = activeListState,
                    emptyText = stringResource(R.string.no_active_tasks),
                    onLongClick = { viewModel.completeTask(it) },
                    onDelete = null,
                    onTaskClick = { selectedTask = it }
                )
                1 -> TaskList(
                    tasks = completedTasks,
                    listState = completedListState,
                    emptyText = stringResource(R.string.no_completed_tasks),
                    onLongClick = null,
                    onDelete = { viewModel.deleteTask(it) },
                    onTaskClick = { selectedTask = it }
                )
            }
        }
    }

    if (showAddDialog) {
        AddTaskDialog(
            onConfirm = { title, description ->
                viewModel.addTask(title, description)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false }
        )
    }

    selectedTask?.let { task ->
        TaskDetailDialog(task = task, onDismiss = { selectedTask = null })
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    listState: LazyListState,
    emptyText: String,
    onLongClick: ((Task) -> Unit)?,
    onDelete: ((Task) -> Unit)?,
    onTaskClick: (Task) -> Unit
) {
    if (tasks.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = emptyText,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
        ) {
            itemsIndexed(tasks, key = { _, task -> task.id }) { index, task ->
                TaskItem(
                    task = task,
                    cardColor = taskCardColors[index % taskCardColors.size],
                    onDelete = onDelete?.let { { it(task) } },
                    onLongClick = onLongClick?.let { { it(task) } },
                    onClick = { onTaskClick(task) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskItem(
    task: Task,
    cardColor: Color,
    onDelete: (() -> Unit)?,
    onLongClick: (() -> Unit)?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClickLabel = stringResource(R.string.view_description),
                role = Role.Button,
                onClick = onClick,
                onLongClickLabel = if (onLongClick != null) stringResource(R.string.mark_as_done) else null,
                onLongClick = onLongClick
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
                Text(
                    text = stringResource(R.string.created_at, formatTimestamp(task.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                task.completedAt?.let { completedAt ->
                    Text(
                        text = stringResource(R.string.completed_at, formatTimestamp(completedAt)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_task)
                    )
                }
            }
        }
    }
}

@Composable
private fun TaskDetailDialog(task: Task, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(task.title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (task.description.isNotBlank()) task.description
                           else stringResource(R.string.no_description)
                )
                Text(
                    text = stringResource(R.string.created_at, formatTimestamp(task.createdAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                task.completedAt?.let { completedAt ->
                    Text(
                        text = stringResource(R.string.completed_at, formatTimestamp(completedAt)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.close))
            }
        }
    )
}

@Composable
private fun AddTaskDialog(onConfirm: (String, String) -> Unit, onDismiss: () -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.new_task),
                    style = MaterialTheme.typography.titleMedium
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.task_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.task_description)) },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.cancel))
                    }
                    TextButton(
                        onClick = { onConfirm(title, description); title = ""; description = "" },
                        enabled = title.isNotBlank()
                    ) {
                        Text(stringResource(R.string.add))
                    }
                }
            }
        }
    }
}

