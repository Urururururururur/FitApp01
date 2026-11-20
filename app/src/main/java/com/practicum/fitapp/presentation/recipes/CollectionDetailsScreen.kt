package com.practicum.fitapp.presentation.recipes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.practicum.fitapp.FullWidthRecipeCard
import com.practicum.fitapp.presentation.common.rememberDrawablePainter
import com.practicum.fitapp.ui.theme.*
import dev.chrisbanes.haze.rememberHazeState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailsScreen(
    onBack: () -> Unit,
    onRecipeClick: (Long) -> Unit,
    viewModel: CollectionDetailsViewModel = hiltViewModel()
) {
    val collectionState by viewModel.collectionState.collectAsStateWithLifecycle()
    val hazeState = rememberHazeState()

    val collection = collectionState

    if (collection == null) {
        Box(Modifier.fillMaxSize().background(Color.White), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryGreen)
        }
        return
    }

    // Берем случайную картинку из рецептов коллекции, если у самой коллекции нет обложки
    val randomRecipeImage = remember(collection) {
        collection.recipes.shuffled().firstOrNull { it.imageUrl != null }?.imageUrl
    }
    val imageUrl = collection.collection.imageUrl ?: randomRecipeImage

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            // Обычный топ-бар (компактный)
            TopAppBar(
                title = {
                    Text(
                        text = collection.collection.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Manrope,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Обложка и описание
            item {
                val painter = rememberDrawablePainter(imageUrl)
                if (painter != null) {
                    Image(
                        painter = painter,
                        contentDescription = collection.collection.title,
                        modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = collection.collection.title,
                        modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxWidth().height(200.dp).background(Color.LightGray))
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    if (collection.collection.subtitle.isNotBlank()) {
                        Text(
                            text = collection.collection.subtitle,
                            fontSize = 15.sp,
                            color = TextSecondary,
                            fontFamily = Manrope,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Text(
                        text = "Рецептов в подборке: ${collection.recipes.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Список рецептов
            items(collection.recipes) { recipe ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    FullWidthRecipeCard(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.id) },
                        hazeState = hazeState
                    )
                }
            }
        }
    }
}