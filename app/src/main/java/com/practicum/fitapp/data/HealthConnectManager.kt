package com.practicum.fitapp.data

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ActiveCaloriesBurnedRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class HealthConnectManager @Inject constructor(
    private val context: Context
) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    // Список всех разрешений
    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(ActiveCaloriesBurnedRecord::class)
    )

    suspend fun hasAllPermissions(): Boolean {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return granted.containsAll(permissions)
    }

    data class HealthMetrics(
        val steps: Long = 0,
        val avgHeartRate: Long? = null,
        val sleepMinutes: Long = 0,
        val calories: Double = 0.0
    )

    suspend fun readHealthMetrics(): HealthMetrics {
        val now = Instant.now()
        val zone = ZoneId.systemDefault()

        val startOfDay = LocalDate.now().atStartOfDay(zone).toInstant()

        // Для сна: берем период с 12:00 вчера до 12:00 сегодня
        val startOfSleepPeriod = LocalDate.now().minusDays(1).atTime(12, 0).atZone(zone).toInstant()
        val endOfSleepPeriod = LocalDate.now().atTime(12, 0).atZone(zone).toInstant()

        // 1. Шаги
        val stepsResponse = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
            )
        )
        val steps = stepsResponse[StepsRecord.COUNT_TOTAL] ?: 0L

        // 2. Пульс
        val hrResponse = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(HeartRateRecord.BPM_AVG),
                timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
            )
        )
        val avgHr = hrResponse[HeartRateRecord.BPM_AVG]

        // 3. Сон (Читаем сессии и суммируем вручную, так как aggregate для сна не работает)
        val sleepRecords = try {
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startOfSleepPeriod, endOfSleepPeriod)
                )
            ).records
        } catch (e: Exception) {
            emptyList()
        }
        val sleepMinutes = sleepRecords.sumOf { Duration.between(it.startTime, it.endTime).toMinutes() }

        // 4. Сожженные калории
        val calResponse = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
            )
        )
        val calories = calResponse[ActiveCaloriesBurnedRecord.ACTIVE_CALORIES_TOTAL]?.inKilocalories ?: 0.0

        return HealthMetrics(
            steps = steps,
            avgHeartRate = avgHr,
            sleepMinutes = sleepMinutes,
            calories = calories
        )
    }
}