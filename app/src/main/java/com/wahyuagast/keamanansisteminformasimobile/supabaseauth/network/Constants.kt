package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network

object Constants {
    const val SUPABASE_URL = "https://eqjtzvkoaannizfsulja.supabase.co"
    const val SUPABASE_ANON_KEY =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVxanR6dmtvYWFubml6ZnN1bGphIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI0MjgyMTgsImV4cCI6MjA3ODAwNDIxOH0.L81cVAExeOC4Lf8aEr-opk80ToDRjrnCc5ACT7Bm27w"

    const val AUTH_BASE = "$SUPABASE_URL/auth/v1/"
    const val REST_BASE = "$SUPABASE_URL/rest/v1/"
    const val POSTGREST_BASE = "$SUPABASE_URL/rest/v1/"
    const val STORAGE_BASE = "$SUPABASE_URL/storage/v1/object/"

    // opsional (tidak wajib)
    const val PROJECT_HOST = "eqjtzvkoaannizfsulja.supabase.co"
}