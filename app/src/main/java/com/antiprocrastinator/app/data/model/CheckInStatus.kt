package com.antiprocrastinator.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class CheckInStatus {
    WORKING,
    TAKING_BREAK,
    DISTRACTED,
    GAVE_UP
}
