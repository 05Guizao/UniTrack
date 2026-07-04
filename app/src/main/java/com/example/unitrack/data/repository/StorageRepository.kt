package com.example.unitrack.data.repository

import com.example.unitrack.data.remote.SupabaseClientProvider
import io.github.jan.supabase.storage.storage

class StorageRepository {

    private val client = SupabaseClientProvider.client

    suspend fun uploadRequestPhoto(
        userId: String,
        photoBytes: ByteArray
    ): String {
        val filePath = "requests/$userId/${System.currentTimeMillis()}.jpg"

        val bucket = client.storage.from("request-photos")

        bucket.upload(filePath, photoBytes) {
            upsert = false
        }

        return bucket.publicUrl(filePath)
    }
}