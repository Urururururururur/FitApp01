package com.practicum.fitapp.presentation.recipes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.practicum.fitapp.FullWidthRecipeCard
import com.practicum.fitapp.data.local.CategoryEntity
import com.practicum.fitapp.data.local.RecipeEntity
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.ui.theme.*
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeCategoriesScreen(
    onBack: () -> Unit,
    onRecipeClick: (Long) -> Unit,
    onSeeAllClick: (String, Long) -> Unit,
    // Добавляем коллбэк для перехода на экран пересечений (например, Завтрак + Веганский)
    onCrossCategoryClick: (Long, Long) -> Unit,
    onCollectionCrossClick: (Long, Long) -> Unit,
    viewModel: RecipeCategoriesViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val recipes by viewModel.recipes.collectAsStateWithLifecycle()
    val crossCategories by viewModel.crossCategories.collectAsStateWithLifecycle()
    val crossCollections by viewModel.crossCollections.collectAsStateWithLifecycle()

    val hazeState = remember { HazeState() }
    val title = when (viewModel.categoryType.name) {
        "MEAL" -> "Приемы пищи"
        "DIET" -> "Диеты"
        "METHOD" -> "Методы"
        else -> "Категории"
    }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {
        Image(
            painter = androidx.compose.ui.res.painterResource(id = com.practicum.fitapp.R.drawable.app_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize().hazeSource(hazeState),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text(title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope) },
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
                // 1. КАРУСЕЛЬ С ПИЛЮЛЯМИ
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            val isSelected = category.id == selectedCategoryId
                            Surface(
                                onClick = { viewModel.selectCategory(category.id) },
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) Color(0xFF5B7A81) else Color.White,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = category.icon, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = category.name,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // 2. РЕЦЕПТ ДНЯ
                if (recipes.isNotEmpty()) {
                    val recipeOfTheDay = recipes.first()
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text("⭐", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Рецепт дня", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            FullWidthRecipeCard(recipe = recipeOfTheDay, onClick = { onRecipeClick(recipeOfTheDay.id) }, hazeState = hazeState)
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // 3. КАРУСЕЛЬ "ВСЕ РЕЦЕПТЫ" (основная)
                if (recipes.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Все рецепты", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(
                                text = "БОЛЬШЕ",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2196F3),
                                modifier = Modifier.clickable { onSeeAllClick(viewModel.categoryType.name, selectedCategoryId) }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(recipes.drop(1).take(8)) { recipe ->
                                RecipeCarouselCard(recipe, onClick = { onRecipeClick(recipe.id) }, hazeState = hazeState)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
                // 5. ПОДБОРКИ (КУХНИ МИРА, СЕЗОНЫ)
                items(crossCollections) { cross ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cross.collection.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        // Кнопка "БОЛЬШЕ" для подборок. Нам понадобится экран для них,
                        // но пока можешь сделать просто переход в общий фильтр.
                        Text(
                            text = "БОЛЬШЕ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3),
                            modifier = Modifier.clickable {
                                // categoryId, collectionId
                                onCollectionCrossClick(selectedCategoryId, cross.collection.id)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cross.recipes.take(8)) { recipe ->
                            RecipeCarouselCard(recipe, onClick = { onRecipeClick(recipe.id) }, hazeState = hazeState)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 4. ДОПОЛНИТЕЛЬНЫЕ КАРУСЕЛИ (ПЕРЕСЕЧЕНИЯ)
                items(crossCategories) { cross ->
                    // Убрали лишнее слово item
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cross.category.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "БОЛЬШЕ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2196F3),
                            modifier = Modifier.clickable {
                                onCrossCategoryClick(selectedCategoryId, cross.category.id)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cross.recipes.take(8)) { recipe ->
                            RecipeCarouselCard(recipe, onClick = { onRecipeClick(recipe.id) }, hazeState = hazeState)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// --- КАРТОЧКА ДЛЯ КАРУСЕЛИ ---
@Composable
fun RecipeCarouselCard(recipe: RecipeEntity, onClick: () -> Unit, hazeState: HazeState) {
    GlassCard(
        modifier = Modifier.width(200.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        hazeState = hazeState
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(130.dp)) {
                // Возвращаем AsyncImage, так как он лучше работает с URI из сидера
                AsyncImage(
                    model = recipe.imageUrl,
                    contentDescription = recipe.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (recipe.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = recipe.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text("${recipe.calories} ккал", fontSize = 12.sp, color = PrimaryGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${recipe.cookTimeMinutes} мин", fontSize = 12.sp, color = TextSecondary)
                }
            }
        }
    }
}