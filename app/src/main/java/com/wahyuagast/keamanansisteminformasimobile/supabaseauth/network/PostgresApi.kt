package com.wahyuagast.keamanansisteminformasimobile.supabaseauth.network

import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.DocumentDto
import com.wahyuagast.keamanansisteminformasimobile.supabaseauth.models.ProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PostgrestApi {
    // RPC get_my_profile
    @POST("rpc/get_my_profile")
    suspend fun getMyProfile(@Body body: Map<String, Any> = emptyMap()): List<ProfileDto>

    // get all profiles (admin will use this)
    @GET("profiles")
    suspend fun getAllProfiles(): List<ProfileDto>

    // get documents (optionally filter by owner using query param like owner_id=eq.<uuid>)
    // Retrofit will append query param, we expect caller to pass "eq.<uuid>"
    @GET("documents")
    suspend fun getDocuments(@Query("owner_id") ownerFilter: String? = null): List<DocumentDto>
}