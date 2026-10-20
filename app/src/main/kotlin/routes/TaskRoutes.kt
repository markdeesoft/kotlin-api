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
                ?: throw IllegalArgumentException("Invalid Task ID format")

            val task = taskService.getTaskById(id)
            call.respond(task)
        }

        // POST /api/v1/tasks - สร้าง Task ใหม่
        post {
            val request = call.receive<TaskRequest>()
            val createdTask = taskService.create(request.title, request.description)
            call.respond(HttpStatusCode.Created, createdTask)
        }

        // PUT /api/v1/tasks - แก้ไข Task
        put("{id}") {

            val id = call.parameters["id"]?.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid Task ID format")

            val request = call.receive<TaskRequest>()
            val updatedTask = taskService.update(
                id = id,
                title = request.title,
                description = request.description,
                isCompleted = request.isCompleted == true
            )
            call.respond(HttpStatusCode.OK, updatedTask)
        }

        // เส้นทางสำหรับเปลี่ยนสถานะ isCompleted โดยเฉพาะ
        patch("/{id}/complete") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid Task ID format")

            val request = call.receive<UpdateStatusRequest>()
            val updatedTask = taskService.updateCompletionStatus(id, request.isCompleted)
            call.respond(HttpStatusCode.OK, updatedTask)
        }

        // DELETE /api/v1/tasks/{id} - ลบ Task
        delete("{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: throw IllegalArgumentException("Invalid Task ID format")

            taskService.delete(id)
            call.respond(HttpStatusCode.OK, mapOf("message" to "Task deleted successfully"))
        }
    }
}