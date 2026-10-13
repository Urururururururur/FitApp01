package com.practicum.fitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
            FitAppTheme {
                // Храним текущий экран. null — главный, "second" — второй.
                var currentScreen by remember { mutableStateOf<String?>(null) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    when (currentScreen) {
                        null -> {
                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                Greeting(name = "Андрей")
                                Button(onClick = { currentScreen = "second" }) {
                                    Text("Перейти на второй экран")
                                }
                            }
                        }
                        else -> SecondScreen(onBack = { currentScreen = null })
                    }
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
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "FitApp", fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Привет, $name!")

        Spacer(modifier = Modifier.height(32.dp))
        WorkoutCounterSection()

        Spacer(modifier = Modifier.height(32.dp))
        BmiSection()

        Spacer(modifier = Modifier.height(32.dp))
        MealListSection()
    }
}
@Composable
fun WorkoutCounterSection() {
    var count by remember { mutableStateOf(0) }

    Column {
        Text(text = "Подходов: $count", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { count++ }) {
            Text("Сделал подход")
        }
    }
}

@Composable
fun BmiSection() {
    var weightInput by remember { mutableStateOf("70") }
    var heightInput by remember { mutableStateOf("1.75") }

    val weight = weightInput.toDoubleOrNull() ?: 0.0
    val height = heightInput.toDoubleOrNull() ?: 0.0
    val bmi = if (height > 0) calculateBmi(weight, height) else 0.0
    val category = when {
        bmi < 18.5 -> "Недостаточный вес"
        bmi < 25 -> "Норма"
        bmi < 30 -> "Избыточный вес"
        else -> "Ожирение"
    }

    Column {
        OutlinedTextField(
            value = weightInput,
            onValueChange = { weightInput = it },
            label = { Text("Вес, кг") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = heightInput,
            onValueChange = { heightInput = it },
            label = { Text("Рост, м") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "ИМТ: $bmi", fontSize = 20.sp)
        Text(text = "Категория: $category", fontSize = 18.sp)
    }
}

@Composable
fun MealListSection() {
    val meals = remember { mutableStateListOf(
        "Овсянка — 350 ккал",
        "Куриная грудка — 200 ккал"
    ) }
    var newMeal by remember { mutableStateOf("") }

    Column {
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
                    newMeal = ""
                }
            }
        ) {
            Text("Добавить")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column {
            meals.forEach { meal ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = meal, fontSize = 18.sp)
                    TextButton(onClick = { meals.remove(meal) }) {
                        Text("✕")
                    }
                }
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