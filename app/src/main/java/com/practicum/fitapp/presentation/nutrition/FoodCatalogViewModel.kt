package com.practicum.fitapp.presentation.nutrition

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.practicum.fitapp.data.local.ProductEntity
import com.practicum.fitapp.data.repository.AiCalorieRepository
import com.practicum.fitapp.data.repository.NutritionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import javax.inject.Inject


@HiltViewModel
class FoodCatalogViewModel @Inject constructor(
    private val repository: NutritionRepository,
    private val aiCalorieRepository: AiCalorieRepository,
    @ApplicationContext private val context: Context // <-- ДОБАВИЛИ КОНТЕКСТ
) : ViewModel() {

    // Стейт загрузки ИИ
    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    /** Созданные пользователем продукты. */
    val userProducts: StateFlow<List<ProductEntity>> = repository
        .getUserCreatedProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Использованные (были в записях о еде). */
    val usedProducts: StateFlow<List<ProductEntity>> = repository
        .getUsedProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /** Встроенные (справочник). */
    val builtInProducts: StateFlow<List<ProductEntity>> = repository
        .getBuiltInProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Запускаем загрузку в фоновом потоке (чтобы не тормозить UI)
        viewModelScope.launch(Dispatchers.IO) {
            if (repository.countBuiltInProducts() == 0) {
                loadProductsFromJson()
            }
        }
    }

    private suspend fun loadProductsFromJson() {
        try {
            val jsonString = context.assets.open("built_in_products.json").bufferedReader().use { it.readText() }
            val type = object : TypeToken<List<ProductEntity>>() {}.type

            // Создаем Gson, который умеет превращать 137.6 в 137 для поля caloriesPer100g
            val gson = GsonBuilder().registerTypeAdapter(Int::class.java, object : JsonDeserializer<Int> {
                override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Int {
                    return json?.asDouble?.toInt() ?: 0 // Берем дробь и отбрасываем хвост
                }
            }).create()

            val products: List<ProductEntity> = gson.fromJson(jsonString, type)

            val productsToInsert = products.map {
                it.copy(id = 0, isUserCreated = false)
            }

            val chunks = productsToInsert.chunked(500)
            chunks.forEach { chunk ->
                repository.insertProducts(chunk)
                kotlinx.coroutines.delay(50)
            }

            println("УСПЕХ: Загружено ${productsToInsert.size} продуктов из JSON")
        } catch (e: Exception) {
            e.printStackTrace()
            println("ОШИБКА ЗАГРУЗКИ JSON: ${e.message}. Грузим старый список из Kotlin.")
            repository.insertProducts(com.practicum.fitapp.presentation.nutrition.builtInProducts)
        }
    }

    /** Добавить новый продукт (вручную). */
    fun addProduct(
        name: String,
        category: String,
        calories: Int,
        protein: Double,
        fat: Double,
        carbs: Double
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addProduct(
                ProductEntity(
                    name = name,
                    category = category.ifBlank { "Прочее" },
                    caloriesPer100g = calories,
                    proteinPer100g = protein,
                    fatPer100g = fat,
                    carbsPer100g = carbs,
                    isUserCreated = true
                )
            )
        }
    }

    /** Добавить продукт через ИИ по названию. */
    fun addProductWithAi(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            _isAiLoading.value = true
            val result = aiCalorieRepository.analyzeFoodText(name)

            if (result.error == null && result.name != null) {
                repository.addProduct(
                    ProductEntity(
                        name = result.name,
                        category = "Прочее",
                        caloriesPer100g = result.caloriesPer100g ?: 0,
                        proteinPer100g = result.proteinPer100g ?: 0.0,
                        fatPer100g = result.fatPer100g ?: 0.0,
                        carbsPer100g = result.carbsPer100g ?: 0.0,
                        isUserCreated = true
                    )
                )
            } else {
                println("AI Error: ${result.error}")
            }
            _isAiLoading.value = false
        }
    }

    /** Удалить пользовательский продукт. */
    fun deleteProduct(product: ProductEntity) {
        if (!product.isUserCreated) return
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    // Поиск
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addRecipeToDiary(recipe: Recipe, dateTimestamp: Long, grams: Double) {
        viewModelScope.launch {
            val kcalPer100 = if (recipe.totalWeightGrams > 0)
                (recipe.totalsKcal / recipe.totalWeightGrams * 100).toInt() else 0
            val proteinPer100 = if (recipe.totalWeightGrams > 0)
                recipe.totalsProtein / recipe.totalWeightGrams * 100 else 0.0
            val fatPer100 = if (recipe.totalWeightGrams > 0)
                recipe.totalsFat / recipe.totalWeightGrams * 100 else 0.0
            val carbsPer100 = if (recipe.totalWeightGrams > 0)
                recipe.totalsCarbs / recipe.totalWeightGrams * 100 else 0.0

            repository.addMealEntry(
                com.practicum.fitapp.data.local.MealEntryEntity(
                    date = dateTimestamp,
                    productId = 0L,
                    productName = recipe.title,
                    productCalories = kcalPer100,
                    productProtein = proteinPer100,
                    productFat = fatPer100,
                    productCarbs = carbsPer100,
                    grams = grams
                )
            )
        }
    }
}