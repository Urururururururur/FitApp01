package com.practicum.fitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.practicum.fitapp.data.local.FitAppDatabase
import com.practicum.fitapp.data.local.ProductEntity
import com.practicum.fitapp.ui.theme.FitAppTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Тестовая проверка, что Room работает.
// Позже уберём — код должен быть только в ViewModel.
        lifecycleScope.launch {
            val db = FitAppDatabase.getInstance(applicationContext)
            val dao = db.productDao()

            // Вставляем тестовый продукт
            dao.insert(
                ProductEntity(
                    name = "Яблоко",
                    caloriesPer100g = 52,
                    proteinPer100g = 0.3,
                    fatPer100g = 0.2,
                    carbsPer100g = 14.0
                )
            )

            // Читаем первые значения из потока и логируем
            val products = dao.getAll().first()
            android.util.Log.d("FitAppDB", "Продуктов в базе: ${products.size}")
            products.forEach { p ->
                android.util.Log.d("FitAppDB", "${p.id}: ${p.name} — ${p.caloriesPer100g} ккал")
            }
        }
        setContent {
            FitAppTheme {
                val navController = rememberNavController()

                // Следим за текущим экраном, чтобы подсветить активную вкладку в меню
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute == "community",
                                onClick = { navController.navigate("community") },
                                icon = { Icon(Icons.Default.Star, contentDescription = null) },
                                label = { Text("Сообщество") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "training",
                                onClick = { navController.navigate("training") },
                                icon = { Icon(Icons.Default.Home, contentDescription = null) },
                                label = { Text("Тренировка") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "nutrition",
                                onClick = { navController.navigate("nutrition") },
                                icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                                label = { Text("Питание") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "journal",
                                onClick = { navController.navigate("journal") },
                                icon = { Icon(Icons.Default.List, contentDescription = null) },
                                label = { Text("Журнал") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "profile",
                                onClick = { navController.navigate("profile") },
                                icon = { Icon(Icons.Default.Person, contentDescription = null) },
                                label = { Text("Профиль") }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "training",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("community") { CommunityScreen() }
                        composable("training") { TrainingScreen() }
                        composable("nutrition") { NutritionScreen() }
                        composable("journal") { JournalScreen() }
                        composable("profile") { ProfileScreen() }
                    }
                }
            }
        }
    }
}
@Composable
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
