package com.practicum.fitapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.nutrition.Recipe
import com.practicum.fitapp.presentation.nutrition.builtInRecipes
import com.practicum.fitapp.ui.theme.*
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCatalogScreen(
    onBack: () -> Unit,
    // Лямбда, которая будет вызываться при добавлении в дневник (передает сам рецепт и граммовку)
    onAddRecipeToDiary: (Recipe, Double) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    var recipeGrams by remember { mutableStateOf("") }

    val hazeState = remember { HazeState() }

    val filteredRecipes = builtInRecipes.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        Image(
            painter = painterResource(id = com.practicum.fitapp.R.drawable.app_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().hazeSource(hazeState),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Каталог рецептов",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                // Поиск
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(30.dp),
                    hazeState = hazeState
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Поиск рецепта...", color = TextSecondary, fontFamily = Manrope) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )
                }

                // Список рецептов
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 100.dp)
                ) {
                    items(filteredRecipes) { recipe ->
                        RecipeCard(
                            recipe = recipe,
                            onClick = {
                                selectedRecipe = recipe
                                recipeGrams = recipe.totalWeightGrams.toString() // По умолчанию берем вес всего блюда
                            },
                            hazeState = hazeState
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }

    // ДИАЛОГ РЕЦЕПТА
    if (selectedRecipe != null) {
        val recipe = selectedRecipe!!
        Dialog(onDismissRequest = { selectedRecipe = null }) {
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(24.dp),
                hazeState = hazeState
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(recipe.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(recipe.description, fontSize = 14.sp, color = TextSecondary, fontFamily = Manrope)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Состав
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        item {
                            Text("Ингредиенты:", fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        items(recipe.ingredients) { ing ->
                            Text("${ing.productName} — ${ing.grams} г", fontSize = 13.sp, color = TextSecondary, fontFamily = Manrope)
                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Приготовление:", fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        items(recipe.steps) { step ->
                            Text(step, fontSize = 13.sp, color = TextSecondary, fontFamily = Manrope, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ввод граммовки
                    OutlinedTextField(
                        value = recipeGrams,
                        onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) recipeGrams = v },
                        label = { Text("Порция, грамм", color = TextSecondary, fontFamily = Manrope) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = TextSecondary,
                            focusedContainerColor = Color.White.copy(alpha = 0.5f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.3f)
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { selectedRecipe = null }) {
                            Text("Отмена", color = TextSecondary, fontFamily = Manrope)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val grams = recipeGrams.toDoubleOrNull() ?: recipe.totalWeightGrams
                                onAddRecipeToDiary(recipe, grams)
                                selectedRecipe = null
                                onBack() // Возвращаемся на экран питания после добавления
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Добавить", color = Color.White, fontFamily = Manrope)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeCard(recipe: Recipe, onClick: () -> Unit, hazeState: HazeState) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        hazeState = hazeState
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Иконка-заглушка (вместо картинки)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Restaurant, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(32.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(recipe.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                Text(recipe.description, fontSize = 12.sp, color = TextSecondary, fontFamily = Manrope, maxLines = 2)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${"%.0f".format(recipe.totalsKcal)} ккал · Б${"%.0f".format(recipe.totalsProtein)} Ж${"%.0f".format(recipe.totalsFat)} У${"%.0f".format(recipe.totalsCarbs)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryGreen,
                    fontFamily = Manrope
                )
            }
        }
    }
}