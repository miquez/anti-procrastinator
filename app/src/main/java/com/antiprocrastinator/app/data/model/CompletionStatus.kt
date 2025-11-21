package com.antiprocrastinator.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class CompletionStatus {
    DONE,
    GAVE_UP
}
