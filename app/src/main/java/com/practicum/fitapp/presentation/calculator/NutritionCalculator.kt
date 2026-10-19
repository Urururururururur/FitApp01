package com.practicum.fitapp.presentation.calculator

import com.practicum.fitapp.data.local.ActivityLevel
import com.practicum.fitapp.data.local.FitnessGoal
import com.practicum.fitapp.data.local.Gender
import com.practicum.fitapp.data.local.UserProfileEntity

/**
 * Результат расчёта нормы калорий.
 */
data class NutritionPlan(
    val bmr: Double,          // базовый метаболизм
    val tdee: Double,         // суточный расход
    val targetCalories: Double, // с учётом цели
    val proteinGrams: Double,
    val fatGrams: Double,
    val carbsGrams: Double
)

/**
 * Калькулятор нормы калорий и БЖУ по формуле Миффлина-Сан Жеора.
 */
object NutritionCalculator {

    /**
     * BMR — базовый метаболизм.
     * Мужчины: 10×вес + 6.25×рост − 5×возраст + 5
     * Женщины: 10×вес + 6.25×рост − 5×возраст − 161
     */
    fun calculateBmr(profile: UserProfileEntity): Double {
        val base = 10.0 * profile.weightKg +
                6.25 * profile.heightCm -
                5.0 * profile.age

        return when (Gender.valueOf(profile.gender)) {
            Gender.MALE -> base + 5
            Gender.FEMALE -> base - 161
        }
    }

    /** Коэффициент активности для TDEE. */
    private fun activityMultiplier(level: String): Double {
        return when (ActivityLevel.valueOf(level)) {
            ActivityLevel.SEDENTARY -> 1.2
            ActivityLevel.LIGHT -> 1.375
            ActivityLevel.MODERATE -> 1.55
            ActivityLevel.ACTIVE -> 1.725
            ActivityLevel.VERY_ACTIVE -> 1.9
        }
    }

    /** Коэффициент цели: -20% для похудения, +10% для набора. */
    private fun goalMultiplier(goal: String): Double {
        return when (FitnessGoal.valueOf(goal)) {
            FitnessGoal.LOSE -> 0.8
            FitnessGoal.MAINTAIN -> 1.0
            FitnessGoal.GAIN -> 1.1
        }
    }

    /** Полный расчёт плана: BMR, TDEE, калории и БЖУ. */
    fun calculatePlan(profile: UserProfileEntity): NutritionPlan {
        val bmr = calculateBmr(profile)
        val tdee = bmr * activityMultiplier(profile.activityLevel)
        val targetCalories = tdee * goalMultiplier(profile.goal)

        // Белки: 2 г на кг веса
        val proteinGrams = 2.0 * profile.weightKg

        // Жиры: 25% от калорий
        val fatGrams = (targetCalories * 0.25) / 9.0

        // Углеводы: остаток
        val carbsCalories = targetCalories - (proteinGrams * 4) - (fatGrams * 9)
        val carbsGrams = carbsCalories / 4.0

        return NutritionPlan(
            bmr = bmr,
            tdee = tdee,
            targetCalories = targetCalories,
            proteinGrams = proteinGrams,
            fatGrams = fatGrams,
            carbsGrams = carbsGrams
        )
    }
}