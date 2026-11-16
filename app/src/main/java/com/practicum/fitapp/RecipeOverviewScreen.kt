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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.practicum.fitapp.data.local.CategoryEntity
import com.practicum.fitapp.data.local.CategoryType
import com.practicum.fitapp.data.local.CollectionWithRecipes
import com.practicum.fitapp.data.local.RecipeEntity
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.recipes.RecipeViewModel
import com.practicum.fitapp.ui.theme.*
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeOverviewScreen(
    onBack: () -> Unit,
    onNavigateToCategory: (String, Long) -> Unit ,
    onRecipeClick: (Long) -> Unit,
    viewModel: RecipeViewModel = hiltViewModel()
) {
    val collections by viewModel.collections.collectAsStateWithLifecycle()
    val allCategories by viewModel.allCategories.collectAsStateWithLifecycle()

    // Группируем категории по типам
    val mealCategories = allCategories.filter { it.type == CategoryType.MEAL }
    val dietCategories = allCategories.filter { it.type == CategoryType.DIET }
    val methodCategories = allCategories.filter { it.type == CategoryType.METHOD }

    val hazeState = remember { HazeState() }

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
                // 1. ПОПУЛЯРНЫЕ КАТЕГОРИИ (Квадратные карточки)
                if (mealCategories.isNotEmpty()) {
                    item {
                        Text(
                            text = "Популярные категории",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Берем первые 6 категорий для "популярных"
                            items(mealCategories.take(6)) { category ->
                                SquareCategoryCard(
                                    category = category,
                                    onClick = { onNavigateToCategory("MEAL", category.id) },
                                    hazeState = hazeState
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // 2. ПОДБОРКИ (Вдохновение)
                if (collections.isNotEmpty()) {
                    item {
                        Text(
                            text = "Вдохновение",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(collections) { collection ->
                                BigCollectionCard(collection, onClick = { /* TODO: Переход в подборку */ }, hazeState = hazeState)
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // 3. ДИЕТЫ (Квадратные карточки)
                if (dietCategories.isNotEmpty()) {
                    item {
                        Text(
                            text = "Выберите диету",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(dietCategories) { category ->
                                SquareCategoryCard(
                                    category = category,
                                    onClick = { onNavigateToCategory("DIET", category.id) },
                                    hazeState = hazeState
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // 4. МЕТОДЫ (Квадратные карточки)
                if (methodCategories.isNotEmpty()) {
                    item {
                        Text(
                            text = "Выберите метод",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            fontFamily = Manrope,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(methodCategories) { category ->
                                SquareCategoryCard(
                                    category = category,
                                    onClick = { onNavigateToCategory("METHOD", category.id) },
                                    hazeState = hazeState
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                // 5. КНОПКА "ВСЕ КАТЕГОРИИ" (Хотите больше?)
                item {
                    Text(
                        text = "Хотите больше?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontFamily = Manrope,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clickable { onNavigateToCategory("FILTERS", 0L) },
                        shape = RoundedCornerShape(16.dp),
                        hazeState = hazeState
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Все категории", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Искали вдохновение? Просмотрите все категории рецептов.", fontSize = 12.sp, color = TextSecondary)
                            }
                            // Иконки-заглушки
                            Text("🥗🍲", fontSize = 24.sp)
                        }
                    }
                }
            }
        }
    }
}

// --- КВАДРАТНАЯ КАРТОЧКА КАТЕГОРИИ (как на скрине) ---
@Composable
fun SquareCategoryCard(category: CategoryEntity, onClick: () -> Unit, hazeState: HazeState) {
    GlassCard(
        modifier = Modifier.size(width = 100.dp, height = 100.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        hazeState = hazeState
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = category.icon, fontSize = 32.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = TextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// --- БОЛЬШАЯ КАРТОЧКА ПОДБОРКИ (как на скрине "Вдохновение") ---
@Composable
fun BigCollectionCard(collection: CollectionWithRecipes, onClick: () -> Unit, hazeState: HazeState) {
    GlassCard(
        modifier = Modifier.width(260.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        hazeState = hazeState
    ) {
        Column {
            // Картинка подборки (берем картинку первого рецепта из подборки, если нет своей)
            val imageUrl = collection.collection.imageUrl ?: collection.recipes.firstOrNull()?.imageUrl
            AsyncImage(
                model = imageUrl,
                contentDescription = collection.collection.title,
                modifier = Modifier.fillMaxWidth().height(140.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = collection.collection.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontFamily = Manrope
                )
                Text(
                    text = collection.collection.subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontFamily = Manrope,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}