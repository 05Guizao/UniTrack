package com.example.unitrack.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    @SerialName("profile_type")
    val profileType: String,
    @SerialName("created_at")
    val createdAt: String? = null
)