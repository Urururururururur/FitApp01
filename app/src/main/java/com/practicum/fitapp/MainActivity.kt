package com.practicum.fitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.fitapp.ui.theme.FitAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userName = "Андрей"
            FitAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = userName,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
/**
 * Считает индекс массы тела.
 * weightKg — вес в килограммах
 * heightM  — рост в метрах
 * Возвращает число с двумя знаками после запятой.
 */
fun calculateBmi(weightKg: Double, heightM: Double): Double {
    val bmi = weightKg / (heightM * heightM)
    return Math.round(bmi * 100.0) / 100.0
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "FitApp",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Привет, $name!")
        Spacer(modifier = Modifier.height(32.dp))

// Создаём state: счётчик подходов, изначально 0
        var count by remember { mutableStateOf(0) }

        Text(text = "Подходов: $count", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { count++ }) {
            Text("Сделал подход")
        }
        // Стейт для ввода веса и роста (храним как строку — так удобнее для TextField)
        var weightInput by remember { mutableStateOf("70") }
        var heightInput by remember { mutableStateOf("1.75") }

// Поле ввода веса
        OutlinedTextField(
            value = weightInput,
            onValueChange = { weightInput = it },
            label = { Text("Вес, кг") }
        )

        Spacer(modifier = Modifier.height(8.dp))

// Поле ввода роста
        OutlinedTextField(
            value = heightInput,
            onValueChange = { heightInput = it },
            label = { Text("Рост, м") }
        )
        val weight = weightInput.toDoubleOrNull() ?: 0.0
        val height = heightInput.toDoubleOrNull() ?: 0.0
        val bmi = if (height > 0) calculateBmi(weight, height) else 0.0
        Text(text = "ИМТ: $bmi", fontSize = 20.sp)
        val category = when {
            bmi < 18.5 -> "Недостаточный вес"
            bmi < 25 -> "Норма"
            bmi < 30 -> "Избыточный вес"
            else -> "Ожирение"
        }
        Text(text = "Категория: $category", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Text(text = "Дневник питания", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

// Изменяемый список блюд. mutableStateListOf — как mutableStateOf,
// только для списков: изменение списка тоже вызывает рекомпозицию.
        val meals = remember { mutableStateListOf(
            "Овсянка — 350 ккал",
            "Куриная грудка — 200 ккал"
        ) }

        var newMeal by remember { mutableStateOf("") }

        OutlinedTextField(
            value = newMeal,
            onValueChange = { newMeal = it },
            label = { Text("Новое блюдо") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (newMeal.isNotBlank()) {
                    meals.add(newMeal)
                    newMeal = ""   // очищаем поле после добавления
                }
            }
        ) {
            Text("Добавить")
        }

        LazyColumn {
            items(meals) { meal ->
                Text(
                    text = meal,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FitAppTheme {
        Greeting("Android")
    }
}