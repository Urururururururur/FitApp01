package com.practicum.fitapp

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ProductCategory
import com.practicum.fitapp.data.local.ProductEntity
import com.practicum.fitapp.presentation.common.GlassCard
import com.practicum.fitapp.presentation.nutrition.FoodCatalogViewModel
import com.practicum.fitapp.presentation.nutrition.Recipe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCatalogScreen(
    onBack: () -> Unit,
    onProductSelected: (ProductEntity) -> Unit,
    viewModel: FoodCatalogViewModel = hiltViewModel()
) {
    val userProducts by viewModel.userProducts.collectAsStateWithLifecycle()
    val usedProducts by viewModel.usedProducts.collectAsStateWithLifecycle()
    val builtInProducts by viewModel.builtInProducts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle() // <-- Стейт загрузки ИИ

    var selectedTab by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    var recipeGrams by remember { mutableStateOf("") }

    var expandedCategories by remember { mutableStateOf(setOf<String>()) }

    // Стейты для формы добавления
    var newName by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("") }
    var newCalories by remember { mutableStateOf("") }
    var newProtein by remember { mutableStateOf("") }
    var newFat by remember { mutableStateOf("") }
    var newCarbs by remember { mutableStateOf("") }

    // Стейт для тумблера ИИ
    var useAi by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Справочник продуктов") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 1. Список
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 130.dp, bottom = 100.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        item {
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { showAddDialog = true },
                                shape = RoundedCornerShape(16.dp),
                                solidColor = Color(0xFF81C784),
                                hasShadow = true,
                                hasBorder = false
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Добавить продукт", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        val filtered = userProducts.filter { it.name.contains(searchQuery, ignoreCase = true) }
                        if (filtered.isEmpty()) {
                            item { Text("У вас пока нет созданных продуктов", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        } else {
                            groupedProductsList(
                                products = filtered,
                                expandedCategories = expandedCategories,
                                onToggleCategory = { cat ->
                                    expandedCategories = if (expandedCategories.contains(cat)) expandedCategories - cat else expandedCategories + cat
                                },
                                onProductClick = onProductSelected,
                                onDelete = { viewModel.deleteProduct(it) }
                            )
                        }
                    }
                    1 -> {
                        val filtered = usedProducts.filter { it.name.contains(searchQuery, ignoreCase = true) }
                        if (filtered.isEmpty()) {
                            item { Text("Вы пока не ели ни одного продукта", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        } else {
                            groupedProductsList(
                                products = filtered,
                                expandedCategories = expandedCategories,
                                onToggleCategory = { cat ->
                                    expandedCategories = if (expandedCategories.contains(cat)) expandedCategories - cat else expandedCategories + cat
                                },
                                onProductClick = onProductSelected,
                                onDelete = null
                            )
                        }
                    }
                    2 -> {
                        val filtered = builtInProducts.filter { it.name.contains(searchQuery, ignoreCase = true) }
                        if (filtered.isEmpty()) {
                            item { Text("Справочник пуст", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        } else {
                            groupedProductsList(
                                products = filtered,
                                expandedCategories = expandedCategories,
                                onToggleCategory = { cat ->
                                    expandedCategories = if (expandedCategories.contains(cat)) expandedCategories - cat else expandedCategories + cat
                                },
                                onProductClick = onProductSelected,
                                onDelete = null
                            )
                        }
                    }
                    3 -> {
                        val filteredRecipes = com.practicum.fitapp.presentation.nutrition.builtInRecipes.filter {
                            it.title.contains(searchQuery, ignoreCase = true)
                        }

                        if (filteredRecipes.isEmpty()) {
                            item { Text("Рецепты не найдены", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        } else {
                            items(filteredRecipes) { recipe ->
                                GlassCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            selectedRecipe = recipe
                                            recipeGrams = recipe.totalWeightGrams.toString()
                                        },
                                    shape = RoundedCornerShape(12.dp),
                                    hasShadow = false,
                                    hasBorder = false
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(recipe.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(recipe.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "${"%.0f".format(recipe.totalsKcal)} ккал · Б${"%.0f".format(recipe.totalsProtein)} Ж${"%.0f".format(recipe.totalsFat)} У${"%.0f".format(recipe.totalsCarbs)}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. Парящая панель
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Вкладки
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    glassColor = Color(0xFFC8E6C9).copy(alpha = 0.75f),
                    hasShadow = true,
                    hasBorder = false
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val tabs = listOf("Мои", "Часто", "Справочник", "Рецепты")

                        tabs.forEachIndexed { index, title ->
                            val isSelected = selectedTab == index

                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else Color.Transparent,
                                label = "bgColor"
                            )
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                label = "textColor"
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(bgColor)
                                    .clickable { selectedTab = index }
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = textColor,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Поиск
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(30.dp),
                    glassColor = Color.White.copy(alpha = 0.7f),
                    hasShadow = true,
                    hasBorder = false
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        placeholder = { Text(if (selectedTab == 3) "Поиск рецепта..." else "Поиск продукта...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
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
            }
        }
    }

    // ============ ДИАЛОГ ДОБАВЛЕНИЯ ПРОДУКТА (С ИИ) ============
    if (showAddDialog) {
        Dialog(onDismissRequest = { if (!isAiLoading) showAddDialog = false }) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(24.dp),
                solidColor = Color(0xFFF1F8E9),
                hasShadow = true,
                hasBorder = false
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Новый продукт",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Название") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isAiLoading
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newCategory,
                        onValueChange = { newCategory = it },
                        label = { Text("Категория") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        enabled = !useAi && !isAiLoading // Блокируем, если включен ИИ
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newCalories,
                        onValueChange = { v -> if (v.all { it.isDigit() }) newCalories = v },
                        label = { Text("Ккал / 100 г") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !useAi && !isAiLoading, // Блокируем, если включен ИИ
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newProtein,
                            onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) newProtein = v },
                            label = { Text("Белки", fontSize = 11.sp, maxLines = 1) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            enabled = !useAi && !isAiLoading, // Блокируем
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = newFat,
                            onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) newFat = v },
                            label = { Text("Жиры", fontSize = 11.sp, maxLines = 1) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            enabled = !useAi && !isAiLoading, // Блокируем
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = newCarbs,
                            onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) newCarbs = v },
                            label = { Text("Углев.", fontSize = 11.sp, maxLines = 1) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            enabled = !useAi && !isAiLoading, // Блокируем
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ============ ТУМБЛЕР ИИ ============
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("✨ ИИ БЖУ", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF6A1B9A))
                        }
                        Switch(
                            checked = useAi,
                            onCheckedChange = {
                                useAi = it
                                // При включении ИИ очищаем ручные поля, чтобы не путать пользователя
                                if (it) {
                                    newCategory = ""; newCalories = ""; newProtein = ""; newFat = ""; newCarbs = ""
                                }
                            },
                            enabled = !isAiLoading,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF6A1B9A))
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showAddDialog = false },
                            enabled = !isAiLoading
                        ) {
                            Text("Отмена", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (useAi) {
                                    viewModel.addProductWithAi(newName)
                                } else {
                                    viewModel.addProduct(
                                        name = newName, category = newCategory,
                                        calories = newCalories.toIntOrNull() ?: 0,
                                        protein = newProtein.toDoubleOrNull() ?: 0.0,
                                        fat = newFat.toDoubleOrNull() ?: 0.0,
                                        carbs = newCarbs.toDoubleOrNull() ?: 0.0
                                    )
                                }
                                // Очистка и закрытие
                                newName = ""; newCategory = ""; newCalories = ""; newProtein = ""; newFat = ""; newCarbs = ""
                                useAi = false
                                showAddDialog = false
                            },
                            enabled = newName.isNotBlank() && !isAiLoading, // Блокируем, если нет названия или идет загрузка
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (useAi) Color(0xFF6A1B9A) else Color(0xFF81C784),
                                disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isAiLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Добавить", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // ============ ДИАЛОГ РЕЦЕПТА ============
    if (selectedRecipe != null) {
        val recipe = selectedRecipe!!
        AlertDialog(
            onDismissRequest = { selectedRecipe = null },
            title = { Text(recipe.title) },
            text = {
                Column {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        item {
                            Text(recipe.description, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Время: ${recipe.cookingTimeMinutes} мин", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Ингредиенты:", fontWeight = FontWeight.Bold)
                        }
                        items(recipe.ingredients) { ing -> Text("${ing.productName} — ${ing.grams} г", fontSize = 13.sp) }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Приготовление:", fontWeight = FontWeight.Bold)
                        }
                        items(recipe.steps) { step -> Text(step, fontSize = 13.sp, modifier = Modifier.padding(vertical = 2.dp)) }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = recipeGrams,
                        onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) recipeGrams = v },
                        label = { Text("Граммы порции") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val grams = recipeGrams.toDoubleOrNull() ?: recipe.totalWeightGrams
                        val todayTimestamp = java.time.LocalDate.now().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                        viewModel.addRecipeToDiary(recipe, todayTimestamp, grams)
                        selectedRecipe = null
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784))
                ) { Text("Добавить", color = Color.White) }
            },
            dismissButton = { TextButton(onClick = { selectedRecipe = null }) { Text("Закрыть") } }
        )
    }
}

// Вспомогательная функция
private fun LazyListScope.groupedProductsList(
    products: List<ProductEntity>,
    expandedCategories: Set<String>,
    onToggleCategory: (String) -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onDelete: ((ProductEntity) -> Unit)?
) {
    // ИСПРАВЛЕНИЕ: Группируем по строковому названию категории
    val grouped = products.groupBy { product ->
        // Пытаемся найти красивый DisplayName, если пользователь ввел ID (например, "FRUITS")
        val matchedEnum = ProductCategory.entries.find {
            it.id.equals(product.category, ignoreCase = true) ||
                    it.displayName.equals(product.category, ignoreCase = true)
        }
        // Если нашли — используем красивое имя. Если нет — оставляем то, что ввел пользователь.
        matchedEnum?.displayName ?: product.category.ifBlank { "Прочее" }
    }.toList().sortedBy { it.first }

    grouped.forEach { (categoryName, categoryProducts) ->
        val isExpanded = expandedCategories.contains(categoryName)

        item {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onToggleCategory(categoryName) },
                shape = RoundedCornerShape(12.dp),
                glassColor = Color(0xFFC8E6C9).copy(alpha = 0.5f),
                hasShadow = false,
                hasBorder = false
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$categoryName (${categoryProducts.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Icon(if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }

        if (isExpanded) {
            items(categoryProducts) { product ->
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onProductClick(product) },
                    shape = RoundedCornerShape(12.dp),
                    hasShadow = false,
                    hasBorder = false
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${product.caloriesPer100g} ккал/100г · Б${"%.0f".format(product.proteinPer100g)} Ж${"%.0f".format(product.fatPer100g)} У${"%.0f".format(product.carbsPer100g)}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (onDelete != null) {
                            IconButton(onClick = { onDelete(product) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}