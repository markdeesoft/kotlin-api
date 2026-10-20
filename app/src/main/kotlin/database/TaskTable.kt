package org.deesoft.database

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object TaskTable : Table("tasks") {
    val id = long("id").autoIncrement()
    val title = varchar("title", 255)
    val description = text("description").nullable()
    val isCompleted = bool("is_completed").default(false)

    val sourceTaskId = long("source_task_id").nullable()
    val isRecurring = bool("is_recurring").default(false)
    val lastClonedDate = date("last_cloned_date").nullable()

    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updated_at").clientDefault { LocalDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}