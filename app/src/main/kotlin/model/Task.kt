package org.deesoft.model

import kotlinx.serialization.Serializable
//import kotlinx.serialization.Transient
import org.deesoft.utils.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class Task(
    val id: Long,
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
    val sourceTaskId: Long? = null,
//    @Transient // สั่งให้ kotlinx.serialization เมินฟิลด์นี้ เวลาแปลงเป็น JSON จะถูกซ่อนอัตโนมัติ
//    val lastClonedDate: LocalDate,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime
)

@Serializable
data class TaskRequest(
    val title: String,
    val description: String? = null,
    val isCompleted: Boolean? = false
)

@Serializable
data class UpdateStatusRequest(
    val isCompleted: Boolean
)