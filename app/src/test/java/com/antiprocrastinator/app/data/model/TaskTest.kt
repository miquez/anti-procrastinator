package com.antiprocrastinator.app.data.model

import kotlinx.datetime.LocalDateTime
import org.junit.Assert.*
import org.junit.Test

class TaskTest {

    @Test
    fun `overlaps returns true for same-day overlapping tasks`() {
        val task1 = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:00:00")
        )
        val task2 = Task(
            title = "Task 2",
            startDateTime = LocalDateTime.parse("2025-11-22T10:30:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:30:00")
        )
        assertTrue(task1.overlaps(task2))
        assertTrue(task2.overlaps(task1))
    }

    @Test
    fun `overlaps returns true for cross-day overlapping tasks`() {
        val task1 = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T23:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-23T01:00:00")
        )
        val task2 = Task(
            title = "Task 2",
            startDateTime = LocalDateTime.parse("2025-11-23T00:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-23T02:00:00")
        )
        assertTrue(task1.overlaps(task2))
        assertTrue(task2.overlaps(task1))
    }

    @Test
    fun `overlaps returns false for adjacent tasks`() {
        val task1 = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:00:00")
        )
        val task2 = Task(
            title = "Task 2",
            startDateTime = LocalDateTime.parse("2025-11-22T11:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T12:00:00")
        )
        assertFalse(task1.overlaps(task2))
        assertFalse(task2.overlaps(task1))
    }

    @Test
    fun `overlaps returns false for non-overlapping tasks`() {
        val task1 = Task(
            title = "Task 1",
            startDateTime = LocalDateTime.parse("2025-11-22T10:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T11:00:00")
        )
        val task2 = Task(
            title = "Task 2",
            startDateTime = LocalDateTime.parse("2025-11-22T14:00:00"),
            endDateTime = LocalDateTime.parse("2025-11-22T15:00:00")
        )
        assertFalse(task1.overlaps(task2))
        assertFalse(task2.overlaps(task1))
    }
}
