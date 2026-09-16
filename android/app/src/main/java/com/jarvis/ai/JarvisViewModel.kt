package com.jarvis.ai

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.ai.ui.MessageItem
import com.jarvis.ai.ui.OrbState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JarvisViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<MessageItem>>(emptyList())
    val messages: StateFlow<List<MessageItem>> = _messages.asStateFlow()

    private val _orbState = MutableStateFlow<OrbState>(OrbState.IDLE)
    val orbState: StateFlow<OrbState> = _orbState.asStateFlow()

    private val _amplitude = MutableStateFlow(0f)
    val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    var textInput by mutableStateOf("")

    fun onInputChange(text: String) {
        _inputText.value = text
        textInput = text
    }

    fun sendMessage() {
        val msg = _inputText.value.trim()
        if (msg.isBlank()) return
        _messages.value += MessageItem(text = msg, isUser = true)
        _inputText.value = ""
        viewModelScope.launch {
            _isLoading.value = true
            delay(500)
            _messages.value += MessageItem(text = "Echo: $msg", isUser = false)
            _isLoading.value = false
        }
    }

    fun startListening() {
        _orbState.value = OrbState.LISTENING
        viewModelScope.launch {
            delay(2000)
            _orbState.value = OrbState.IDLE
            _messages.value += MessageItem(text = "Heard you.", isUser = false)
        }
    }

    fun clearError() {
        _error.value = null
    }
}
