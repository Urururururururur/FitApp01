package com.practicum.fitapp

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ProductCategory
import com.practicum.fitapp.data.local.ProductEntity
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

    var selectedTab by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedRecipe by remember { mutableStateOf<Recipe?>(null) }
    var recipeGrams by remember { mutableStateOf("") }

    // Стейты для формы добавления
    var newName by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("") }
    var newCalories by remember { mutableStateOf("") }
    var newProtein by remember { mutableStateOf("") }
    var newFat by remember { mutableStateOf("") }
    var newCarbs by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Справочник продуктов") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Вкладки
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Мои") })
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Часто") })
                Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }, text = { Text("Справочник") })
                Tab(selected = selectedTab == 3, onClick = { selectedTab = 3 }, text = { Text("Рецепты") })
            }

            // Поиск
            if (selectedTab != 3) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Поиск продукта...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    singleLine = true
                )
            }

            // Содержимое вкладок
            when (selectedTab) {
                0 -> UserProductsTab(
                    products = userProducts,
                    searchQuery = searchQuery,
                    onDelete = { viewModel.deleteProduct(it) },
                    onAdd = { showAddDialog = true },
                    onProductClick = onProductSelected
                )
                1 -> ProductsList(
                    products = usedProducts,
                    searchQuery = searchQuery,
                    emptyText = "Вы пока не ели ни одного продукта"
                ) { onProductSelected(it) }
                2 -> ProductsList(
                    products = builtInProducts,
                    searchQuery = searchQuery,
                    emptyText = "Справочник пуст"
                ) { onProductSelected(it) }
                3 -> RecipesList(
                    recipes = com.practicum.fitapp.presentation.nutrition.builtInRecipes,
                    onRecipeClick = { recipe ->
                        selectedRecipe = recipe
                        recipeGrams = recipe.totalWeightGrams.toString()
                    }
                )
            }
        }
    }

    // ============ ДИАЛОГ ДОБАВЛЕНИЯ ПРОДУКТА ============
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Новый продукт") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Название") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCategory,
                        onValueChange = { newCategory = it },
                        label = { Text("Категория") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newCalories,
                        onValueChange = { v -> if (v.all { it.isDigit() }) newCalories = v },
                        label = { Text("Ккал / 100 г") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newProtein,
                            onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) newProtein = v },
                            label = { Text("Белки") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newFat,
                            onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) newFat = v },
                            label = { Text("Жиры") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = newCarbs,
                            onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) newCarbs = v },
                            label = { Text("Углеводы") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.addProduct(
                        name = newName,
                        category = newCategory,
                        calories = newCalories.toIntOrNull() ?: 0,
                        protein = newProtein.toDoubleOrNull() ?: 0.0,
                        fat = newFat.toDoubleOrNull() ?: 0.0,
                        carbs = newCarbs.toDoubleOrNull() ?: 0.0
                    )
                    newName = ""; newCategory = ""; newCalories = ""; newProtein = ""; newFat = ""; newCarbs = ""
                    showAddDialog = false
                }) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    // ============ ДИАЛОГ ДЕТАЛЕЙ РЕЦЕПТА ============
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
                        items(recipe.ingredients) { ing ->
                            Text("${ing.productName} — ${ing.grams} г", fontSize = 13.sp)
                        }
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Приготовление:", fontWeight = FontWeight.Bold)
                        }
                        items(recipe.steps) { step ->
                            Text(step, fontSize = 13.sp, modifier = Modifier.padding(vertical = 2.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = recipeGrams,
                        onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) recipeGrams = v },
                        label = { Text("Граммы порции") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val grams = recipeGrams.toDoubleOrNull() ?: recipe.totalWeightGrams
                    val todayTimestamp = java.time.LocalDate.now()
                        .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                    viewModel.addRecipeToDiary(recipe, todayTimestamp, grams)
                    selectedRecipe = null
                    onBack() // Возвращаемся в дневник питания
                }) {
                    Text("Добавить в дневник")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRecipe = null }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

// ==================== ВСПОМОГАТЕЛЬНЫЕ КОМПОНЕНТЫ ====================

@Composable
private fun UserProductsTab(
    products: List<ProductEntity>,
    searchQuery: String,
    onDelete: (ProductEntity) -> Unit,
    onAdd: () -> Unit,
    onProductClick: (ProductEntity) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = onAdd,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Добавить продукт")
        }

        val filtered = products.filter { it.name.contains(searchQuery, ignoreCase = true) }
        if (filtered.isEmpty()) {
            Text(
                "У вас пока нет созданных продуктов или ничего не найдено",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            GroupedProductsList(
                products = filtered,
                onProductClick = onProductClick,
                onDelete = onDelete
            )
        }
    }
}

@Composable
private fun ProductsList(
    products: List<ProductEntity>,
    searchQuery: String,
    emptyText: String,
    onProductClick: (ProductEntity) -> Unit
) {
    val filtered = products.filter { it.name.contains(searchQuery, ignoreCase = true) }
    if (filtered.isEmpty()) {
        Text(
            emptyText,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    } else {
        GroupedProductsList(
            products = filtered,
            onProductClick = onProductClick,
            onDelete = null
        )
    }
}

@Composable
private fun GroupedProductsList(
    products: List<ProductEntity>,
    onProductClick: (ProductEntity) -> Unit,
    onDelete: ((ProductEntity) -> Unit)?
) {
    var expandedCategories by remember { mutableStateOf(setOf<String>()) }

    val grouped = products.groupBy {
        ProductCategory.fromId(it.category)
    }.toList().sortedBy { it.first.displayName }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        grouped.forEach { (category, categoryProducts) ->
            val isExpanded = expandedCategories.contains(category.displayName)

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable {
                            expandedCategories = if (isExpanded) {
                                expandedCategories - category.displayName
                            } else {
                                expandedCategories + category.displayName
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${category.displayName} (${categoryProducts.size})",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "Свернуть" else "Развернуть"
                        )
                    }
                }
            }

            if (isExpanded) {
                items(categoryProducts) { product ->
                    ProductRow(
                        product = product,
                        onClick = { onProductClick(product) },
                        onDelete = if (onDelete != null) { { onDelete(product) } } else null
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductRow(
    product: ProductEntity,
    onClick: () -> Unit,
    onDelete: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))

                val categoryDisplayName = ProductCategory.fromId(product.category).displayName
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = categoryDisplayName,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${product.caloriesPer100g} ккал/100г · " +
                            "Б${"%.0f".format(product.proteinPer100g)} " +
                            "Ж${"%.0f".format(product.fatPer100g)} " +
                            "У${"%.0f".format(product.carbsPer100g)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun RecipesList(
    recipes: List<Recipe>,
    onRecipeClick: (Recipe) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(recipes) { recipe ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clickable { onRecipeClick(recipe) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(recipe.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(recipe.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "${"%.0f".format(recipe.totalsKcal)} ккал · " +
                                "Б${"%.0f".format(recipe.totalsProtein)} " +
                                "Ж${"%.0f".format(recipe.totalsFat)} " +
                                "У${"%.0f".format(recipe.totalsCarbs)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}