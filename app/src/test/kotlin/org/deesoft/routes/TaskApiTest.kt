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
import org.deesoft.service.TaskService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import java.time.LocalDateTime
import org.deesoft.plugins.DataNotFoundException
import org.deesoft.plugins.configureStatusPages

class TaskApiTest {

    private val mockTaskService = mockk<TaskService>()
    private val sampleTaskResponse = TaskResponse(
        id = 1L,
        title = "Title Test Task",
        description = "Testing API routes",
        isCompleted = false,
        isRecurring = false,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    // 1. GET /api/v1/tasks (ดึงทั้งหมด)
    @Test
    fun `GET tasks should return 200 OK and list of tasks`() = testApplication {
        application {
            install(ContentNegotiation) { json() }
            install(Koin) { modules(module { single { mockTaskService } }) }
            configureStatusPages()
            routing { route("/api/v1") { taskRouting() } }
        }

        every { mockTaskService.getAllTasks() } returns listOf(sampleTaskResponse)

        val response = client.get("/api/v1/tasks")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Title Test Task"))
    }

    // 2. GET /api/v1/tasks/{id} (ดึงตาม ID สำเร็จ)
    @Test
    fun `GET tasks by id should return 200 OK when task exists`() = testApplication {
        application {
            install(ContentNegotiation) { json() }
            install(Koin) { modules(module { single { mockTaskService } }) }
            configureStatusPages()
            routing { route("/api/v1") { taskRouting() } }
        }

        every { mockTaskService.getTaskById(1L) } returns sampleTaskResponse

        val response = client.get("/api/v1/tasks/1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Title Test Task"))
    }

    // 3. POST /api/v1/tasks (สร้าง Task สำเร็จ)
    @Test
    fun `POST tasks should return 201 Created`() = testApplication {
        application {
            install(ContentNegotiation) { json() }
            install(Koin) { modules(module { single { mockTaskService } }) }
            configureStatusPages()
            routing { route("/api/v1") { taskRouting() } }
        }

        every { mockTaskService.create(any(), any()) } returns sampleTaskResponse

        val response = client.post("/api/v1/tasks") {
            contentType(ContentType.Application.Json)
            setBody("""{"title": "New Task", "description": "Desc"}""")
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    // 4. PUT /api/v1/tasks/{id} (แก้ไข Task สำเร็จ)
    @Test
    fun `PUT tasks should return 200 OK when updated`() = testApplication {
        application {
            install(ContentNegotiation) { json() }
            install(Koin) { modules(module { single { mockTaskService } }) }
            configureStatusPages()
            routing { route("/api/v1") { taskRouting() } }
        }

        every { mockTaskService.update(eq(1L), any(), any(), any()) } returns sampleTaskResponse

        val response = client.put("/api/v1/tasks/1") {
            contentType(ContentType.Application.Json)
            setBody("""{"title": "Updated Title", "description": "Updated Desc", "isCompleted": false}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    // 5. PATCH /api/v1/tasks/{id}/complete (อัปเดตสถานะสำเร็จ)
    @Test
    fun `PATCH tasks complete should return 200 OK and updated task`() = testApplication {
        application {
            install(ContentNegotiation) { json() }
            install(Koin) { modules(module { single { mockTaskService } }) }
            configureStatusPages()
            routing { route("/api/v1") { taskRouting() } }
        }

        every { mockTaskService.updateCompletionStatus(1L, true) } returns sampleTaskResponse.copy(isCompleted = true)

        val response = client.patch("/api/v1/tasks/1/complete") {
            contentType(ContentType.Application.Json)
            setBody("""{"isCompleted": true}""")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    // 6. DELETE /api/v1/tasks/{id} (ลบ Task สำเร็จ)
    @Test
    fun `DELETE tasks should return 200 OK when deleted`() = testApplication {
        application {
            install(ContentNegotiation) { json() }
            install(Koin) { modules(module { single { mockTaskService } }) }
            configureStatusPages()
            routing { route("/api/v1") { taskRouting() } }
        }

        every { mockTaskService.delete(1L) } returns Unit // สมมติ delete คืนค่า Unit

        val response = client.delete("/api/v1/tasks/1")

        assertEquals(HttpStatusCode.OK, response.status)
    }

    // 7. GET /api/v1/tasks/99 (กรณีไม่พบข้อมูล ทดสอบ StatusPages 404)
    @Test
    fun `GET tasks by id should return 404 when task not found`() = testApplication {
        application {
            install(ContentNegotiation) { json() }
            install(Koin) { modules(module { single { mockTaskService } }) }
            configureStatusPages()
            routing { route("/api/v1") { taskRouting() } }
        }

        every { mockTaskService.getTaskById(99L) } throws DataNotFoundException(99L)

        val response = client.get("/api/v1/tasks/99")

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertTrue(response.bodyAsText().contains("99"))
    }
}