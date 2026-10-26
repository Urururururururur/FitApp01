package com.practicum.fitapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.fitapp.presentation.training.CatalogProgram
import com.practicum.fitapp.presentation.training.builtInPrograms

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramCatalogScreen(
    onBack: () -> Unit,
    onProgramClick: (CatalogProgram) -> Unit
) {
    var selectedLevel by remember { mutableStateOf<String?>(null) }

    val filteredPrograms = builtInPrograms.filter { program ->
        selectedLevel == null || program.level == selectedLevel
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Каталог программ") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Фильтр по уровню
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedLevel == null,
                    onClick = { selectedLevel = null },
                    label = { Text("Все") }
                )
                listOf("Новичок", "Средний", "Продвинутый").forEach { level ->
                    FilterChip(
                        selected = selectedLevel == level,
                        onClick = { selectedLevel = level },
                        label = { Text(level) }
                    )
                }
            }

            LazyColumn {
                items(filteredPrograms) { program ->
                    ProgramCard(program = program, onClick = { onProgramClick(program) })
                }
            }
        }
    }
}

@Composable
private fun ProgramCard(program: CatalogProgram, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(program.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(program.subtitle, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("${program.level} · ${program.gender} · ${program.goal}", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Дней: ${program.days.size}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}