package org.deesoft.service

import org.deesoft.model.TaskResponse
import org.deesoft.plugins.DataNotFoundException
import org.deesoft.repository.TaskRepository

class TaskService(private val repository: TaskRepository) {

    fun getAllTasks(): List<TaskResponse> = repository.findAll()

    fun getTaskById(id: Long): TaskResponse {
        return repository.findById(id)
            ?: throw DataNotFoundException(id)
    }

    fun create(title: String, description: String?): TaskResponse {
        require(title.isNotBlank()) { "Task title cannot be blank" }
        return repository.store(title, description)
    }

    fun update(id: Long, title: String, description: String?, isCompleted: Boolean): TaskResponse {
        require(title.isNotBlank()) { "Task title cannot be blank" }
        return repository.update(id,title, description, isCompleted)
            ?: throw DataNotFoundException(id)
    }

    fun updateCompletionStatus(id: Long, isCompleted: Boolean): TaskResponse {
        return repository.updateCompletionStatus(id, isCompleted)
            ?: throw DataNotFoundException(id)
    }

    fun delete(id: Long) {
        val deleted = repository.delete(id)
        if (!deleted) {
            throw DataNotFoundException(id)
        }
    }
}