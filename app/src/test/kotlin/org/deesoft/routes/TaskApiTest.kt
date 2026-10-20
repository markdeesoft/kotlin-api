package org.deesoft.routes

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.every
import io.mockk.mockk
import org.deesoft.model.TaskResponse
import org.deesoft.routes.taskRouting
import org.deesoft.service.TaskService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import java.time.LocalDateTime

class TaskApiTest {

    private val mockTaskService = mockk<TaskService>()

    private val sampleTaskResponse = TaskResponse(
        id = 1L,
        title = "Ktor Test Task",
        description = "Testing API routes",
        isCompleted = false,
        isRecurring = false,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Test
    fun `PATCH tasks complete should return 200 OK and updated task`() = testApplication {
        // 1. จำลอง Koin Module ให้จ่าย Mock TaskService ออกมาเวลาเรียกใช้งาน
        application {
            install(ContentNegotiation) { json() }
            install(Koin) {
                modules(module {
                    single { mockTaskService }
                })
            }
            routing {
                route("/api/v1") {
                    taskRouting()
                }
            }
        }

        // 2. กำหนดพฤติกรรม Mock Service
        every { mockTaskService.updateCompletionStatus(1L, true) } returns sampleTaskResponse.copy(isCompleted = true)

        // 3. จำลองการยิง HTTP PATCH Request ไปที่ Endpoint
        val response = client.patch("/api/v1/tasks/1/complete") {
            contentType(ContentType.Application.Json)
            setBody("""{"isCompleted": true}""")
        }

        // 4. ตรวจสอบผลลัพธ์ HTTP Status Code
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET tasks by id should return 404 when task not found`() = testApplication {
        application {
            install(ContentNegotiation) { json() }
            install(Koin) {
                modules(module {
                    single { mockTaskService }
                })
            }
            routing {
                route("/api/v1") {
                    taskRouting()
                }
            }
        }

        // ให้ Service คืนค่า null เมื่อหาไม่เจอ
        every { mockTaskService.getTaskById(99L) } returns null

        val response = client.get("/api/v1/tasks/99")

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals("Task not found with id: 99", response.bodyAsText())
    }
}