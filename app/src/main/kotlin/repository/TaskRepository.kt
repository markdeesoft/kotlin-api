package org.deesoft.repository

import org.deesoft.database.TaskTable
import org.deesoft.model.Task
import org.deesoft.model.TaskResponse
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDate
import java.time.LocalDateTime

class TaskRepository {

    private fun ResultRow.toTaskResponse(): TaskResponse {
        return TaskResponse(
            id = this[TaskTable.id],
            title = this[TaskTable.title],
            description = this[TaskTable.description],
            isCompleted = this[TaskTable.isCompleted],
            isRecurring = this[TaskTable.isRecurring],
//            lastClonedDate = this[TaskTable.lastClonedDate],
            createdAt = this[TaskTable.createdAt],
            updatedAt = this[TaskTable.updatedAt]
        )
    }

    fun findAll(): List<TaskResponse> = transaction {
        TaskTable.selectAll().map { it.toTaskResponse() }
    }

    fun findById(id: Long): TaskResponse? = transaction {
        TaskTable.selectAll()
            .where { TaskTable.id eq id }
            .map { it.toTaskResponse() }
            .singleOrNull()
    }

    fun store(title: String, description: String?): TaskResponse = transaction {

        val now = LocalDateTime.now()

        val insertedId = TaskTable.insert {
            it[TaskTable.title] = title
            it[TaskTable.description] = description
            it[TaskTable.isCompleted] = false
            it[TaskTable.createdAt] = now
            it[TaskTable.updatedAt] = now
        }[TaskTable.id]

        // เรียกใช้ findById ซึ่งข้างในเรียก .toTaskResponse() อีกที ได้ทั้งความสะอาดและชัวร์ว่าข้อมูลตรงกับ DB
        findById(insertedId) ?: throw IllegalStateException("Failed to retrieve created task")
    }

    fun update(id: Long, title: String, description: String?, isCompleted: Boolean): TaskResponse? = transaction {
        val now = LocalDateTime.now()
        val updatedRows = TaskTable.update({ TaskTable.id.eq(id) }) {
            it[TaskTable.title] = title
            it[TaskTable.description] = description
            it[TaskTable.isCompleted] = isCompleted
            it[TaskTable.updatedAt] = now // อัปเดตเวลาแก้ไขล่าสุดอัตโนมัติ
        }

        if (updatedRows > 0) findById(id) else null
    }

    fun delete(id: Long): Boolean = transaction {
        val deletedRows = TaskTable.deleteWhere { TaskTable.id.eq(id) }
        deletedRows > 0
    }

    fun updateCompletionStatus(id: Long, isCompleted: Boolean): TaskResponse? = transaction {
        val now = LocalDateTime.now()
        val updatedRows = TaskTable.update({ TaskTable.id.eq(id) }) {
            it[TaskTable.isCompleted] = isCompleted
            it[TaskTable.updatedAt] = now
        }

        if (updatedRows > 0) findById(id) else null
    }

    fun processDailyRecurringTasks(): Unit = transaction {

        val today = LocalDate.now() // รูปแบบ "YYYY-MM-DD"

        // ดึงงานที่เป็น Recurring ทั้งหมดที่ทำเสร็จแล้ว และยังไม่เคยถูกโคลนในวันนี้
//        val tasksToClone = TaskTable.selectAll().map { it.toTaskResponse() }.filter { task ->
//            task.isRecurring && task.isCompleted && task.lastClonedDate != today
//        }
        val tasksToClone = TaskTable.selectAll()
            .where {
                (TaskTable.isRecurring eq true) and
                        (TaskTable.isCompleted eq true) and
                        ((TaskTable.lastClonedDate.isNull()) or (TaskTable.lastClonedDate neq today))
            }
            .map { row ->
                // Map เป็น Object ชั่วคราวเฉพาะตอนที่จะเอาไปใช้โคลน
                object {
                    val id = row[TaskTable.id]
                    val title = row[TaskTable.title]
                    val description = row[TaskTable.description]
                }
            }

        for (task in tasksToClone) {
            val now = LocalDateTime.now()

            // 1. สร้าง Task ใหม่สำหรับวันนี้
            TaskTable.insert {
                it[TaskTable.title] = task.title
                it[TaskTable.description] = task.description
                it[TaskTable.isCompleted] = false // งานใหม่ของวันนี้เริ่มจากยังไม่เสร็จ
                it[TaskTable.isRecurring] = true  // สืบทอดความเป็น Recurring ต่อไป
                it[TaskTable.sourceTaskId] = task.id // บันทึก ID ต้นทาง
                it[TaskTable.lastClonedDate] = null
                it[TaskTable.createdAt] = now
                it[TaskTable.updatedAt] = now
            }

            // 2. อัปเดตงานของเมื่อวานว่าถูกโคลนไปแล้วในวันนี้ เพื่อป้องกันการโคลนซ้ำ
            TaskTable.update({ TaskTable.id.eq(task.id) }) {
                it[TaskTable.lastClonedDate] = today
            }
        }
    }
}