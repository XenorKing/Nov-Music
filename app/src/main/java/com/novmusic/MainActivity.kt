package com.novmusic

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.novmusic.ui.NovMusicNavHost
import com.novmusic.ui.theme.NovMusicTheme
import com.novmusic.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NovMusicTheme {
                val authViewModel: AuthViewModel = hiltViewModel()
                val authState by authViewModel.authState.collectAsState()
                NovMusicNavHost(authState = authState, authViewModel = authViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val data = intent.data
        if (data != null && data.scheme == "novmusic" && data.host == "vk-callback") {
            val token = data.getQueryParameter("access_token")
            val userId = data.getQueryParameter("user_id")
            if (token != null && userId != null) {
                // Передаём токен в ViewModel через broadcast или SharedFlow
                VkCallbackHolder.onVkCallback(token, userId)
            }
        }
    }
}
