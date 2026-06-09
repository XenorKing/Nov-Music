package com.novmusic.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.novmusic.ui.theme.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VkAuthWebViewScreen(
    authUrl: String,
    onTokenReceived: (token: String, userId: String) -> Unit,
    onBack: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    val redirectBase = "https://oauth.vk.com/blank.html"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, null, tint = OnSurfaceDark)
                }
                Text(
                    text = "Войти через ВКонтакте",
                    color = OnSurfaceDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, null, tint = OnSurfaceVariantDark)
                }
            }

            HorizontalDivider(color = DividerColor)

            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.loadsImagesAutomatically = true
                            settings.userAgentString =
                                "Mozilla/5.0 (Linux; Android 11; Mobile) AppleWebKit/537.36 Chrome/96.0 Mobile Safari/537.36"

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    isLoading = true

                                    if (url != null && url.startsWith(redirectBase)) {
                                        isLoading = false
                                        val uri = android.net.Uri.parse(url)
                                        val fragment = uri.fragment ?: ""
                                        val params = parseFragment(fragment)
                                        val token = params["access_token"]
                                        val userId = params["user_id"]
                                        if (!token.isNullOrBlank() && !userId.isNullOrBlank()) {
                                            onTokenReceived(token, userId)
                                        } else {
                                            val error = params["error"]
                                            if (error != null) onBack()
                                        }
                                    }
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    if (url.startsWith(redirectBase)) {
                                        val fragment = request.url.fragment ?: ""
                                        val params = parseFragment(fragment)
                                        val token = params["access_token"]
                                        val userId = params["user_id"]
                                        if (!token.isNullOrBlank() && !userId.isNullOrBlank()) {
                                            onTokenReceived(token, userId)
                                        } else {
                                            onBack()
                                        }
                                        return true
                                    }
                                    return false
                                }
                            }
                            loadUrl(authUrl)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(BackgroundDark),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = PrimaryPurple,
                            modifier = Modifier.size(40.dp),
                            strokeWidth = 3.dp
                        )
                    }
                }
            }
        }
    }
}

private fun parseFragment(fragment: String): Map<String, String> {
    if (fragment.isBlank()) return emptyMap()
    return fragment.split("&").mapNotNull { pair ->
        val idx = pair.indexOf('=')
        if (idx > 0) pair.substring(0, idx) to pair.substring(idx + 1) else null
    }.toMap()
}
