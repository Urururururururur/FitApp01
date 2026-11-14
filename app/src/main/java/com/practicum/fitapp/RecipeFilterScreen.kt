package com.practicum.fitapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.recipes.RecipeViewModel
import com.practicum.fitapp.ui.theme.*
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun RecipeFilterScreen(
    onBack: () -> Unit,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategoryIds by viewModel.selectedCategoryIds.collectAsStateWithLifecycle()
    val minCalories by viewModel.minCalories.collectAsStateWithLifecycle()
    val maxCalories by viewModel.maxCalories.collectAsStateWithLifecycle()
    val filteredRecipes by viewModel.filteredRecipes.collectAsStateWithLifecycle()

    val hazeState = remember { HazeState() }

    // Локальный стейт для слайдера (использует Float для плавности)
    var sliderRange by remember { mutableStateOf(minCalories.toFloat()..maxCalories.toFloat()) }

    // Синхронизация слайдера при изменении фильтров
    LaunchedEffect(minCalories, maxCalories) {
        sliderRange = minCalories.toFloat()..maxCalories.toFloat()
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
                    title = { Text("Фильтры", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp)
            ) {
                // 1. ПОИСК
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(30.dp), hazeState = hazeState) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
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
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 2. КАЛОРИИ (Слайдер)
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), hazeState = hazeState) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Калорийность: ${sliderRange.start.toInt()} - ${sliderRange.endInclusive.toInt()} ккал",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontFamily = Manrope
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            RangeSlider(
                                value = sliderRange,
                                onValueChange = { range ->
                                    sliderRange = range
                                    viewModel.setCalorieRange(range.start.toInt(), range.endInclusive.toInt())
                                },
                                valueRange = 0f..1500f,
                                colors = SliderDefaults.colors(
                                    thumbColor = PrimaryGreen,
                                    activeTrackColor = PrimaryGreen,
                                    inactiveTrackColor = Color.Black.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 3. КАТЕГОРИИ (Теги)
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), hazeState = hazeState) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Категории", fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                            Spacer(modifier = Modifier.height(12.dp))

                            // FlowRow позволяет тегам переноситься на новую строку
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                allCategories.forEach { category ->
                                    val isSelected = selectedCategoryIds.contains(category.id)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.toggleCategory(category.id) },
                                        label = { Text(category.name, fontFamily = Manrope) },
                                        leadingIcon = { Text(category.icon) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PrimaryGreen.copy(alpha = 0.2f),
                                            selectedLabelColor = PrimaryGreen
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 4. РЕЗУЛЬТАТЫ
                item {
                    Text("Найдено: ${filteredRecipes.size}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (filteredRecipes.isEmpty()) {
                    item {
                        Text("Ничего не найдено. Попробуйте изменить фильтры.", color = TextSecondary, fontFamily = Manrope)
                    }
                } else {
                    // Сетка результатов (по 2 в ряд)
                    val chunked = filteredRecipes.chunked(2)
                    items(chunked.size) { index ->
                        val rowRecipes = chunked[index]
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            rowRecipes.forEach { recipe ->
                                GlassCard(
                                    modifier = Modifier.weight(1f).padding(vertical = 6.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    hazeState = hazeState
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth().height(100.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.5f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Outlined.Restaurant, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(32.dp))
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(recipe.title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope, maxLines = 2)
                                        Text("${recipe.calories} ккал", fontSize = 12.sp, color = PrimaryGreen, fontFamily = Manrope)
                                    }
                                }
                            }
                            if (rowRecipes.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}