package com.jarvis.ai.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.jarvis.ai.supabase.SupabaseManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val emailState = remember { mutableStateOf("") }
    val email = emailState.value
    val passwordState = remember { mutableStateOf("") }
    val password = passwordState.value
    val isLoadingState = remember { mutableStateOf(false) }
    val isLoading = isLoadingState.value
    val errorState = remember { mutableStateOf<String?>(null) }
    val error = errorState.value
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (SupabaseManager.getCurrentUser() != null) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Jarvis AI", modifier = Modifier.padding(bottom = 24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        if (error != null) {
            Text(text = error, modifier = Modifier.padding(top = 8.dp))
        }

        Button(
            onClick = {
                isLoadingState.value = true
                errorState.value = null
                scope.launch {
                    try {
                        SupabaseManager.signIn(email, password)
                        onLoginSuccess()
                    } catch (e: Exception) {
                        errorState.value = e.localizedMessage ?: "Login failed"
                    } finally {
                        isLoadingState.value = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            enabled = !isLoading
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp)) else Text("Sign In")
        }
    }
}
