package com.practicum.fitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.practicum.fitapp.ui.theme.FitAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
