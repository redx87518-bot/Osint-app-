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
}

interface WhatsAppApi {
    @POST("whatsapp-connect")
    suspend fun connect(@Header("Authorization") token: String, @Body body: Map<String, Any?>): WhatsAppConnectResponse

    @GET("whatsapp-status")
    suspend fun status(@Header("Authorization") token: String): WhatsAppStatusResponse

    @POST("whatsapp-send")
    suspend fun send(@Header("Authorization") token: String, @Body body: Map<String, String>): WhatsAppSendResponse
}

data class HealthResponse(val status: String, val timestamp: String)
data class ChatRequest(val message: String, val history: List<Map<String, String>> = emptyList())
data class SessionResponse(val session: Map<String, Any?>?)
data class SaveResponse(val ok: Boolean)
data class MemoryResponse(val memories: Map<String, Any?>)
data class SaveMemoryResponse(val memory: Map<String, Any?>)
data class DeleteResponse(val ok: Boolean)

data class WhatsAppConnectResponse(
    val status: String? = null,
    val message: String? = null,
    val pairing_code: String? = null,
    val expires_in: Int? = null,
    val connection: Map<String, Any?>? = null
)

data class WhatsAppStatusResponse(
    val status: String? = null,
    val connected: Boolean = false,
    val phone: String? = null,
    val device_id: String? = null,
    val provider_status: String? = null,
    val last_connected: String? = null,
    val last_seen: String? = null
)

data class WhatsAppSendResponse(
    val messageId: String? = null,
    val status: String
)

object ApiClient {
    private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080/"
    private const val SUPABASE_URL = "https://jklnlcrzpumcgezwkucb.supabase.co/functions/v1/"

    private val authInterceptor = Interceptor { chain ->
        val token = SupabaseManager.getAccessToken() ?: ""
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .addHeader("apikey", "sb_publishable_8ES2xILHwJ2o9ejn6k26pw_L8EqK0ND")
            .build()
        chain.proceed(request)
    }

    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .build()

    fun createJarvisApi(baseUrl: String = BuildConfig.BACKEND_URL.ifEmpty { DEFAULT_BASE_URL }): JarvisApi {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(JarvisApi::class.java)
    }

    fun createWhatsAppApi(): WhatsAppApi {
        return Retrofit.Builder()
            .baseUrl(SUPABASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WhatsAppApi::class.java)
    }
}

