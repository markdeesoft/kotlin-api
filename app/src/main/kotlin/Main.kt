package org.deesoft

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.deesoft.di.appModule
import org.deesoft.routes.mainRouting
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import kotlinx.serialization.json.Json
import org.deesoft.database.DatabaseFactory
import org.deesoft.database.DatabaseSeeder
import org.deesoft.plugins.configureStatusPages
import org.deesoft.repository.TaskRepository
import org.flywaydb.core.Flyway
import java.time.Duration
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.milliseconds
import io.github.cdimascio.dotenv.dotenv
import org.deesoft.plugins.configureMonitoring

fun main() {

    val dotenv = dotenv {
        ignoreIfMissing = true
    }

    val port = dotenv["PORT"]?.toIntOrNull()
        ?: System.getenv("PORT")?.toIntOrNull()
        ?: 8080

    val host = dotenv["HOST"]
        ?: System.getenv("HOST")
        ?: "0.0.0.0"

    embeddedServer(Netty, port = port, host = host, module = Application::module)
        .start(wait = true)
}

fun Application.module() {

    configureMonitoring()

    // StatusPages เพื่อให้ดักจับ Error ได้ทุกส่วน
    configureStatusPages()

    // เชื่อมต่อฐานข้อมูล
    DatabaseFactory.init()

    // รัน Flyway Migration อัตโนมัติ
    val flyway = Flyway.configure()
        .dataSource("jdbc:sqlite:data/tasks.db", "", "")
        .load()
    flyway.migrate()

    // รัน Seeder ข้อมูลตั้งต้น
    DatabaseSeeder.seed()

    // เปิด Background Worker สำหรับรัน Task Cloning อัตโนมัติ
    // ใช้งานจริงอาจจะตั้งค่าผ่าน Clontab เอา
    startRecurringTaskScheduler()

    // ติดตั้ง Koin Plugin
    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }

    // เปิดใช้งาน JSON Serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // กำหนด Routing ของ API
    routing {
        // เรียกใช้งาน Routing ที่แยกไฟล์ไว้
        mainRouting()
    }
}

@OptIn(DelicateCoroutinesApi::class)
fun startRecurringTaskScheduler() {
    GlobalScope.launch {

        val repository = TaskRepository() // ค่อยดึงผ่าน Koin

        while (true) {
            try {
                // รันฟังก์ชันตรวจสอบและโคลน Task ทุกครั้งที่ระบบทำงานวนรอบ
                repository.processDailyRecurringTasks()
                println("🔄 Daily recurring tasks checked and processed.")
            } catch (e: Exception) {
                println("❌ Error processing recurring tasks: ${e.message}")
            }

            val now = LocalDateTime.now()
            val nextMidnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0)
            val delayMillis = Duration.between(now, nextMidnight).toMillis()

            println("⏳ Next recurring task check scheduled in approximately ${delayMillis / 1000 / 60} minutes.")

            // หน่วงเวลาข้ามไปจนถึงวันใหม่ (รันวันละ 1 ครั้ง)
            delay(delayMillis.milliseconds)
        }
    }
}