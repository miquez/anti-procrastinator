package com.antiprocrastinator.app.data.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startDateTime: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endDateTime: LocalDateTime,
    val lifecycleState: TaskLifecycleState = TaskLifecycleState.SCHEDULED,
    val completionStatus: CompletionStatus? = null,
    val actualStartTime: Instant? = null,
    val actualEndTime: Instant? = null,
    val checkIns: List<CheckIn> = emptyList()
) {
    fun overlaps(other: Task): Boolean {
        return this.startDateTime < other.endDateTime &&
               this.endDateTime > other.startDateTime
    }
}
