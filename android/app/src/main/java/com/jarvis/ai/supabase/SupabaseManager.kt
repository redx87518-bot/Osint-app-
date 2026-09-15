package com.jarvis.ai.supabase

import android.content.Context
import android.util.Log
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SupabaseManager {
    private const val TAG = "SupabaseManager"
    private const val SUPABASE_URL = "https://jklnlcrzpumcgezwkucb.supabase.co"
    private const val SUPABASE_ANON_KEY = "sb_publishable_8ES2xILHwJ2o9ejn6k26pw_L8EqK0ND"

    private var initialized = false
    private var supabase: io.github.jan.supabase.SupabaseClient? = null

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun init(context: Context) {
        if (initialized) return

        try {
            supabase = createSupabaseClient(SUPABASE_URL, SUPABASE_ANON_KEY) {
                install(Auth)
                install(Storage)
            }
            initialized = true
            Log.d(TAG, "Supabase initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Supabase init failed", e)
        }
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            supabase?.auth?.signInWith(Email) {
                this.email = email
                this.password = password
            }
            _isLoggedIn.value = true
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoggedIn.value = false
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            supabase?.auth?.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            _isLoggedIn.value = true
            Result.success(Unit)
        } catch (e: Exception) {
            _isLoggedIn.value = false
            Result.failure(e)
        }
    }

    fun signOut() {
        try {
            supabase?.auth?.signOut()
            _isLoggedIn.value = false
        } catch (e: Exception) {
            Log.e(TAG, "Sign out failed", e)
        }
    }

    fun getCurrentUser() = supabase?.auth?.currentUserOrNull()

    fun getAccessToken(): String? = supabase?.auth?.currentSessionOrNull()?.accessToken

    suspend fun uploadAudio(file: java.io.File, path: String): Result<String> {
        return try {
            val storage = supabase?.storage?.get("audio")
            storage?.upload(path, file)
            val url = "$SUPABASE_URL/storage/v1/object/public/audio/$path"
            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
