package org.deesoft.database

import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

object DatabaseSeeder {
    fun seed() {
        transaction {
            // เช็คว่ามีข้อมูลอยู่แล้วหรือยัง ถ้ายังไม่มีค่อยใส่ Seed ลงไป
            if (TaskTable.selectAll().count() == 0L) {
                TaskTable.insert {
                    it[title] = "เรียนรู้ Ktor 3.0 & Exposed"
                    it[description] = "ศึกษาการทำ ORM และ Migration ร่วมกับ SQLite"
                    it[isCompleted] = true
                }
                TaskTable.insert {
                    it[title] = "พัฒนา Portfolio Backend"
                    it[description] = "จัดโครงสร้าง Clean Architecture ให้เป็นมืออาชีพ"
                    it[isCompleted] = false
                }
                println("🌱 Database Seeded successfully!")
            }
        }
    }
}