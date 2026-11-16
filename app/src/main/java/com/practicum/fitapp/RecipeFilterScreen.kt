package com.practicum.fitapp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.practicum.fitapp.data.local.RecipeEntity
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.recipes.RecipeViewModel
import com.practicum.fitapp.ui.theme.*
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun RecipeFilterScreen(
    onBack: () -> Unit,
    onRecipeClick: (Long) -> Unit,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategoryIds by viewModel.selectedCategoryIds.collectAsStateWithLifecycle()
    val minCalories by viewModel.minCalories.collectAsStateWithLifecycle()
    val maxCalories by viewModel.maxCalories.collectAsStateWithLifecycle()
    val filteredRecipes by viewModel.filteredRecipes.collectAsStateWithLifecycle()

    val hazeState = remember { HazeState() }
    var sliderRange by remember { mutableStateOf(minCalories.toFloat()..maxCalories.toFloat()) }

    // Стейт для спойлера категорий (по умолчанию свернут)
    var isCategoriesExpanded by remember { mutableStateOf(false) }
    // Анимация стрелочки
    val rotationAngle by animateFloatAsState(
        targetValue = if (isCategoriesExpanded) 180f else 0f,
        label = "arrow_rotation"
    )

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

                // 3. КАТЕГОРИИ (СПОЙЛЕР)
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp), hazeState = hazeState) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            // Заголовок-кнопка для спойлера
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { isCategoriesExpanded = !isCategoriesExpanded },
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (selectedCategoryIds.isNotEmpty()) "Категории (выбрано: ${selectedCategoryIds.size})" else "Категории",
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    fontFamily = Manrope
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Развернуть",
                                    tint = TextPrimary,
                                    modifier = Modifier.rotate(rotationAngle)
                                )
                            }

                            // Выпадающий контент
                            AnimatedVisibility(visible = isCategoriesExpanded) {
                                Column {
                                    Spacer(modifier = Modifier.height(12.dp))
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
                    items(filteredRecipes) { recipe ->
                        FullWidthRecipeCard(recipe, onClick = { onRecipeClick(recipe.id) }, hazeState = hazeState)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

// --- КАРТОЧКА НА ВСЮ ШИРИНУ ---
@Composable
fun FullWidthRecipeCard(recipe: RecipeEntity, onClick: () -> Unit, hazeState: HazeState) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        hazeState = hazeState
    ) {
        Column {
            // Картинка с кнопкой "Избранное"
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Кнопка избранного поверх картинки
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable { /* TODO: Сюда добавим логику избранного */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "Избранное",
                        tint = Color.White
                    )
                }
            }

            // Текстовая часть
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = recipe.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = Manrope,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${recipe.calories} ккал", fontSize = 14.sp, color = PrimaryGreen, fontFamily = Manrope)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("${recipe.cookTimeMinutes} мин.", fontSize = 14.sp, color = TextSecondary, fontFamily = Manrope)
                }
            }
        }
    }
}