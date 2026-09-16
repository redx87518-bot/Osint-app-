package com.jarvis.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jarvis.ai.supabase.SupabaseManager
import com.jarvis.ai.ui.JarvisScreen
import com.jarvis.ai.ui.LoginScreen
import com.jarvis.ai.ui.theme.JarvisTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupabaseManager.init(applicationContext)

        setContent {
            JarvisTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    var isLoggedIn by remember { mutableStateOf(SupabaseManager.getCurrentUser() != null) }

                    LaunchedEffect(Unit) {
                        SupabaseManager.isLoggedIn.collect { loggedIn ->
                            isLoggedIn = loggedIn
                        }
                    }

                    if (isLoggedIn) {
                        val viewModel: JarvisViewModel = viewModel()
                        JarvisScreen(viewModel = viewModel)
                    } else {
                        LoginScreen(onLoginSuccess = { isLoggedIn = true })
                    }
                }
            }
        }
    }
}
