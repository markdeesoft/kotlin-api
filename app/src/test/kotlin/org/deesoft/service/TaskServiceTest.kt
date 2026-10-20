package org.deesoft.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.deesoft.model.TaskResponse
import org.deesoft.plugins.DataNotFoundException
import org.deesoft.repository.TaskRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class TaskServiceTest {

    private val repository = mockk<TaskRepository>()
    private val taskService = TaskService(repository)

    private val sampleTask = TaskResponse(
        id = 1L,
        title = "Test Task",
        description = "Description",
        isCompleted = false,
        isRecurring = false,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    // 1. ทดสอบ GET ทั้งหมด (getAllTasks)
    @Test
    fun `getAllTasks should return list of tasks when repository has data`() {
        every { repository.findAll() } returns listOf(sampleTask)

        val result = taskService.getAllTasks()

        assertEquals(1, result.size)
        assertEquals("Test Task", result[0].title)
        verify(exactly = 1) { repository.findAll() }
    }

    // 2. ทดสอบ GET ตาม ID สำเร็จ (getTaskById)
    @Test
    fun `getTaskById should return task when repository returns task`() {
        every { repository.findById(1L) } returns sampleTask

        val result = taskService.getTaskById(1L)

        assertNotNull(result)
        assertEquals(1L, result.id)
        assertEquals("Test Task", result.title)
        verify(exactly = 1) { repository.findById(1L) }
    }

    // 3. ทดสอบ GET ตาม ID แล้วไม่พบ (พ่น DataNotFoundException)
    @Test
    fun `getTaskById should throw exception when task not found`() {
        every { repository.findById(99L) } returns null

        val exception = assertThrows<DataNotFoundException> {
            taskService.getTaskById(99L)
        }
        assertEquals("data not found with id: 99", exception.message)
    }

    // 4. ทดสอบสร้าง Task สำเร็จ (create)
    @Test
    fun `create task should return created task when valid`() {
        every { repository.store("New Task", "Desc") } returns sampleTask

        val result = taskService.create("New Task", "Desc")

        assertNotNull(result)
        assertEquals("Test Task", result.title)
        verify(exactly = 1) { repository.store("New Task", "Desc") }
    }

    // 5. ทดสอบสร้าง Task แล้ว Title เป็นค่าว่าง (พ่น IllegalArgumentException)
    @Test
    fun `create task should throw exception when title is blank`() {
        val exception = assertThrows<IllegalArgumentException> {
            taskService.create("   ", "Description")
        }
        assertEquals("Task title cannot be blank", exception.message)
    }

    // 6. ทดสอบอัปเดต Task สำเร็จ (update)
    @Test
    fun `update task should return updated task when successful`() {
        val updatedSample = sampleTask.copy(title = "Updated Title")
        every { repository.update(1L, "Updated Title", "Desc", false) } returns updatedSample

        val result = taskService.update(1L, "Updated Title", "Desc", false)

        assertEquals("Updated Title", result.title)
        verify(exactly = 1) { repository.update(1L, "Updated Title", "Desc", false) }
    }

    // 7. ทดสอบอัปเดต Task แล้วไม่พบ ID (พ่น DataNotFoundException)
    @Test
    fun `update task should throw exception when task not found`() {
        every { repository.update(99L, "Title", "Desc", false) } returns null

        val exception = assertThrows<DataNotFoundException> {
            taskService.update(99L, "Title", "Desc", false)
        }
        assertEquals("data not found with id: 99", exception.message)
    }

    // 8. ทดสอบเปลี่ยนสถานะสำเร็จ (updateCompletionStatus)
    @Test
    fun `updateCompletionStatus should return updated task when successful`() {
        val updatedSample = sampleTask.copy(isCompleted = true)
        every { repository.updateCompletionStatus(1L, true) } returns updatedSample

        val result = taskService.updateCompletionStatus(1L, true)

        assertTrue(result.isCompleted)
        verify(exactly = 1) { repository.updateCompletionStatus(1L, true) }
    }

    // 9. ทดสอบเปลี่ยนสถานะแล้วไม่พบ ID (พ่น DataNotFoundException)
    @Test
    fun `updateCompletionStatus should throw exception when task not found`() {
        every { repository.updateCompletionStatus(99L, true) } returns null

        val exception = assertThrows<DataNotFoundException> {
            taskService.updateCompletionStatus(99L, true)
        }
        assertEquals("data not found with id: 99", exception.message)
    }

    // 10. ทดสอบลบ Task สำเร็จ (delete)
    @Test
    fun `delete task should execute successfully when task exists`() {
        every { repository.delete(1L) } returns true

        // สมมติ delete คืนค่า Unit หรือไม่มีการพ่น Exception เมื่อสำเร็จ
        assertDoesNotThrow {
            taskService.delete(1L)
        }
        verify(exactly = 1) { repository.delete(1L) }
    }

    // 11. ทดสอบลบ Task แล้วไม่พบ ID (พ่น DataNotFoundException)
    @Test
    fun `delete task should throw exception when task not found`() {
        every { repository.delete(99L) } returns false

        val exception = assertThrows<DataNotFoundException> {
            taskService.delete(99L)
        }
        assertEquals("data not found with id: 99", exception.message)
    }
}