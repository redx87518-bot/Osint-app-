package com.jarvis.ai.supabase

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SupabaseManager {
    private const val TAG = "SupabaseManager"

    private var initialized = false

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    fun init(context: Context) {
        if (initialized) return
        initialized = true
        Log.d(TAG, "Supabase stub initialized")
    }

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return Result.success(Unit)
    }

    suspend fun signUp(email: String, password: String): Result<Unit> {
        return Result.success(Unit)
    }

    fun signOut() {
        _isLoggedIn.value = false
    }

    fun getCurrentUser() = null

    fun getAccessToken(): String? = null

    suspend fun uploadAudio(file: java.io.File, path: String): Result<String> {
        return Result.failure(Exception("Not implemented"))
    }
}
