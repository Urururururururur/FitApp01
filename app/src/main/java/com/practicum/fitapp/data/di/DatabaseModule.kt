package com.practicum.fitapp.data.di

import android.content.Context
import androidx.room.Room
import com.practicum.fitapp.data.local.FitAppDatabase
import com.practicum.fitapp.data.local.ProductDao
import com.practicum.fitapp.data.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
            .addMigrations(FitAppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideProductDao(db: FitAppDatabase): ProductDao = db.productDao()

    @Provides
    @Singleton
    fun provideProductRepository(dao: ProductDao): ProductRepository =
        ProductRepository(dao)
}