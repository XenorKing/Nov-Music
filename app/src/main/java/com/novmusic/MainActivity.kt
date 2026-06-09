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
        // Обрабатываем deep link если запуск через него
        handleVkDeepLink(intent)
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
        handleVkDeepLink(intent)
    }

    private fun handleVkDeepLink(intent: Intent) {
        val data = intent.data ?: return
        if (data.scheme != "novmusic" || data.host != "vk-callback") return

        // VK возвращает токен в ФРАГМЕНТЕ (#), не в query-параметрах
        // Пример: novmusic://vk-callback#access_token=TOKEN&user_id=123&...
        val fragment = data.fragment ?: ""
        if (fragment.isNotBlank()) {
            val params = parseFragment(fragment)
            val token = params["access_token"]
            val userId = params["user_id"]
            if (!token.isNullOrBlank() && !userId.isNullOrBlank()) {
                VkCallbackHolder.onVkCallback(token, userId)
            }
        } else {
            // Fallback: иногда токен в query params
            val token = data.getQueryParameter("access_token")
            val userId = data.getQueryParameter("user_id")
            if (!token.isNullOrBlank() && !userId.isNullOrBlank()) {
                VkCallbackHolder.onVkCallback(token, userId)
            }
        }
    }

    private fun parseFragment(fragment: String): Map<String, String> {
        return fragment.split("&").mapNotNull { pair ->
            val idx = pair.indexOf('=')
            if (idx > 0) pair.substring(0, idx) to pair.substring(idx + 1) else null
        }.toMap()
    }
}
