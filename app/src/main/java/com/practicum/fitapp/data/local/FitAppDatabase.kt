package com.practicum.fitapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Главный класс базы данных.
 * entities — список всех таблиц, которые входят в эту БД.
 * version — номер версии схемы. Меняется при изменении таблиц.
 */
@Database(
    entities = [ProductEntity::class],
    version = 2,
    exportSchema = false
)
abstract class FitAppDatabase : RoomDatabase() {

    /** Точка доступа к таблице products. */
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: FitAppDatabase? = null

        /**
         * Миграция с версии 1 на версию 2.
         * Добавляет колонку category в таблицу products.
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE products ADD COLUMN category TEXT NOT NULL DEFAULT 'Прочее'"
                )
            }
        }

        fun getInstance(context: Context): FitAppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FitAppDatabase::class.java,
                    "fitapp_database"
                )
                    .addMigrations(MIGRATION_1_2)   // ← регистрируем миграцию
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}