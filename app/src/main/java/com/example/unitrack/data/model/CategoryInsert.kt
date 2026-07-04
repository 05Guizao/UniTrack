package com.example.unitrack.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryInsert(
    val name: String,
    val description: String? = null,
    val active: Boolean = true
)