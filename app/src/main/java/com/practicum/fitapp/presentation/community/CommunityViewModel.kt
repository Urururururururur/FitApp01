package com.practicum.fitapp.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.fitapp.data.local.Achievement
import com.practicum.fitapp.data.repository.AchievementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlinx.coroutines.launch

/**
 * Модель для UI: ачивка + её статус (открыта/закрыта).
 */
data class AchievementUiState(
    val achievement: Achievement,
    val isUnlocked: Boolean,
    val unlockedAt: Long?
)



@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val repository: AchievementRepository
) : ViewModel() {

    /** Список всех ачивок с их статусом. */
    val achievements: StateFlow<List<AchievementUiState>> = repository.getUnlocked()
        .map { unlockedList ->
            val unlockedMap = unlockedList.associateBy { it.achievementId }
            Achievement.entries.map { achievement ->
                val unlocked = unlockedMap[achievement.id]
                AchievementUiState(
                    achievement = achievement,
                    isUnlocked = unlocked != null,
                    unlockedAt = unlocked?.unlockedAt
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    init {
        // При загрузке экрана проверяем, не открылись ли новые ачивки
        viewModelScope.launch {
            checkAchievements()
        }
    }

    private suspend fun checkAchievements() {
        // 1. Первая тренировка, 10 тренировок, 50 тренировок
        val totalWorkouts = repository.getTotalWorkouts()
        if (totalWorkouts >= 1) repository.unlock("first_workout")
        if (totalWorkouts >= 10) repository.unlock("ten_workouts")
        if (totalWorkouts >= 50) repository.unlock("fifty_workouts")

        // 2. Первая тонна, 10 тонн
        val totalWeight = repository.getTotalWeightLifted()
        if (totalWeight >= 1000.0) repository.unlock("first_ton")
        if (totalWeight >= 10000.0) repository.unlock("ten_tons")

        // 3. Первые 5 км, 10 км
        val totalDistance = repository.getTotalDistance()
        if (totalDistance >= 5000.0) repository.unlock("first_5km")
        if (totalDistance >= 10000.0) repository.unlock("first_10km")

        // 4. 7 дней дневника, 30 дней дневника
        val diaryDays = repository.getTotalDiaryDays()
        if (diaryDays >= 7) repository.unlock("diary_7")
        if (diaryDays >= 30) repository.unlock("diary_30")

        // 5. 10 разных упражнений
        val uniqueExercises = repository.getTotalUniqueExercises()
        if (uniqueExercises >= 10) repository.unlock("ten_exercises")
    }
}
