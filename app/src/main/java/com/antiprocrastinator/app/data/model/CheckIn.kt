package com.antiprocrastinator.app.data.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class CheckIn(
    val timestamp: Instant,
    val status: CheckInStatus,
    val note: String? = null
)
