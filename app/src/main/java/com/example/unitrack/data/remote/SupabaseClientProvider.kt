package com.example.unitrack.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {

    val client = createSupabaseClient(
        supabaseUrl = "https://gqqcwmwkkpvnflksflhu.supabase.co",
        supabaseKey = "sb_publishable_IGmdUjod0fb7hMXlv8MN5A_QWOSnamC"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}