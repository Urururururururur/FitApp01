package com.practicum.fitapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    /** Все упражнения, отсортированные по имени. */
    @Query("SELECT * FROM exercises ORDER BY name")
    fun getAll(): Flow<List<ExerciseEntity>>

    /** Только встроенные. */
    @Query("SELECT * FROM exercises WHERE isBuiltIn = 1 ORDER BY name")
    fun getBuiltIn(): Flow<List<ExerciseEntity>>

    /** Только пользовательские. */
    @Query("SELECT * FROM exercises WHERE isBuiltIn = 0 ORDER BY name")
    fun getUserCreated(): Flow<List<ExerciseEntity>>

    /** Сколько встроенных уже есть. Для проверки "надо ли наполнять". */
    @Query("SELECT COUNT(*) FROM exercises WHERE isBuiltIn = 1")
    suspend fun countBuiltIn(): Int

    @Insert
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Insert
    suspend fun insert(exercise: ExerciseEntity): Long

    @Query("DELETE FROM exercises WHERE id = :id AND isBuiltIn = 0")
    suspend fun deleteUserCreated(id: Long)
}