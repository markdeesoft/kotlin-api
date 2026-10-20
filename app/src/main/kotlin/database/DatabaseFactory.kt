package org.deesoft.database

import org.jetbrains.exposed.sql.Database
import java.io.File

object DatabaseFactory {
    fun init() {

        val envDbUrl = System.getenv("DATABASE_URL")
        val envDbDriver = System.getenv("DATABASE_DRIVER")

        if (!envDbUrl.isNullOrBlank() && !envDbDriver.isNullOrBlank()) {
            // ถ้ามีการกำหนดค่ามา ให้ใช้ค่าจาก Environment/'.env' ได้เลย
            Database.connect(
                url = System.getenv("DATABASE_URL"),
                driver = System.getenv("DATABASE_DRIVER")
            )

        } else {
            // สร้างไฟล์ database ไว้ในโฟลเดอร์โปรเจกต์ (เช่น tasks.db)
            val dbFile = File("data/tasks.db")
            dbFile.parentFile?.mkdirs()
            "jdbc:sqlite:${dbFile.absolutePath}"

            Database.connect(
                url = "jdbc:sqlite:${dbFile.absolutePath}",
                driver = "org.sqlite.JDBC"
            )
        }
    }
}