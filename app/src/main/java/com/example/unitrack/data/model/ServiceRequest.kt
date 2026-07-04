package com.example.unitrack.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ServiceRequest(
    val id: Long = 0,
    @SerialName("user_id")
    val userId: String,
    @SerialName("category_id")
    val categoryId: Long,
    val location: String,
    val description: String,
    val status: String = "SUBMITTED",
    @SerialName("photo_url")
    val photoUrl: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("updated_at")
    val updatedAt: String? = null
)