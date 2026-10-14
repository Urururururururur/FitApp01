package com.practicum.fitapp.data.repository

import com.practicum.fitapp.data.local.ProductDao
import com.practicum.fitapp.data.local.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository — посредник между ViewModel и источником данных.
 * Сегодня источник — только Room.
 * Завтра добавим сеть (Supabase) — и ViewModel не заметит изменений.
 */
class ProductRepository(private val dao: ProductDao) {

    /** Получить все продукты как поток. */
    fun getAllProducts(): Flow<List<ProductEntity>> = dao.getAll()

    /** Найти продукты по названию. */
    fun searchProducts(query: String): Flow<List<ProductEntity>> = dao.searchByName(query)

    /** Добавить продукт. */
    suspend fun addProduct(product: ProductEntity) {
        dao.insert(product)
    }

    /** Удалить продукт. */
    suspend fun deleteProduct(product: ProductEntity) {
        dao.delete(product)
    }
}