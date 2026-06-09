package com.novmusic.ui.screens

import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.novmusic.ui.theme.*
import com.novmusic.ui.viewmodel.AuthEvent
import com.novmusic.ui.viewmodel.AuthState
import com.novmusic.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToVkAuth: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    val authEvent by authViewModel.authEvent.collectAsState()

    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) onNavigateToHome()
    }

    LaunchedEffect(authEvent) {
        when (val event = authEvent) {
            is AuthEvent.Error -> {
                errorMessage = event.message
                authViewModel.clearEvent()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0D1A),
                        Color(0xFF151530),
                        Color(0xFF0D0D1A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        Brush.linearGradient(colors = listOf(PrimaryPurple, AccentCyan)),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "novМузыка",
                color = OnSurfaceDark,
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Миллионы треков ВКонтакте\nпрямо у тебя в кармане",
                color = OnSurfaceVariantDark,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            AnimatedVisibility(visible = errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3A1A2A)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = Color(0xFFFF6B6B),
                        modifier = Modifier.padding(14.dp),
                        fontSize = 13.sp
                    )
                }
            }

            Button(
                onClick = {
                    // Открываем браузер телефона (Chrome Custom Tabs)
                    val url = authViewModel.getVkAuthUrl()
                    CustomTabsIntent.Builder()
                        .setShowTitle(false)
                        .build()
                        .launchUrl(context, Uri.parse(url))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0077FF),
                    contentColor = Color.White
                ),
                enabled = authEvent !is AuthEvent.Loading
            ) {
                if (authEvent is AuthEvent.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ВК",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Войти через ВКонтакте",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Нажимая «Войти», ты соглашаешься с\nПолитикой конфиденциальности",
                color = OnSurfaceVariantDark.copy(alpha = 0.5f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}
