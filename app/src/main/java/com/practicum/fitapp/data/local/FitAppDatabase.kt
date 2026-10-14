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
    entities = [ProductEntity::class, MealEntryEntity::class],
    version = 3,
    exportSchema = false
)

abstract class FitAppDatabase : RoomDatabase() {

    /** Точка доступа к таблице products. */
    abstract fun productDao(): ProductDao
    abstract fun mealEntryDao(): MealEntryDao

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
        /**
         * Миграция с версии 2 на версию 3.
         * Создаёт таблицу meal_entries для записей о приёме пищи.
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS meal_entries (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date INTEGER NOT NULL,
                productId INTEGER NOT NULL,
                productName TEXT NOT NULL,
                productCalories INTEGER NOT NULL,
                grams REAL NOT NULL,
                FOREIGN KEY(productId) REFERENCES products(id) ON DELETE CASCADE
            )
            """.trimIndent()
                )
                db.execSQL("CREATE INDEX IF NOT EXISTS index_meal_entries_productId ON meal_entries(productId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_meal_entries_date ON meal_entries(date)")
            }
        }

        fun getInstance(context: Context): FitAppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FitAppDatabase::class.java,
                    "fitapp_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}