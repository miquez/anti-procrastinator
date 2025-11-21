package com.antiprocrastinator.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class TaskLifecycleState {
    SCHEDULED,
    IN_PROGRESS,
    ENDED
}
