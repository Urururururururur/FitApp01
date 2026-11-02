package com.practicum.fitapp.data // или где он у тебя лежит

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import javax.inject.Inject



class HealthConnectManager @Inject constructor( // ← ДОБАВИТЬ @Inject constructor
    private val context: Context
) {
    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    // Список разрешений, которые мы будем запрашивать
    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class)
        // Сюда можно добавить другие, например, для пульса
    )

    // Проверка, есть ли у нас уже разрешения
    suspend fun hasAllPermissions(): Boolean {
        val granted = healthConnectClient.permissionController.getGrantedPermissions()
        return granted.containsAll(permissions)
    }

    // Чтение шагов за сегодня
    suspend fun readStepsToday(): Long {
        val now = Instant.now()
        val startOfDay = now.atZone(java.time.ZoneId.systemDefault()).toLocalDate().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()

        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startOfDay, now)
            )
        )
        return response[StepsRecord.COUNT_TOTAL] ?: 0L
    }
}