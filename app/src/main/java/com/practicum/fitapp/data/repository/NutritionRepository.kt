package com.practicum.fitapp.data.repository

import com.practicum.fitapp.data.local.DailyCalories
import com.practicum.fitapp.data.local.MealEntryDao
import com.practicum.fitapp.data.local.MealEntryEntity
import com.practicum.fitapp.data.local.ProductDao
import com.practicum.fitapp.data.local.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository для раздела «Питание».
 * Объединяет работу со справочником продуктов и записями о приёме пищи.
 */
class NutritionRepository(
    private val productDao: ProductDao,
    private val mealEntryDao: MealEntryDao
) {
    fun getDailyCalories(startDate: Long, endDate: Long): Flow<List<DailyCalories>> =
        mealEntryDao.getDailyCalories(startDate, endDate)

    // ==================== Продукты (справочник) ====================

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAll()

    fun searchProducts(query: String): Flow<List<ProductEntity>> =
        productDao.searchByName(query)

    suspend fun addProduct(product: ProductEntity) {
        productDao.insert(product)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productDao.delete(product)
    }

    // ==================== Записи о еде ====================

    /** Записи за конкретный день. */
    fun getEntriesByDate(date: Long): Flow<List<MealEntryEntity>> =
        mealEntryDao.getByDate(date)

    /** Все даты, за которые есть записи. */
    fun getAllDates(): Flow<List<Long>> = mealEntryDao.getAllDates()

    /** Добавить запись о еде. */
    suspend fun addMealEntry(entry: MealEntryEntity) {
        mealEntryDao.insert(entry)
    }

    /** Удалить запись о еде по id. */
    suspend fun deleteMealEntryById(id: Long) {
        mealEntryDao.deleteById(id)
    }
}