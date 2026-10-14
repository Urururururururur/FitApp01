package com.practicum.fitapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO — интерфейс для работы с таблицей products.
 * Room сам сгенерирует реализацию этого интерфейса.
 */
@Dao
interface ProductDao {

    /** Получить все продукты. Возвращает Flow — поток данных. */
    @Query("SELECT * FROM products ORDER BY name")
    fun getAll(): Flow<List<ProductEntity>>

    /** Найти продукты по названию (частичное совпадение). */
    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%'")
    fun searchByName(query: String): Flow<List<ProductEntity>>

    /** Вставить продукт. */
    @Insert
    suspend fun insert(product: ProductEntity)

    /** Удалить продукт. */
    @Delete
    suspend fun delete(product: ProductEntity)
}