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
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.practicum.fitapp.ui.theme.FitAppTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitAppTheme {
                val navController = rememberNavController()
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Прячем нижнее меню, когда мы внутри справочника/тренировки
                        if (currentRoute in listOf("community", "training", "nutrition", "journal", "profile")) {
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
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "training",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("community") { CommunityScreen() }
                        composable("training") {
                            TrainingScreen(
                                onWorkoutClick = { workoutId ->
                                    navController.navigate("workout_details/$workoutId")
                                }
                            )
                        }
                        composable(
                            route = "workout_details/{workoutId}",
                            arguments = listOf(androidx.navigation.navArgument("workoutId") { type = androidx.navigation.NavType.LongType })
                        ) { backStackEntry ->
                            val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
                            WorkoutDetailsScreen(
                                workoutId = workoutId,
                                onBack = { navController.popBackStack() },
                                onExerciseClick = { workoutExerciseId ->
                                    navController.navigate("exercise/$workoutExerciseId")
                                }
                            )
                        }
                        composable(
                            route = "exercise/{workoutExerciseId}",
                            arguments = listOf(androidx.navigation.navArgument("workoutExerciseId") { type = androidx.navigation.NavType.LongType })
                        ) { backStackEntry ->
                            val workoutExerciseId = backStackEntry.arguments?.getLong("workoutExerciseId") ?: 0L
                            ExerciseScreen(
                                workoutExerciseId = workoutExerciseId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("nutrition") { backStackEntry ->
                            NutritionScreen(
                                onAddFood = { navController.navigate("food_catalog") },
                                savedStateHandle = backStackEntry.savedStateHandle
                            )
                        }
                        composable("food_catalog") {
                            FoodCatalogScreen(
                                onBack = { navController.popBackStack() },
                                onProductSelected = { product ->
                                    navController.previousBackStackEntry?.savedStateHandle?.set("selectedProductId", product.id)
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("journal") { JournalScreen() }
                        composable("profile") { ProfileScreen() }
                    }
                }
            }
        }
    }
}