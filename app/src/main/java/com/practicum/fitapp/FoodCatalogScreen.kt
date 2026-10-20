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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.practicum.fitapp.data.local.ProductEntity
import com.practicum.fitapp.presentation.nutrition.FoodCatalogViewModel

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

    var selectedTab by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

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
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Мои") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Часто") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Справочник") }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("Рецепты") }
                )
            }

            // Содержимое вкладок
            when (selectedTab) {
                0 -> UserProductsTab(
                    products = userProducts,
                    onDelete = { viewModel.deleteProduct(it) },
                    onAdd = { showAddDialog = true },
                    onProductClick = onProductSelected
                )
                1 -> ProductsList(
                    products = usedProducts,
                    emptyText = "Вы пока не ели ни одного продукта"
                ) { onProductSelected(it) }
                2 -> ProductsList(
                    products = builtInProducts,
                    emptyText = "Справочник пуст"
                ) { onProductSelected(it) }
                3 -> RecipesPlaceholder()
            }
        }
    }

    // Диалог добавления продукта
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
                    newName = ""
                    newCategory = ""
                    newCalories = ""
                    newProtein = ""
                    newFat = ""
                    newCarbs = ""
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
}

@Composable
private fun UserProductsTab(
    products: List<ProductEntity>,
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
        if (products.isEmpty()) {
            Text(
                "У вас пока нет созданных продуктов",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray
            )
        } else {
            LazyColumn {
                items(products) { product ->
                    ProductRow(
                        product = product,
                        onClick = { onProductClick(product) },
                        onDelete = { onDelete(product) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductsList(
    products: List<ProductEntity>,
    emptyText: String,
    onProductClick: (ProductEntity) -> Unit
) {
    if (products.isEmpty()) {
        Text(
            emptyText,
            modifier = Modifier.padding(16.dp),
            color = Color.Gray
        )
    } else {
        LazyColumn {
            items(products) { product ->
                ProductRow(
                    product = product,
                    onClick = { onProductClick(product) },
                    onDelete = null
                )
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
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(
                    text = "${product.caloriesPer100g} ккал/100г · " +
                            "Б${"%.0f".format(product.proteinPer100g)} " +
                            "Ж${"%.0f".format(product.fatPer100g)} " +
                            "У${"%.0f".format(product.carbsPer100g)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            if (onDelete != null) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = Color.Red)
                }
            }
        }
    }
}

@Composable
private fun RecipesPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            "Рецепты появятся позже",
            color = Color.Gray,
            fontSize = 16.sp
        )
    }
}