package com.example.unitrack.data.repository

import com.example.unitrack.data.model.Category
import com.example.unitrack.data.model.CategoryInsert
import com.example.unitrack.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from

class CategoryRepository {

    private val client = SupabaseClientProvider.client

    suspend fun getActiveCategories(): List<Category> {
        return client
            .from("categories")
            .select {
                filter {
                    eq("active", true)
                }
            }
            .decodeList<Category>()
            .sortedBy { it.name }
    }

    suspend fun getAllCategories(): List<Category> {
        return client
            .from("categories")
            .select {
                filter {
                    eq("active", true)
                }
            }
            .decodeList<Category>()
            .sortedBy { it.name }
    }

    suspend fun createCategory(
        name: String,
        description: String?
    ) {
        val category = CategoryInsert(
            name = name.trim(),
            description = description?.trim()?.takeIf { it.isNotBlank() },
            active = true
        )

        client
            .from("categories")
            .insert(category)
    }

    suspend fun updateCategory(
        categoryId: Long,
        name: String,
        description: String?
    ) {
        client
            .from("categories")
            .update(
                {
                    set("name", name.trim())
                    set("description", description?.trim()?.takeIf { it.isNotBlank() })
                }
            ) {
                filter {
                    eq("id", categoryId)
                }
            }
    }

    suspend fun removeCategory(categoryId: Long) {
        client
            .from("categories")
            .update(
                {
                    set("active", false)
                }
            ) {
                filter {
                    eq("id", categoryId)
                }
            }
    }
}