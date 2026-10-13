package com.practicum.fitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FitAppTheme {
        Greeting("Android")
    }
}