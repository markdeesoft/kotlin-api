package org.deesoft.service

import org.deesoft.model.TaskResponse
import org.deesoft.plugins.DataNotFoundException
import org.deesoft.repository.TaskRepository
import org.slf4j.LoggerFactory

class TaskService(private val repository: TaskRepository) {

    private val logger = LoggerFactory.getLogger(TaskService::class.java)

    fun getAllTasks(): List<TaskResponse> {
//        logger.info("Fetching all tasks")
        val tasks = repository.findAll()
//        logger.info("Successfully fetched {} tasks", tasks.size)
        return tasks
    }

    fun getTaskById(id: Long): TaskResponse {

//        logger.info("Fetching task by id: {}", id)
        val task = repository.findById(id)
        if (task == null) {
//            logger.warn("Task not found with id: {}", id)
            throw DataNotFoundException(id)
        }
        return task
    }

    fun create(title: String, description: String?): TaskResponse {

        logger.info("Creating new task with title: '{}'", title)

        require(title.isNotBlank()) {
            logger.warn("Failed to create task: title is blank")
            "Task title cannot be blank"
        }
        return repository.store(title, description)
    }

    fun update(id: Long, title: String, description: String?, isCompleted: Boolean): TaskResponse {

        logger.info("Updating task with id: {}", id)

        require(title.isNotBlank()) {
            logger.warn("Failed to update task id {}: title is blank", id)
            "Task title cannot be blank"
        }

        val updatedTask = repository.update(id, title, description, isCompleted)
        if (updatedTask == null) {
            logger.warn("Task not found for update with id: {}", id)
            throw DataNotFoundException(id)
        }
        logger.info("Successfully updated task with id: {}", id)
        return updatedTask
    }

    fun updateCompletionStatus(id: Long, isCompleted: Boolean): TaskResponse {

//        logger.info("Updating completion status for task id: {} to {}", id, isCompleted)

        val updatedTask = repository.updateCompletionStatus(id, isCompleted)
        if (updatedTask == null) {
//            logger.warn("Task not found for status update with id: {}", id)
            throw DataNotFoundException(id)
        }
//        logger.info("Successfully updated completion status for task id: {}", id)
        return updatedTask
    }

    fun delete(id: Long) {
        logger.info("Deleting task with id: {}", id)
        val deleted = repository.delete(id)
        if (!deleted) {
            logger.warn("Task not found for deletion with id: {}", id)
            throw DataNotFoundException(id)
        }
        logger.info("Successfully deleted task with id: {}", id)
    }
}