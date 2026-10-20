package org.deesoft.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.deesoft.model.TaskResponse
import org.deesoft.repository.TaskRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class TaskServiceTest {

    // จำลอง (Mock) TaskRepository ขึ้นมา เพื่อไม่ต้องต่อฐานข้อมูลจริง
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

    @Test
    fun `getTaskById should return task when repository returns task`() {
        // Given: กำหนดพฤติกรรมจำลองให้ repository คืนค่า sampleTask เมื่อเรียกหาด้วย ID 1
        every { repository.findById(1L) } returns sampleTask

        // When: เรียกใช้งานผ่าน Service
        val result = taskService.getTaskById(1L)

        // Then: ตรวจสอบผลลัพธ์
        assertNotNull(result)
        assertEquals(1L, result?.id)
        assertEquals("Test Task", result?.title)
        verify(exactly = 1) { repository.findById(1L) }
    }

    @Test
    fun `create task should throw exception when title is blank`() {
        // When & Then: ถ้าส่ง Title เป็นค่าว่าง ต้องโยน IllegalArgumentException ออกมาทันทีโดยไม่แตะ Database
        val exception = assertThrows<IllegalArgumentException> {
            taskService.create("   ", "Description")
        }
        assertEquals("Task title cannot be blank", exception.message)
    }

    @Test
    fun `updateCompletionStatus should return updated task when successful`() {
        // Given
        val updatedSample = sampleTask.copy(isCompleted = true)
        every { repository.updateCompletionStatus(1L, true) } returns updatedSample

        // When
        val result = taskService.updateCompletionStatus(1L, true)

        // Then
        assertTrue(result.isCompleted)
        verify(exactly = 1) { repository.updateCompletionStatus(1L, true) }
    }

    @Test
    fun `updateCompletionStatus should throw exception when task not found`() {
        // Given: Repository คืนค่า null (หาไม่พบ)
        every { repository.updateCompletionStatus(99L, true) } returns null

        // When & Then
        val exception = assertThrows<IllegalArgumentException> {
            taskService.updateCompletionStatus(99L, true)
        }
        assertEquals("Task not found with id: 99", exception.message)
    }
}