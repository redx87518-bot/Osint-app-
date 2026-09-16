package com.jarvis.ai.whatsapp

import com.jarvis.ai.network.WhatsAppApi
import com.jarvis.ai.network.WhatsAppConnectResponse
import com.jarvis.ai.network.WhatsAppSendResponse
import com.jarvis.ai.network.WhatsAppStatusResponse
import com.jarvis.ai.supabase.SupabaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

sealed class WhatsAppConnectionState {
    object Idle : WhatsAppConnectionState()
    object CreatingDevice : WhatsAppConnectionState()
    object WaitingForPhone : WhatsAppConnectionState()
    object Pairing : WhatsAppConnectionState()
    data class Connected(val phone: String?, val deviceId: String?) : WhatsAppConnectionState()
    object Disconnected : WhatsAppConnectionState()
    data class Error(val message: String) : WhatsAppConnectionState()
}

class WhatsAppRepository(private val api: WhatsAppApi, private val scope: CoroutineScope) {
    private val _state = kotlinx.coroutines.flow.MutableStateFlow<WhatsAppConnectionState>(WhatsAppConnectionState.Disconnected)
    val state: Flow<WhatsAppConnectionState> = _state

    private var pollingJob: kotlinx.coroutines.Job? = null

    suspend fun createDevice(label: String = "Jarvis WhatsApp"): Result<WhatsAppConnectResponse> {
        return try {
            _state.value = WhatsAppConnectionState.CreatingDevice
            val token = SupabaseManager.getAccessToken() ?: return Result.failure(Exception("Not authenticated"))
            val response = api.connect(token, mapOf("action" to "create", "label" to label))
            _state.value = WhatsAppConnectionState.WaitingForPhone
            Result.success(response)
        } catch (e: Exception) {
            _state.value = WhatsAppConnectionState.Error(e.message ?: "Failed to create device")
            Result.failure(e)
        }
    }

    suspend fun pair(phone: String): Result<WhatsAppConnectResponse> {
        return try {
            _state.value = WhatsAppConnectionState.Pairing
            val token = SupabaseManager.getAccessToken() ?: return Result.failure(Exception("Not authenticated"))
            val response = api.connect(token, mapOf("action" to "pair", "phone" to phone))
            startPolling()
            Result.success(response)
        } catch (e: Exception) {
            _state.value = WhatsAppConnectionState.Error(e.message ?: "Failed to pair")
            Result.failure(e)
        }
    }

    suspend fun disconnect(): Result<Unit> {
        return try {
            pollingJob?.cancel()
            val token = SupabaseManager.getAccessToken() ?: return Result.failure(Exception("Not authenticated"))
            api.connect(token, mapOf("action" to "disconnect"))
            _state.value = WhatsAppConnectionState.Disconnected
            Result.success(Unit)
        } catch (e: Exception) {
            _state.value = WhatsAppConnectionState.Error(e.message ?: "Failed to disconnect")
            Result.failure(e)
        }
    }

    suspend fun sendMessage(phone: String, message: String): Result<WhatsAppSendResponse> {
        return try {
            val token = SupabaseManager.getAccessToken() ?: return Result.failure(Exception("Not authenticated"))
            val response = api.send(token, mapOf("phone" to phone, "message" to message))
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun startPolling() {
        pollingJob?.cancel()
        pollingJob = scope.launch(kotlinx.coroutines.Dispatchers.IO) {
            while (true) {
                delay(3000)
                try {
                    val token = SupabaseManager.getAccessToken() ?: break
                    val status = api.status(token)
                    when (status.status) {
                        "connected" -> {
                            _state.value = WhatsAppConnectionState.Connected(status.phone, status.device_id)
                            pollingJob?.cancel()
                        }
                        "disconnected" -> {
                            _state.value = WhatsAppConnectionState.Disconnected
                            pollingJob?.cancel()
                        }
                        "error" -> {
                            _state.value = WhatsAppConnectionState.Error(status.provider_status ?: "Unknown error")
                            pollingJob?.cancel()
                        }
                    }
                } catch (e: Exception) {
                    _state.value = WhatsAppConnectionState.Error(e.message ?: "Polling failed")
                    pollingJob?.cancel()
                }
            }
        }
    }

    fun cancelPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }
}
