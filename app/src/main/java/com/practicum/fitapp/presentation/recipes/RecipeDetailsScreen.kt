package com.practicum.fitapp.presentation.recipes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.practicum.fitapp.ui.theme.PrimaryGreen
import com.practicum.fitapp.ui.theme.TextPrimary
import com.practicum.fitapp.ui.theme.TextSecondary
import androidx.compose.foundation.Image
import coil.compose.AsyncImage
import com.practicum.fitapp.presentation.common.rememberDrawablePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    onBack: () -> Unit,
    onAddToDiary: () -> Unit,
    viewModel: RecipeDetailsViewModel = hiltViewModel()
) {
    val recipeState by viewModel.recipeState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White, // <-- БЕЛЫЙ ФОН
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
                    onClick = onAddToDiary,
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
                    if (recipe.imageUrl != null) {
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
                        Text(
                            text = recipe.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = recipe.description,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }

                // 3. БЖУ и характеристики
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFF5F5F5)) // Светло-серый фон вместо стекла
                            .padding(16.dp)
                    ) {
                        Column {
                            // Верхний ряд: БЖУ и Калории
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                InfoItem("Калории", "${recipe.calories}")
                                InfoItem("Белки", "${recipe.protein} г")
                                InfoItem("Жиры", "${recipe.fat} г")
                                InfoItem("Углеводы", "${recipe.carbs} г")
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Divider(color = Color.Black.copy(alpha = 0.1f))
                            Spacer(modifier = Modifier.height(16.dp))

                            // Нижний ряд: Время и Порции (компактно)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                    Text(
                        text = "Ингредиенты",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                        color = TextPrimary
                    )
                }
                items(ingredients) { ingredient ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(ingredient.productName, fontSize = 15.sp, color = TextPrimary)
                        Text("${ingredient.grams} г", fontSize = 15.sp, color = TextSecondary)
                    }
                }

                // 5. Инструкция
                item {
                    Text(
                        text = "Инструкция",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
                        color = TextPrimary
                    )
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

@Composable
fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryGreen)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 12.sp, color = TextSecondary)
    }
}