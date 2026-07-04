package com.example.unitrack.data.repository

import com.example.unitrack.data.model.UserProfile
import com.example.unitrack.data.remote.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

class AuthRepository {

    private val client = SupabaseClientProvider.client

    suspend fun register(
        name: String,
        email: String,
        password: String,
        profileType: String
    ): UserProfile {
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }

        val authUser = client.auth.currentUserOrNull()
            ?: throw Exception("Não foi possível obter o utilizador criado.")

        val profile = UserProfile(
            id = authUser.id,
            name = name,
            email = email,
            profileType = profileType
        )

        client.from("profiles").insert(profile)

        return profile
    }

    suspend fun login(
        email: String,
        password: String
    ): UserProfile {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        val authUser = client.auth.currentUserOrNull()
            ?: throw Exception("Não foi possível obter o utilizador autenticado.")

        return client
            .from("profiles")
            .select {
                filter {
                    eq("id", authUser.id)
                }
            }
            .decodeSingle<UserProfile>()
    }

    suspend fun logout() {
        client.auth.signOut()
    }
}