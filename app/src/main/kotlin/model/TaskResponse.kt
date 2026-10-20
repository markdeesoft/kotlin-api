package org.deesoft.model

import kotlinx.serialization.Serializable
import org.deesoft.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class TaskResponse(
    val id: Long,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime
)