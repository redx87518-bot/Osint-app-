package com.jarvis.ai.network

import com.jarvis.ai.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import com.jarvis.ai.supabase.SupabaseManager

interface JarvisApi {
    @GET("health")
    suspend fun health(): HealthResponse

    @POST("api/chat")
    @JvmSuppressWildcards
    suspend fun chat(
        @Header("Authorization") token: String,
        @Body body: ChatRequest
    ): okhttp3.ResponseBody

    @GET("api/session")
    suspend fun session(@Header("Authorization") token: String): SessionResponse

    @POST("api/session")
    suspend fun saveSession(@Header("Authorization") token: String, @Body body: Map<String, Any?>): SaveResponse

    @GET("api/memory")
    suspend fun memory(@Header("Authorization") token: String): MemoryResponse

    @POST("api/memory")
    suspend fun saveMemory(@Header("Authorization") token: String, @Body body: Map<String, Any?>): SaveMemoryResponse

    @DELETE("api/memory/{id}")
    suspend fun deleteMemory(@Header("Authorization") token: String, @Path("id") id: String): DeleteResponse

    @GET("api/whatsapp/status")
    suspend fun whatsappStatus(@Header("Authorization") token: String): WhatsAppStatusResponse

    @POST("api/whatsapp/pair")
    suspend fun whatsappPair(@Header("Authorization") token: String): WhatsAppPairResponse

    @POST("api/whatsapp/send")
    suspend fun whatsappSend(@Header("Authorization") token: String, @Body body: Map<String, String>): WhatsAppSendResponse
}

data class HealthResponse(val status: String, val timestamp: String)
data class ChatRequest(val message: String, val history: List<Map<String, String>> = emptyList())
data class SessionResponse(val session: Map<String, Any?>?)
data class SaveResponse(val ok: Boolean)
data class MemoryResponse(val memories: Map<String, Any?>)
data class SaveMemoryResponse(val memory: Map<String, Any?>)
data class DeleteResponse(val ok: Boolean)
data class WhatsAppStatusResponse(val connected: Boolean, val phone: String? = null)
data class WhatsAppPairResponse(val message: String)
data class WhatsAppSendResponse(val messageId: String? = null, val status: String)

object ApiClient {
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080/"

    fun create(baseUrl: String = BuildConfig.BACKEND_URL.ifEmpty { DEFAULT_BASE_URL }): JarvisApi {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

        val authInterceptor = Interceptor { chain ->
            val token = SupabaseManager.getAccessToken() ?: ""
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JarvisApi::class.java)
    }
}
