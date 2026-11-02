package com.practicum.fitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.common.GradientBackground
import com.practicum.fitapp.presentation.training.TrainingViewModel
import com.practicum.fitapp.presentation.training.builtInPrograms
import com.practicum.fitapp.ui.theme.FitAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = androidx.activity.SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        // Отключаем принудительный контраст для системных панелей
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }

        setContent {
            FitAppTheme {
                val isDarkTheme = false // Всегда светлая тема

                GradientBackground(isDarkTheme = isDarkTheme) {
                    val navController = rememberNavController()
                    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                        bottomBar = {
                            // Прячем нижнее меню, когда мы внутри справочника/деталей
                            if (currentRoute in listOf("community", "training", "nutrition", "journal", "profile")) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .navigationBarsPadding()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    GlassCard(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(30.dp),
                                        glassColor = Color.White.copy(alpha = 0.7f),
                                        hasShadow = false,
                                        hasBorder = false
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp),
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            val activeColor = MaterialTheme.colorScheme.primary
                                            val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant

                                            @Composable
                                            fun NavIcon(route: String, filledIcon: ImageVector, outlinedIcon: ImageVector, label: String) {
                                                val isSelected = currentRoute == route
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxHeight()
                                                        .clickable(
                                                            interactionSource = remember { MutableInteractionSource() },
                                                            indication = null
                                                        ) {
                                                            navController.navigate(route) {
                                                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                                                launchSingleTop = true
                                                                restoreState = true
                                                            }
                                                        },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = if (isSelected) filledIcon else outlinedIcon,
                                                        contentDescription = label,
                                                        tint = if (isSelected) activeColor else inactiveColor
                                                    )
                                                }
                                            }

                                            NavIcon("community", Icons.Filled.EmojiEvents, Icons.Outlined.EmojiEvents, "Достижения")
                                            NavIcon("training", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, "Тренировка")
                                            NavIcon("nutrition", Icons.Filled.Restaurant, Icons.Outlined.Restaurant, "Питание")
                                            NavIcon("journal", Icons.Filled.BarChart, Icons.Outlined.BarChart, "Статистика")
                                            NavIcon("profile", Icons.Filled.Person, Icons.Outlined.Person, "Профиль")
                                        }
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        // Навигационный хост со всеми экранами приложения
                        NavHost(
                            navController = navController,
                            startDestination = "training",
                            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
                        ) {
                            // --- 1. Достижения ---
                            composable("community") {
                                CommunityScreen()
                            }

                            // --- 2. Тренировки (Главный экран) ---
                            composable("training") {
                                TrainingScreen(
                                    onWorkoutClick = { workoutId ->
                                        navController.navigate("workout_details/$workoutId")
                                    },
                                    onCatalogClick = {
                                        navController.navigate("program_catalog")
                                    }
                                )
                            }

                            // --- Детали тренировки ---
                            composable(
                                route = "workout_details/{workoutId}",
                                arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
                            ) { backStackEntry ->
                                val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
                                WorkoutDetailsScreen(
                                    workoutId = workoutId,
                                    onBack = { navController.popBackStack() },
                                    onExerciseClick = { workoutExerciseId ->
                                        navController.navigate("exercise/$workoutExerciseId")
                                    },
                                    onAddExerciseClick = {
                                        navController.navigate("exercise_catalog/$workoutId")
                                    }
                                )
                            }

                            // --- Экран упражнения (Подходы) ---
                            composable(
                                route = "exercise/{workoutExerciseId}",
                                arguments = listOf(navArgument("workoutExerciseId") { type = NavType.LongType })
                            ) { backStackEntry ->
                                val workoutExerciseId = backStackEntry.arguments?.getLong("workoutExerciseId") ?: 0L
                                ExerciseScreen(
                                    workoutExerciseId = workoutExerciseId,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            // --- Каталог упражнений (Выбор упражнения) ---
                            composable(
                                route = "exercise_catalog/{workoutId}",
                                arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
                            ) { backStackEntry ->
                                ExerciseCatalogScreen(
                                    onBack = { navController.popBackStack() },
                                    onExerciseAdded = { navController.popBackStack() }
                                )
                            }

                            // --- Каталог программ тренировок ---
                            composable("program_catalog") {
                                ProgramCatalogScreen(
                                    onBack = { navController.popBackStack() },
                                    onProgramClick = { program ->
                                        navController.navigate("program_details/${program.id}")
                                    }
                                )
                            }

                            // --- Детали программы тренировок ---
                            composable("program_details/{programId}") { backStackEntry ->
                                val programId = backStackEntry.arguments?.getString("programId")
                                val program = builtInPrograms.find { it.id == programId }

                                if (program != null) {
                                    val trainingViewModel: TrainingViewModel = hiltViewModel()
                                    ProgramDetailsScreen(
                                        program = program,
                                        onBack = { navController.popBackStack() },
                                        onAddToDiary = { selectedDay ->
                                            trainingViewModel.addProgramDay(program, selectedDay)
                                            navController.popBackStack("training", inclusive = false)
                                        }
                                    )
                                }
                            }

                            // --- 3. Питание ---
                            composable("nutrition") { backStackEntry ->
                                NutritionScreen(
                                    onAddFood = { navController.navigate("food_catalog") },
                                    savedStateHandle = backStackEntry.savedStateHandle
                                )
                            }

                            // --- Справочник продуктов ---
                            composable("food_catalog") {
                                FoodCatalogScreen(
                                    onBack = { navController.popBackStack() },
                                    onProductSelected = { product ->
                                        navController.previousBackStackEntry?.savedStateHandle?.set("selectedProductId", product.id)
                                        navController.popBackStack()
                                    }
                                )
                            }

                            // --- 4. Журнал ---
                            composable("journal") {
                                JournalScreen()
                            }

                            // --- 5. Профиль ---
                            composable("profile") {
                                ProfileScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}