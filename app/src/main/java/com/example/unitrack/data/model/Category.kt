package com.example.unitrack.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val active: Boolean = true,
    @SerialName("created_at")
    val createdAt: String? = null
)