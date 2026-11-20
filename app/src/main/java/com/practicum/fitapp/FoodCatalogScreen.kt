package com.practicum.fitapp

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.practicum.fitapp.presentation.nutrition.BarcodeScannerScreen
import com.practicum.fitapp.presentation.nutrition.FoodCatalogViewModel
import com.practicum.fitapp.ui.theme.*
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodCatalogScreen(
    onBack: () -> Unit,
    onProductSelected: (ProductEntity) -> Unit,
    viewModel: FoodCatalogViewModel = hiltViewModel()
) {
    val userProducts by viewModel.userProducts.collectAsStateWithLifecycle()
    val builtInProducts by viewModel.builtInProducts.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

    // 0 - Справочник (по умолчанию), 1 - Мои, 2 - Штрихкод (заглушка)
    var selectedTab by remember { mutableStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) } // У тебя уже есть
    var expandedCategories by remember { mutableStateOf(setOf<String>()) }

    // Стейты для формы добавления
    var newName by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("") }
    var newCalories by remember { mutableStateOf("") }
    var newProtein by remember { mutableStateOf("") }
    var newFat by remember { mutableStateOf("") }
    var newCarbs by remember { mutableStateOf("") }
    var useAi by remember { mutableStateOf(false) }
    var showScanner by remember { mutableStateOf(false) }

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
                    title = { Text("Справочник продуктов", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Назад", tint = TextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                // 1. Вкладки
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(30.dp),
                    hazeState = hazeState,
                    hasBorder = true
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Новый порядок вкладок
                        val tabs = listOf("Справочник", "Мои", "Штрихкод")

                        tabs.forEachIndexed { index, title ->
                            val isSelected = selectedTab == index

                            // Для штрихкода делаем иконку
                            val icon = if (index == 2) Icons.Outlined.QrCodeScanner else null

                            val bgColor by animateColorAsState(
                                targetValue = if (isSelected) PrimaryGreen else Color.Transparent,
                                label = "bgColor"
                            )
                            val textColor by animateColorAsState(
                                targetValue = if (isSelected) Color.White else TextSecondary,
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
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (icon != null) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            tint = textColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    Text(
                                        text = title,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = textColor,
                                        fontFamily = Manrope,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Поиск (Скрываем на вкладке штрихкода)
                if (selectedTab != 2) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 0.dp),
                        shape = RoundedCornerShape(30.dp),
                        hazeState = hazeState,
                        hasBorder = true
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Поиск продукта...", color = TextSecondary, fontFamily = Manrope) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(fontFamily = Manrope, color = TextPrimary),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Список
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp)
                ) {
                    when (selectedTab) {
                        0 -> { // СПРАВОЧНИК (По умолчанию)
                            val filtered = builtInProducts.filter { it.name.contains(searchQuery, ignoreCase = true) }
                            if (filtered.isEmpty()) {
                                item { Text("Справочник пуст", color = TextSecondary, fontFamily = Manrope) }
                            } else {
                                groupedProductsList(
                                    products = filtered,
                                    expandedCategories = expandedCategories,
                                    onToggleCategory = { cat ->
                                        expandedCategories = if (expandedCategories.contains(cat)) expandedCategories - cat else expandedCategories + cat
                                    },
                                    onProductClick = onProductSelected,
                                    onDelete = null,
                                    hazeState = hazeState
                                )
                            }
                        }

                        1 -> { // МОИ ПРОДУКТЫ
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .height(56.dp)
                                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = PrimaryGreen.copy(alpha = 0.3f))
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(PrimaryGreen)
                                        .clickable { showAddDialog = true },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Добавить продукт", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            val filtered = userProducts.filter { it.name.contains(searchQuery, ignoreCase = true) }
                            if (filtered.isEmpty()) {
                                item { Text("У вас пока нет созданных продуктов", color = TextSecondary, fontFamily = Manrope) }
                            } else {
                                groupedProductsList(
                                    products = filtered,
                                    expandedCategories = expandedCategories,
                                    onToggleCategory = { cat ->
                                        expandedCategories = if (expandedCategories.contains(cat)) expandedCategories - cat else expandedCategories + cat
                                    },
                                    onProductClick = onProductSelected,
                                    onDelete = { viewModel.deleteProduct(it) },
                                    hazeState = hazeState
                                )
                            }
                        }

                        2 -> {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Outlined.QrCodeScanner, contentDescription = null, modifier = Modifier.size(80.dp), tint = PrimaryGreen)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text("Сканер штрихкодов", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { showScanner = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                                    ) {
                                        Text("Открыть камеру")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    // ЭКРАН СКАНЕРА
    if (showScanner) {
        BarcodeScannerScreen(
            onBack = { showScanner = false },
            onProductFound = { scannedProduct ->
                showScanner = false
                selectedTab = 1 // Переключаем на вкладку "Мои"

                // Заполняем форму
                newName = scannedProduct.name
                newCalories = scannedProduct.calories.toString()
                newProtein = scannedProduct.protein.toString()
                newFat = scannedProduct.fat.toString()
                newCarbs = scannedProduct.carbs.toString()
                newCategory = "Сканер" // Или оставь пустым

                showAddDialog = true // Открываем диалог
            }
        )
    }
    // ============ ДИАЛОГ ДОБАВЛЕНИЯ ПРОДУКТА ============
    if (showAddDialog) {
        Dialog(onDismissRequest = { if (!isAiLoading) showAddDialog = false }) {
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                shape = RoundedCornerShape(24.dp),
                hazeState = hazeState,
                hasShadow = true,
                hasBorder = true
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Новый продукт", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                    Spacer(modifier = Modifier.height(16.dp))

                    val textFieldColors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )

                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Название", color = TextSecondary, fontFamily = Manrope) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isAiLoading,
                        colors = textFieldColors
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newCategory,
                        onValueChange = { newCategory = it },
                        label = { Text("Категория", color = TextSecondary, fontFamily = Manrope) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        enabled = !useAi && !isAiLoading,
                        colors = textFieldColors
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = newCalories,
                        onValueChange = { v -> if (v.all { it.isDigit() }) newCalories = v },
                        label = { Text("Ккал / 100 г", color = TextSecondary, fontFamily = Manrope) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !useAi && !isAiLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        colors = textFieldColors
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newProtein,
                            onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) newProtein = v },
                            label = { Text("Белки", fontSize = 11.sp, color = TextSecondary, fontFamily = Manrope) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            enabled = !useAi && !isAiLoading,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )
                        OutlinedTextField(
                            value = newFat,
                            onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) newFat = v },
                            label = { Text("Жиры", fontSize = 11.sp, color = TextSecondary, fontFamily = Manrope) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            enabled = !useAi && !isAiLoading,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )
                        OutlinedTextField(
                            value = newCarbs,
                            onValueChange = { v -> if (v.all { it.isDigit() || it == '.' }) newCarbs = v },
                            label = { Text("Углев.", fontSize = 11.sp, color = TextSecondary, fontFamily = Manrope) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            enabled = !useAi && !isAiLoading,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(12.dp),
                            colors = textFieldColors
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("✨ ИИ БЖУ", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryGreen, fontFamily = Manrope)
                        Switch(
                            checked = useAi,
                            onCheckedChange = {
                                useAi = it
                                if (it) {
                                    newCategory = ""; newCalories = ""; newProtein = ""; newFat = ""; newCarbs = ""
                                }
                            },
                            enabled = !isAiLoading,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryGreen
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { showAddDialog = false }, enabled = !isAiLoading) {
                            Text("Отмена", color = TextSecondary, fontFamily = Manrope)
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
                                newName = ""; newCategory = ""; newCalories = ""; newProtein = ""; newFat = ""; newCarbs = ""
                                useAi = false
                                showAddDialog = false
                            },
                            enabled = newName.isNotBlank() && !isAiLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryGreen,
                                disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isAiLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Добавить", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = Manrope)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Вспомогательная функция (осталась без изменений)
private fun LazyListScope.groupedProductsList(
    products: List<ProductEntity>,
    expandedCategories: Set<String>,
    onToggleCategory: (String) -> Unit,
    onProductClick: (ProductEntity) -> Unit,
    onDelete: ((ProductEntity) -> Unit)?,
    hazeState: HazeState
) {
    val grouped = products.groupBy { product ->
        val matchedEnum = ProductCategory.entries.find {
            it.id.equals(product.category, ignoreCase = true) ||
                    it.displayName.equals(product.category, ignoreCase = true)
        }
        matchedEnum?.displayName ?: product.category.ifBlank { "Прочее" }
    }.toList().sortedBy { it.first }

    grouped.forEach { (categoryName, categoryProducts) ->
        val isExpanded = expandedCategories.contains(categoryName)

        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onToggleCategory(categoryName) },
                shape = RoundedCornerShape(16.dp),
                hazeState = hazeState,
                hasShadow = true,
                hasBorder = true
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$categoryName (${categoryProducts.size})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                    Icon(if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextSecondary)
                }
            }
        }

        if (isExpanded) {
            items(categoryProducts) { product ->
                GlassCard(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onProductClick(product) },
                    shape = RoundedCornerShape(16.dp),
                    hazeState = hazeState,
                    hasShadow = true,
                    hasBorder = true
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, fontFamily = Manrope)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${product.caloriesPer100g} ккал/100г · Б${"%.0f".format(product.proteinPer100g)} Ж${"%.0f".format(product.fatPer100g)} У${"%.0f".format(product.carbsPer100g)}",
                                fontSize = 12.sp, color = TextSecondary, fontFamily = Manrope
                            )
                        }
                        if (onDelete != null) {
                            IconButton(onClick = { onDelete(product) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = TextSecondary)
                            }
                        }
                    }
                }
            }
        }
    }
}