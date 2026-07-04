package com.example.unitrack.data.repository

import com.example.unitrack.data.model.ServiceRequest
import com.example.unitrack.data.model.ServiceRequestInsert
import com.example.unitrack.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import java.time.Instant

class RequestRepository {

    private val client = SupabaseClientProvider.client

    suspend fun createRequest(
        userId: String,
        categoryId: Long,
        location: String,
        description: String,
        photoUrl: String? = null
    ) {
        val request = ServiceRequestInsert(
            userId = userId,
            categoryId = categoryId,
            location = location,
            description = description,
            photoUrl = photoUrl
        )

        client
            .from("service_requests")
            .insert(request)
    }

    suspend fun getRequestsByUser(userId: String): List<ServiceRequest> {
        return client
            .from("service_requests")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<ServiceRequest>()
            .sortedByDescending { it.createdAt ?: "" }
    }

    suspend fun cancelRequest(requestId: Long) {
        client
            .from("service_requests")
            .update(
                {
                    set("status", "CANCELLED")
                    set("updated_at", Instant.now().toString())
                }
            ) {
                filter {
                    eq("id", requestId)
                }
            }
    }

    suspend fun getAllRequests(): List<ServiceRequest> {
        return client
            .from("service_requests")
            .select()
            .decodeList<ServiceRequest>()
            .sortedByDescending { it.createdAt ?: "" }
    }

    suspend fun updateRequestStatus(
        requestId: Long,
        newStatus: String
    ) {
        client
            .from("service_requests")
            .update(
                {
                    set("status", newStatus)
                    set("updated_at", java.time.Instant.now().toString())
                }
            ) {
                filter {
                    eq("id", requestId)
                }
            }
    }

    suspend fun deleteRequest(requestId: Long) {
        client
            .from("service_requests")
            .delete {
                filter {
                    eq("id", requestId)
                }
            }
    }
}