package com.practicum.fitapp.presentation.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.practicum.fitapp.presentation.common.rememberDrawablePainter
import com.practicum.fitapp.ui.theme.PrimaryGreen
import com.practicum.fitapp.ui.theme.TextPrimary
import com.practicum.fitapp.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    onBack: () -> Unit,
    onNavigateToNutrition: () -> Unit, // <-- ДОБАВИЛИ ДЛЯ ПЕРЕХОДА В ДНЕВНИК
    viewModel: RecipeDetailsViewModel = hiltViewModel()
) {
    val recipeState by viewModel.recipeState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Для Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Переменная для ввода граммовки в BottomSheet
    var inputWeight by remember { mutableStateOf("") }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "Добавить в дневник",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Поле ввода граммовки
                OutlinedTextField(
                    value = inputWeight,
                    onValueChange = { inputWeight = it.filter { char -> char.isDigit() || char == '.' } },
                    label = { Text("Вес порции (г)") },
                    placeholder = { Text("Например: 250") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    singleLine = true
                )

                // Функция-помощник для отправки
                fun submit(mealType: String) {
                    val weight = inputWeight.toDoubleOrNull() ?: 0.0
                    if (weight <= 0) return // Защита от пустого ввода

                    // Передаем граммовку: вес_порции_в_рецепте = 1.0 порция
                    // Вес в рецепте: weight / singleServingWeight
                    val singleServing = recipeState?.recipe?.let { it.totalWeightGrams.toDouble() / it.servings } ?: 100.0
                    val servingsCount = weight / singleServing

                    viewModel.addRecipeToDiary(mealType, servingsCount) {
                        showBottomSheet = false
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Рецепт добавлен в ${getMealTypeName(mealType)}!",
                                actionLabel = "В дневник",
                                duration = SnackbarDuration.Long
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                onNavigateToNutrition() // Переходим в питание
                            }
                        }
                    }
                }

                MealTypeButton("Завтрак") { submit("BREAKFAST") }
                MealTypeButton("Обед") { submit("LUNCH") }
                MealTypeButton("Ужин") { submit("DINNER") }
                MealTypeButton("Перекус") { submit("SNACK") }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        snackbarHost = { SnackbarHost(snackbarHostState) }, // <-- ДОБАВИЛИ SNACKBAR
        topBar = {
            TopAppBar(
                title = { Text("Рецепт", color = TextPrimary, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            if (recipeState != null) {
                ExtendedFloatingActionButton(
                    onClick = {
                        // Подставляем стандартный вес одной порции при открытии
                        val singleServing = recipeState?.recipe?.let { it.totalWeightGrams.toDouble() / it.servings } ?: 100.0
                        inputWeight = singleServing.toInt().toString()
                        showBottomSheet = true
                    },
                    containerColor = PrimaryGreen,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("В дневник") }
                )
            }
        }
    ) { paddingValues ->
        val recipeWithIngredients = recipeState

        if (recipeWithIngredients == null) {
            Box(Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else {
            val recipe = recipeWithIngredients.recipe
            val ingredients = recipeWithIngredients.ingredients

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // 1. Картинка
                item {
                    val painter = rememberDrawablePainter(recipe.imageUrl)
                    if (painter != null) {
                        androidx.compose.foundation.Image(
                            painter = painter,
                            contentDescription = recipe.title,
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else if (recipe.imageUrl != null) {
                        AsyncImage(
                            model = recipe.imageUrl,
                            contentDescription = recipe.title,
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // 2. Заголовок и описание
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = recipe.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = recipe.description, fontSize = 14.sp, color = TextSecondary, lineHeight = 20.sp)
                    }
                }

                // 3. БЖУ
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F5F5))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                InfoItem("Калории", "${recipe.calories}")
                                InfoItem("Белки", "${recipe.protein} г")
                                InfoItem("Жиры", "${recipe.fat} г")
                                InfoItem("Углеводы", "${recipe.carbs} г")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = Color.Black.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${recipe.cookTimeMinutes} мин", fontSize = 14.sp, color = TextSecondary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${recipe.servings} порции", fontSize = 14.sp, color = TextSecondary)
                                }
                                Text("Вес: ${recipe.totalWeightGrams} г", fontSize = 14.sp, color = TextSecondary)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 4. Ингредиенты
                item {
                    Text("Ингредиенты", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, bottom = 8.dp), color = TextPrimary)
                }
                items(ingredients) { ingredient ->
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(ingredient.productName, fontSize = 15.sp, color = TextPrimary)
                        Text("${ingredient.grams} г", fontSize = 15.sp, color = TextSecondary)
                    }
                }

                // 5. Инструкция
                item {
                    Text("Инструкция", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp), color = TextPrimary)
                }
                items(recipe.instructions.split("\n")) { step ->
                    if (step.isNotBlank()) {
                        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            Text("• ", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                            Text(step, fontSize = 15.sp, color = TextPrimary, lineHeight = 20.sp)
                        }
                    }
                }
            }
        }
    }
}

// Функция для красивого названия приема пищи
fun getMealTypeName(type: String): String {
    return when (type) {
        "BREAKFAST" -> "Завтрак"
        "LUNCH" -> "Обед"
        "DINNER" -> "Ужин"
        "SNACK" -> "Перекус"
        else -> "Прием пищи"
    }
}

@Composable
fun MealTypeButton(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(16.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryGreen)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 12.sp, color = TextSecondary)
    }
}