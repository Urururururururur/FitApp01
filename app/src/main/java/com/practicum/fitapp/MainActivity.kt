package com.practicum.fitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Surface
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents

import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.EmojiEvents

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
                        // Прячем нижнее меню, когда мы внутри справочника/деталей
                        if (currentRoute in listOf("community", "training", "nutrition", "journal", "profile")) {

                            // Используем Surface для легкого фона и верхней границы
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 0.dp,
                                tonalElevation = 0.dp
                            ) {
                                // Тонкая линия-разделитель сверху
                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                                )

                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    tonalElevation = 0.dp,
                                    modifier = Modifier.height(64.dp) // Делаем бар чуть тоньше, раз нет текста
                                ) {
                                    val navItemColors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = Color.Transparent,
                                        unselectedIconColor = Color.Gray
                                    )

                                    NavigationBarItem(
                                        selected = currentRoute == "community",
                                        onClick = { navController.navigate("community") },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentRoute == "community") Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents,
                                                contentDescription = "Достижения"
                                            )
                                        },
                                        colors = navItemColors
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == "training",
                                        onClick = { navController.navigate("training") },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentRoute == "training") Icons.Filled.FitnessCenter else Icons.Outlined.FitnessCenter,
                                                contentDescription = "Тренировка"
                                            )
                                        },
                                        colors = navItemColors
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == "nutrition",
                                        onClick = { navController.navigate("nutrition") },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentRoute == "nutrition") Icons.Filled.Restaurant else Icons.Outlined.Restaurant,
                                                contentDescription = "Питание"
                                            )
                                        },
                                        colors = navItemColors
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == "journal",
                                        onClick = { navController.navigate("journal") },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentRoute == "journal") Icons.Filled.BarChart else Icons.Outlined.BarChart,
                                                contentDescription = "Статистика"
                                            )
                                        },
                                        colors = navItemColors
                                    )
                                    NavigationBarItem(
                                        selected = currentRoute == "profile",
                                        onClick = { navController.navigate("profile") },
                                        icon = {
                                            Icon(
                                                imageVector = if (currentRoute == "profile") Icons.Filled.Person else Icons.Outlined.Person,
                                                contentDescription = "Профиль"
                                            )
                                        },
                                        colors = navItemColors
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "training",
                        modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                    ) {
                        composable("community") { CommunityScreen() }
                        composable("training") {
                            TrainingScreen(
                                onWorkoutClick = { workoutId ->
                                    navController.navigate("workout_details/$workoutId")
                                },
                                onCatalogClick = { // ← ДОБАВЛЯЕМ ПЕРЕХОД
                                    navController.navigate("program_catalog")
                                }
                            )
                        }
                        composable(
                            route = "exercise_catalog/{workoutId}",
                            arguments = listOf(androidx.navigation.navArgument("workoutId") { type = androidx.navigation.NavType.LongType })
                        ) { backStackEntry ->
                            ExerciseCatalogScreen(
                                onBack = { navController.popBackStack() },
                                onExerciseAdded = { navController.popBackStack() }
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
                                },
                                onAddExerciseClick = { //
                                    navController.navigate("exercise_catalog/$workoutId")
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
                        composable("program_catalog") {
                            ProgramCatalogScreen(
                                onBack = { navController.popBackStack() },
                                onProgramClick = { program ->
                                    navController.navigate("program_details/${program.id}")
                                }
                            )
                        }

                        composable("program_details/{programId}") { backStackEntry ->
                            val programId = backStackEntry.arguments?.getString("programId")
                            val program = com.practicum.fitapp.presentation.training.builtInPrograms.find { it.id == programId }

                            if (program != null) {
                                // Получаем TrainingViewModel, чтобы сохранить программу в БД
                                val trainingViewModel: com.practicum.fitapp.presentation.training.TrainingViewModel =
                                    androidx.hilt.navigation.compose.hiltViewModel()

                                ProgramDetailsScreen(
                                    program = program,
                                    onBack = { navController.popBackStack() },
                                    onAddToDiary = { selectedDay ->
                                        // Сохраняем день в БД
                                        trainingViewModel.addProgramDay(program, selectedDay)
                                        // Возвращаемся на главный экран тренировок
                                        navController.popBackStack("training", inclusive = false)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}