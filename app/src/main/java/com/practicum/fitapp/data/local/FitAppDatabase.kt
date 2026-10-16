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
    entities = [
        ProductEntity::class,
        MealEntryEntity::class,
        ExerciseEntity::class,
        WorkoutEntity::class,
        WorkoutExerciseEntity::class,
        SetEntity::class
    ],
    version = 5,
    exportSchema = false
)

abstract class FitAppDatabase : RoomDatabase() {

    /** Точка доступа к таблице products. */
    abstract fun productDao(): ProductDao
    abstract fun mealEntryDao(): MealEntryDao

    abstract fun workoutDao(): WorkoutDao

    abstract fun workoutExerciseDao(): WorkoutExerciseDao

    abstract fun setDao(): SetDao

    companion object {
        @Volatile
        private var INSTANCE: FitAppDatabase? = null

        /**
         * Миграция
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
        /**
         * Миграция с 3 на 4.
         * Добавляет колонки БЖУ в meal_entries.
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE meal_entries ADD COLUMN productProtein REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE meal_entries ADD COLUMN productFat REAL NOT NULL DEFAULT 0.0")
                db.execSQL("ALTER TABLE meal_entries ADD COLUMN productCarbs REAL NOT NULL DEFAULT 0.0")
            }
        }
        /**
         * Миграция с 4 на 5.
         * Создаёт четыре таблицы: exercises, workouts, workout_exercises, sets.
         * Порядок создания важен: сначала parents, потом children (FK).
         */
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Упражнения (нет FK, создаём первым)
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS exercises (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                muscleGroups TEXT NOT NULL,
                category TEXT NOT NULL,
                isBuiltIn INTEGER NOT NULL,
                description TEXT NOT NULL
            )
        """.trimIndent())

                // 2. Тренировки
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS workouts (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                date INTEGER NOT NULL,
                endDate INTEGER,
                name TEXT NOT NULL,
                note TEXT NOT NULL,
                isCompleted INTEGER NOT NULL,
                createdAt INTEGER NOT NULL
            )
        """.trimIndent())

                // 3. Упражнения в тренировке (зависит от exercises и workouts)
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS workout_exercises (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                workoutId INTEGER NOT NULL,
                exerciseId INTEGER NOT NULL,
                orderInWorkout INTEGER NOT NULL,
                note TEXT NOT NULL,
                FOREIGN KEY(workoutId) REFERENCES workouts(id) ON DELETE CASCADE,
                FOREIGN KEY(exerciseId) REFERENCES exercises(id) ON DELETE CASCADE
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_workout_exercises_workoutId ON workout_exercises(workoutId)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_workout_exercises_exerciseId ON workout_exercises(exerciseId)")

                // 4. Подходы (зависит от workout_exercises)
                db.execSQL("""
            CREATE TABLE IF NOT EXISTS sets (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                workoutExerciseId INTEGER NOT NULL,
                weight REAL,
                reps INTEGER,
                distance REAL,
                duration INTEGER,
                intensity TEXT NOT NULL,
                orderInExercise INTEGER NOT NULL,
                FOREIGN KEY(workoutExerciseId) REFERENCES workout_exercises(id) ON DELETE CASCADE
            )
        """.trimIndent())
                db.execSQL("CREATE INDEX IF NOT EXISTS index_sets_workoutExerciseId ON sets(workoutExerciseId)")
            }
        }


        fun getInstance(context: Context): FitAppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    FitAppDatabase::class.java,
                    "fitapp_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}