package com.example.unitrack.ui.components

import com.example.unitrack.data.model.RequestStatus
import com.example.unitrack.data.model.ServiceRequest

fun requestMatchesSearch(
    request: ServiceRequest,
    categoryName: String,
    query: String
): Boolean {
    val searchText = query.trim().lowercase()

    if (searchText.isBlank()) {
        return true
    }

    val statusLabel = RequestStatus
        .getLabelFromDbValue(request.status)
        .lowercase()

    val createdDate = request.createdAt
        ?.take(10)
        ?.lowercase()
        ?: ""

    return request.id.toString().contains(searchText) ||
            categoryName.lowercase().contains(searchText) ||
            request.location.lowercase().contains(searchText) ||
            request.description.lowercase().contains(searchText) ||
            request.status.lowercase().contains(searchText) ||
            statusLabel.contains(searchText) ||
            createdDate.contains(searchText)
}