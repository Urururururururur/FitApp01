package com.practicum.fitapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
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
import com.practicum.fitapp.data.local.CategoryEntity
import com.practicum.fitapp.data.local.CollectionWithRecipes
import com.practicum.fitapp.data.local.RecipeEntity
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.recipes.OverviewItem
import com.practicum.fitapp.presentation.recipes.RecipeViewModel
import com.practicum.fitapp.ui.theme.*
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeOverviewScreen(
    onBack: () -> Unit,
    onNavigateToCategory: (String) -> Unit, // Например, для перехода к фильтрам
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val collections by viewModel.collections.collectAsStateWithLifecycle()
    val mealCategories by viewModel.mealCategories.collectAsStateWithLifecycle()
    val dietCategories by viewModel.dietCategories.collectAsStateWithLifecycle()
    val popularRecipes by viewModel.filteredRecipes.collectAsStateWithLifecycle()

    val hazeState = remember { HazeState() }

    // Формируем список блоков для LazyColumn
    val overviewItems = remember(collections, mealCategories, dietCategories, popularRecipes) {
        val items = mutableListOf<OverviewItem>()

        // 1. Блок подборок (если есть)
        if (collections.isNotEmpty()) {
            collections.forEach { collection ->
                items.add(OverviewItem.CollectionBlock(collection))
            }
        }

        // 2. Блок приемов пищи
        if (mealCategories.isNotEmpty()) {
            items.add(OverviewItem.CategoryRow("Приемы пищи", mealCategories))
        }

        // 3. Блок диет
        if (dietCategories.isNotEmpty()) {
            items.add(OverviewItem.CategoryRow("Диеты", dietCategories))
        }

        // 4. Сетка популярных рецептов
        items.add(OverviewItem.PopularRecipesBlock(popularRecipes))

        items
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
                    title = { Text("Рецепты", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope) },
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
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                // Тот самый магический паттерн рендеринга
                items(overviewItems) { item ->
                    when (item) {
                        is OverviewItem.CollectionBlock -> {
                            CollectionCard(item.collection, hazeState)
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        is OverviewItem.CategoryRow -> {
                            CategoryRow(item.title, item.categories, onCategoryClick = { category ->
                                onNavigateToCategory(category.name)
                            })
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                        is OverviewItem.PopularRecipesBlock -> {
                            PopularRecipesGrid(item.recipes, hazeState)
                        }
                        is OverviewItem.CalorieFilterBlock -> {
                            // Пока не используем, оставили на будущее
                        }
                    }
                }
            }
        }
    }
}

// --- БЛОК ПОДБОРКИ ---
@Composable
private fun CollectionCard(collection: CollectionWithRecipes, hazeState: HazeState) {
    GlassCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        hazeState = hazeState
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = collection.collection.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
            Text(text = collection.collection.subtitle, fontSize = 12.sp, color = TextSecondary, fontFamily = Manrope)
            Spacer(modifier = Modifier.height(12.dp))

            // Горизонтальный список рецептов в подборке
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(collection.recipes) { recipe ->
                    RecipeMiniCard(recipe)
                }
            }
        }
    }
}

// --- БЛОК КАТЕГОРИЙ (ПИЛЮЛИ) ---
@Composable
private fun CategoryRow(title: String, categories: List<CategoryEntity>, onCategoryClick: (CategoryEntity) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { category ->
                Surface(
                    onClick = { onCategoryClick(category) },
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.8f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f))
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = category.icon, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = category.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary, fontFamily = Manrope)
                    }
                }
            }
        }
    }
}

// --- БЛОК ПОПУЛЯРНЫХ РЕЦЕПТОВ (СЕТКА) ---
@Composable
private fun PopularRecipesGrid(recipes: List<RecipeEntity>, hazeState: HazeState) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text("Все рецепты", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
        Spacer(modifier = Modifier.height(12.dp))

        // Имитация сетки внутри LazyColumn (используем Row и разбиваем по 2)
        recipes.chunked(2).forEach { rowRecipes ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowRecipes.forEach { recipe ->
                    GlassCard(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        hazeState = hazeState
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Картинка-заглушка
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
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// --- МИНИ-КАРТОЧКА ДЛЯ ПОДБОРКИ ---
@Composable
private fun RecipeMiniCard(recipe: RecipeEntity) {
    Card(
        modifier = Modifier.width(120.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Box(
                modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Restaurant, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(24.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(recipe.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary, maxLines = 1)
            Text("${recipe.calories} ккал", fontSize = 10.sp, color = TextSecondary)
        }
    }
}