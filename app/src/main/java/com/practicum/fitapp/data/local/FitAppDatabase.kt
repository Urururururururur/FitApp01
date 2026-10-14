package com.practicum.fitapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Главный класс базы данных.
 * entities — список всех таблиц, которые входят в эту БД.
 * version — номер версии схемы. Меняется при изменении таблиц.
 */
@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FitAppDatabase : RoomDatabase() {

    /** Точка доступа к таблице products. */
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: FitAppDatabase? = null

        /** Получить единственный экземпляр базы данных. */
        fun getInstance(context: Context): FitAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitAppDatabase::class.java,
                    "fitapp_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}