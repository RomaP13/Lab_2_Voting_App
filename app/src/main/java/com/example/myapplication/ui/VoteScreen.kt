package com.example.myapplication.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.viewmodel.VoteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoteScreen(viewModel: VoteViewModel = viewModel()) {
    val options by viewModel.options.collectAsState()
    val leader by viewModel.leader.collectAsState()
    val totalVotes by viewModel.totalVotes.collectAsState()
    var newOptionText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Рейтинг / Голосування") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    // Кнопка скидання результатів у верхньому правому куті
                    IconButton(onClick = { viewModel.resetVotes() }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Скинути голоси")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Блок статистики (Лідер + Загальна кількість)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (leader == null) {
                        Text("Голосування ще не розпочато", style = MaterialTheme.typography.titleMedium)
                    } else {
                        Text("🏆 Лідер: ${leader?.title}", style = MaterialTheme.typography.titleLarge)
                        Text("Всього голосів віддано: $totalVotes", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Поле введення
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newOptionText,
                    onValueChange = { newOptionText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Новий варіант...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.addOption(newOptionText)
                        newOptionText = ""
                    },
                    enabled = newOptionText.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp) // Вирівнюємо по висоті з текстовим полем
                ) {
                    Text("Додати")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Список варіантів
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(items = options, key = { it.id }) { option ->
                    VoteCard(
                        title = option.title,
                        votes = option.votes,
                        onVote = { viewModel.vote(option.id) },
                        onDelete = { viewModel.removeOption(option.id) }
                    )
                }
            }
        }
    }
}