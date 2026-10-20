package org.deesoft.database

import org.jetbrains.exposed.sql.Database
import java.io.File

object DatabaseFactory {
    fun init() {
        // สร้างไฟล์ database ไว้ในโฟลเดอร์โปรเจกต์ (เช่น tasks.db)
        val dbFile = File("data/tasks.db")
        dbFile.parentFile?.mkdirs()

        Database.connect(
            url = "jdbc:sqlite:${dbFile.absolutePath}",
            driver = "org.sqlite.JDBC"
        )
    }
}