package com.practicum.fitapp.data.di

import android.content.Context
import androidx.room.Room
import com.practicum.fitapp.data.local.ExerciseDao
import com.practicum.fitapp.data.local.FitAppDatabase
import com.practicum.fitapp.data.local.MealEntryDao
import com.practicum.fitapp.data.local.ProductDao
import com.practicum.fitapp.data.local.SetDao
import com.practicum.fitapp.data.local.WorkoutDao
import com.practicum.fitapp.data.local.WorkoutExerciseDao
import com.practicum.fitapp.data.repository.NutritionRepository
import com.practicum.fitapp.data.repository.TrainingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.practicum.fitapp.data.local.UserProfileDao
import com.practicum.fitapp.data.repository.UserRepository

/**
 * Модуль Hilt: объясняет, как создавать объекты для БД.
 * SingletonComponent — все объекты живут столько же, сколько приложение.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FitAppDatabase {
        return Room.databaseBuilder(
            context,
            FitAppDatabase::class.java,
            "fitapp_database"
        )
            .addMigrations(
                FitAppDatabase.MIGRATION_1_2,
                FitAppDatabase.MIGRATION_2_3,
                FitAppDatabase.MIGRATION_3_4,
                FitAppDatabase.MIGRATION_4_5,
                FitAppDatabase.MIGRATION_5_6,
                FitAppDatabase.MIGRATION_6_7
            )
            .build()
    }

    @Provides
    fun provideProductDao(db: FitAppDatabase): ProductDao = db.productDao()

    @Provides
    fun provideMealEntryDao(db: FitAppDatabase): MealEntryDao = db.mealEntryDao()

    @Provides
    @Singleton
    fun provideNutritionRepository(
        productDao: ProductDao,
        mealEntryDao: MealEntryDao
    ): NutritionRepository = NutritionRepository(productDao, mealEntryDao)

    @Provides
    fun provideWorkoutDao(db: FitAppDatabase): WorkoutDao = db.workoutDao()

    @Provides
    fun provideWorkoutExerciseDao(db: FitAppDatabase): WorkoutExerciseDao = db.workoutExerciseDao()

    @Provides
    fun provideSetDao(db: FitAppDatabase): SetDao = db.setDao()

    @Provides
    fun provideUserProfileDao(db: FitAppDatabase): UserProfileDao = db.userProfileDao()

    @Provides
    @Singleton
    fun provideUserRepository(dao: UserProfileDao): UserRepository = UserRepository(dao)

    @Provides
    fun provideExerciseDao(db: FitAppDatabase): ExerciseDao = db.exerciseDao()
    @Provides
    @Singleton
    fun provideTrainingRepository(
        workoutDao: WorkoutDao,
        workoutExerciseDao: WorkoutExerciseDao,
        setDao: SetDao,
        exerciseDao: ExerciseDao
    ): TrainingRepository = TrainingRepository(workoutDao, workoutExerciseDao, setDao, exerciseDao)
}