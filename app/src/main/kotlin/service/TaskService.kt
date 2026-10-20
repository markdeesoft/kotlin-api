package org.deesoft.service

import org.deesoft.model.Task
import org.deesoft.model.TaskResponse
import org.deesoft.repository.TaskRepository

class TaskNotFoundException(id: Long) : RuntimeException("Task not found with id: $id")

class TaskService(private val repository: TaskRepository) {

    fun getAllTasks(): List<TaskResponse> = repository.findAll()

    fun getTaskById(id: Long): TaskResponse? = repository.findById(id)

    fun create(title: String, description: String?): TaskResponse {
        require(title.isNotBlank()) { "Task title cannot be blank" }
        return repository.store(title, description)
    }

    fun update(id: Long, title: String, description: String?, isCompleted: Boolean): TaskResponse {
        require(title.isNotBlank()) { "Task title cannot be blank" }
        return repository.update(id,title, description, isCompleted)
            ?: throw TaskNotFoundException(id)
    }

    fun updateCompletionStatus(id: Long, isCompleted: Boolean): TaskResponse {
        return repository.updateCompletionStatus(id, isCompleted)
            ?: throw TaskNotFoundException(id)
    }

    fun delete(id: Long): Boolean = repository.delete(id)
}