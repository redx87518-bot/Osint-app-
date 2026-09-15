package com.jarvis.ai.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jarvis.ai.supabase.SupabaseManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var isSignUp by remember { mutableStateOf(false) }
    val emailState = remember { mutableStateOf("") }
    val email = emailState.value
    val passwordState = remember { mutableStateOf("") }
    val password = passwordState.value
    val confirmPasswordState = remember { mutableStateOf("") }
    val confirmPassword = confirmPasswordState.value
    val nameState = remember { mutableStateOf("") }
    val name = nameState.value
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Jarvis AI",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A237E),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = if (isSignUp) "Create your account" else "Welcome back",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        if (isSignUp) {
            OutlinedTextField(
                value = name,
                onValueChange = { nameState.value = it },
                label = { Text("Full Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = if (isSignUp) 12.dp else 24.dp),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        if (isSignUp) {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPasswordState.value = it },
                label = { Text("Confirm Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
        }

        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Button(
            onClick = {
                if (isSignUp && password != confirmPassword) {
                    errorState.value = "Passwords do not match"
                    return@Button
                }

                isLoadingState.value = true
                errorState.value = null
                scope.launch {
                    try {
                        if (isSignUp) {
                            SupabaseManager.signUp(email, password)
                        } else {
                            SupabaseManager.signIn(email, password)
                        }
                        onLoginSuccess()
                    } catch (e: Exception) {
                        errorState.value = e.localizedMessage ?: "Authentication failed"
                    } finally {
                        isLoadingState.value = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp), color = Color.White)
            }
            Text(if (isSignUp) "Sign Up" else "Sign In")
        }

        TextButton(onClick = { isSignUp = !isSignUp }) {
            Text(
                text = if (isSignUp) "Already have an account? Sign In" else "Don't have an account? Sign Up",
                color = Color(0xFF1A237E)
            )
        }
    }
}
