package com.example.unitrack.data.repository

import com.example.unitrack.data.model.UserProfile
import com.example.unitrack.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class ProfileRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getProfile(userId: String): UserProfile {
        return client
            .from("profiles")
            .select {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingle<UserProfile>()
    }

    suspend fun updateName(
        userId: String,
        name: String
    ) {
        client
            .from("profiles")
            .update(
                {
                    set("name", name.trim())
                }
            ) {
                filter {
                    eq("id", userId)
                }
            }
    }

    suspend fun updatePassword(newPassword: String) {
        client.auth.updateUser {
            password = newPassword
        }
    }
}