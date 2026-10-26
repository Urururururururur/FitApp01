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
    /** Только встроенные продукты (справочник). */
    @Query("SELECT * FROM products WHERE isUserCreated = 0 ORDER BY name")
    fun getBuiltIn(): Flow<List<ProductEntity>>

    /** Только созданные пользователем. */
    @Query("SELECT * FROM products WHERE isUserCreated = 1 ORDER BY name")
    fun getUserCreated(): Flow<List<ProductEntity>>

    /** Использованные: те, что хоть раз добавлялись в записи о еде. */
    @Query("""
    SELECT DISTINCT p.* FROM products p
    INNER JOIN meal_entries me ON me.productId = p.id
    ORDER BY p.name
""")
    fun getUsed(): Flow<List<ProductEntity>>
    /** Проверяет, есть ли встроенные продукты в базе. */
    @Query("SELECT COUNT(*) FROM products WHERE isUserCreated = 0")
    suspend fun countBuiltIn(): Int

    /** Вставляет список продуктов, игнорируя дубликаты. */
    @Insert(onConflict = androidx.room.OnConflictStrategy.IGNORE)
    suspend fun insertAll(products: List<ProductEntity>)
    @Query("SELECT * FROM products WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): ProductEntity?





}