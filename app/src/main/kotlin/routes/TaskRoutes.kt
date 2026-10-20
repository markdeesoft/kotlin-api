package org.deesoft.routes

import org.deesoft.model.TaskRequest
import org.deesoft.service.TaskService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.deesoft.model.UpdateStatusRequest
import org.koin.ktor.ext.inject

fun Route.taskRouting() {

    val taskService by application.inject<TaskService>()

    route("/tasks") {

        // GET /api/v1/tasks - ดึงรายการทั้งหมด
        get {
            val tasks = taskService.getAllTasks()
            call.respond(tasks)
        }

        // GET /api/v1/tasks/{id} - ดึงข้อมูลตาม ID
        get("{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Task ID format")
                return@get
            }

            val task = taskService.getTaskById(id)
            if (task == null) {
                call.respond(HttpStatusCode.NotFound, "Task not found with id: $id")
            } else {
                call.respond(task)
            }
        }

        // POST /api/v1/tasks - สร้าง Task ใหม่
        post {
            try {
                val request = call.receive<TaskRequest>()
                val createdTask = taskService.create(request.title, request.description)
                call.respond(HttpStatusCode.Created, createdTask)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
            }
        }

        // PUT /api/v1/tasks - แก้ไข Task
        put("{id}") {

            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Task ID format")
                return@put
            }

            try {
                val request = call.receive<TaskRequest>()
                val createdTask = taskService.update(id, request.title, request.description,
                    request.isCompleted == true
                )
                call.respond(HttpStatusCode.OK, createdTask)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, e.message ?: "Invalid request")
            }
        }

        // เส้นทางสำหรับเปลี่ยนสถานะ isCompleted โดยเฉพาะ
        patch("/{id}/complete") {
            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid task ID format")
                return@patch
            }

            try {
                val request = call.receive<UpdateStatusRequest>()
                val updatedTask = taskService.updateCompletionStatus(id, request.isCompleted)
                call.respond(HttpStatusCode.OK, updatedTask)
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.NotFound, mapOf("error" to (e.message ?: "Task not found")))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid request body"))
            }
        }

        // DELETE /api/v1/tasks/{id} - ลบ Task
        delete("{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Invalid Task ID format")
                return@delete
            }

            val deleted = taskService.delete(id)
            if (deleted) {
                call.respond(HttpStatusCode.OK, "Task deleted successfully")
            } else {
                call.respond(HttpStatusCode.NotFound, "Task not found")
            }
        }
    }
}