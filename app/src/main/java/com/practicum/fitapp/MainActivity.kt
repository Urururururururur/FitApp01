package com.practicum.fitapp

// ДОБАВЛЕНЫ ВСЕ НЕОБХОДИМЫЕ ИМПОРТЫ ЭКРАНОВ
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.practicum.fitapp.data.repository.UserRepository
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.common.GradientBackground
import com.practicum.fitapp.presentation.dashboard.DashboardScreen
import com.practicum.fitapp.presentation.training.TrainingViewModel
import com.practicum.fitapp.presentation.training.builtInPrograms
import com.practicum.fitapp.ui.theme.BgColor
import com.practicum.fitapp.ui.theme.FitAppTheme
import com.practicum.fitapp.ui.theme.PrimaryGreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userRepository: UserRepository

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.isStatusBarContrastEnforced = false
        }

        setContent {
            FitAppTheme {
                val isDarkTheme = false

                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    val profile = userRepository.getProfile().first()
                    startDestination = if (profile != null) "dashboard" else "welcome"
                }

                if (startDestination == null) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(BgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryGreen)
                    }
                } else {
                    GradientBackground(isDarkTheme = isDarkTheme) {
                        val navController = rememberNavController()
                        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            containerColor = Color.Transparent,
                            bottomBar = {
                                // Показываем меню только на 4 главных экранах
                                if (currentRoute in listOf("dashboard", "nutrition", "training", "journal")) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .navigationBarsPadding()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        GlassCard(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(30.dp)
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
                                                                if (route == "dashboard") {
                                                                    // Если нажали на Домик — просто возвращаемся к нему, удаляя всё лишнее
                                                                    navController.popBackStack("dashboard", inclusive = false)
                                                                } else {
                                                                    // Для остальных вкладок используем стандартную навигацию
                                                                    navController.navigate(route) {
                                                                        popUpTo("dashboard") { saveState = true }
                                                                        launchSingleTop = true
                                                                        restoreState = true
                                                                    }
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

                                                NavIcon("dashboard", Icons.Filled.Home, Icons.Outlined.Home, "Главная")
                                                NavIcon("nutrition", Icons.Filled.Restaurant, Icons.Outlined.Restaurant, "Питание")
                                                NavIcon("training", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, "Тренировки")
                                                NavIcon("journal", Icons.Filled.List, Icons.Outlined.List, "Журнал")
                                            }
                                        }
                                    }
                                }
                            }
                        ) { innerPadding ->
                            NavHost(
                                navController = navController,
                                startDestination = startDestination!!,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                // --- ПЕРВЫЙ ЗАПУСК ---
                                composable("welcome") {
                                    WelcomeScreen(
                                        onStartClick = {
                                            navController.navigate("onboarding") {
                                                popUpTo("welcome") { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                composable("onboarding") {
                                    OnboardingScreen(
                                        onComplete = {
                                            navController.navigate("dashboard") {
                                                popUpTo("onboarding") { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                // --- ОСНОВНЫЕ ЭКРАНЫ ---
                                composable("dashboard") {
                                    DashboardScreen(
                                        onNavigateToNutrition = { navController.navigate("nutrition") },
                                        onNavigateToWorkout = { navController.navigate("training") },
                                        onNavigateToJournal = { navController.navigate("journal") },
                                        onNavigateToProfile = { navController.navigate("profile") },
                                        onNavigateToCommunity = { navController.navigate("community") }
                                    )
                                }

                                composable("nutrition") { backStackEntry ->
                                    NutritionScreen(
                                        onAddFood = { mealType ->
                                            navController.navigate("food_catalog/$mealType")
                                        },
                                        onNavigateToRecipes = {
                                            navController.navigate("recipe_catalog")
                                        },
                                        savedStateHandle = backStackEntry.savedStateHandle
                                    )
                                }

                                composable(
                                    route = "food_catalog/{mealType}",
                                    arguments = listOf(navArgument("mealType") { type = NavType.StringType })
                                ) { backStackEntry ->
                                    val mealType = backStackEntry.arguments?.getString("mealType") ?: "OTHER"
                                    FoodCatalogScreen(
                                        onBack = { navController.popBackStack() },
                                        onProductSelected = { product ->
                                            navController.previousBackStackEntry?.savedStateHandle?.set("selectedProductId", product.id)
                                            navController.previousBackStackEntry?.savedStateHandle?.set("selectedMealType", mealType)
                                            navController.popBackStack()
                                        }
                                    )
                                }

                                // --- Каталог рецептов ---
                                composable("recipe_catalog") {
                                    val nutritionViewModel: com.practicum.fitapp.presentation.nutrition.NutritionViewModel = hiltViewModel()

                                    RecipeCatalogScreen(
                                        onBack = { navController.popBackStack() },
                                        onAddRecipeToDiary = { recipe, grams ->
                                            // Сохраняем рецепт в сегодняшний день (пока без привязки к конкретному приему пищи)
                                            val todayTimestamp = java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

                                            // Используем метод из твоей ViewModel
                                            nutritionViewModel.addRecipeToDiary(
                                                recipe = recipe,
                                                dateTimestamp = todayTimestamp,
                                                grams = grams
                                            )
                                        }
                                    )
                                }

                                composable("training") {
                                    TrainingScreen(
                                        onWorkoutClick = { workoutId -> navController.navigate("workout_details/$workoutId") },
                                        onCatalogClick = { navController.navigate("program_catalog") }
                                    )
                                }

                                composable(
                                    route = "workout_details/{workoutId}",
                                    arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
                                ) { backStackEntry ->
                                    val workoutId = backStackEntry.arguments?.getLong("workoutId") ?: 0L
                                    WorkoutDetailsScreen(
                                        workoutId = workoutId,
                                        onBack = { navController.popBackStack() },
                                        onExerciseClick = { workoutExerciseId -> navController.navigate("exercise/$workoutExerciseId") },
                                        onAddExerciseClick = { navController.navigate("exercise_catalog/$workoutId") }
                                    )
                                }

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

                                composable(
                                    route = "exercise_catalog/{workoutId}",
                                    arguments = listOf(navArgument("workoutId") { type = NavType.LongType })
                                ) { backStackEntry ->
                                    ExerciseCatalogScreen(
                                        onBack = { navController.popBackStack() },
                                        onExerciseAdded = { navController.popBackStack() }
                                    )
                                }

                                composable("program_catalog") {
                                    ProgramCatalogScreen(
                                        onBack = { navController.popBackStack() },
                                        onProgramClick = { program -> navController.navigate("program_details/${program.id}") }
                                    )
                                }

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

                                composable("profile") {
                                    ProfileScreen(onBack = { navController.popBackStack() })
                                }

                                composable("community") {
                                    CommunityScreen(onBack = { navController.popBackStack() })
                                }

                                composable("journal") {
                                    JournalScreen()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}