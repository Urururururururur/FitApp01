package com.practicum.fitapp.presentation.common

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Утилиты для работы с датами.
 *
 * В БД даты хранятся как Long (timestamp в миллисекундах).
 * В UI работаем с LocalDate (просто дата, без времени).
 *
 * Эти функции — мост между двумя мирами.
 */
object DateUtils {

    private val RU_LOCALE = Locale("ru", "RU")

    /**
     * Превращает timestamp (начало дня) в LocalDate.
     * Используется для отображения дат в календаре.
     */
    fun timestampToLocalDate(timestamp: Long): LocalDate {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    /**
     * Превращает LocalDate в timestamp начала этого дня (00:00:00).
     * Используется для запросов в БД.
     */
    fun localDateToStartOfDay(date: LocalDate): Long {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    /**
     * Timestamp начала сегодняшнего дня.
     * Эквивалент старой функции startOfToday() из NutritionViewModel.
     */
    fun startOfToday(): Long = localDateToStartOfDay(LocalDate.now())

    /**
     * Форматирование даты как "14.10.2026".
     */
    fun formatShort(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    }

    /**
     * Форматирование как "Октябрь 2026" — для заголовка календаря.
     */
    fun formatMonthYear(date: LocalDate): String {
        val monthName = date.format(
            DateTimeFormatter.ofPattern("LLLL", RU_LOCALE)
        ).replaceFirstChar { it.uppercase() }
        return "$monthName ${date.year}"
    }

    /**
     * Короткое название дня недели: "пн", "вт", ...
     */
    fun dayOfWeekShort(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("EE", RU_LOCALE))
    }

    /**
     * Это выходной день? (суббота или воскресенье)
     */
    fun isWeekend(date: LocalDate): Boolean {
        val dow = date.dayOfWeek.value // 1 = понедельник, 7 = воскресенье
        return dow == 6 || dow == 7
    }

    /**
     * Начало недели (понедельник), в которую попадает date.
     */
    fun startOfWeek(date: LocalDate): LocalDate {
        val dow = date.dayOfWeek.value // 1 = пн, ..., 7 = вс
        return date.minusDays((dow - 1).toLong())
    }

    /**
     * Список из 7 дней, начиная с понедельника недели, в которую попадает date.
     */
    fun weekDays(date: LocalDate): List<LocalDate> {
        val monday = startOfWeek(date)
        return (0..6).map { monday.plusDays(it.toLong()) }
    }
}