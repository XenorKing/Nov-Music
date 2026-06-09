package com.novmusic.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    val authState by authViewModel.authState.collectAsState()
    val authEvent by authViewModel.authEvent.collectAsState()
    val focusManager = LocalFocusManager.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
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
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryPurple, AccentCyan)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "novМузыка",
                color = OnSurfaceDark,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Войдите в свой аккаунт",
                color = OnSurfaceVariantDark,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF3A1A2A)
                    ),
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

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; errorMessage = null },
                placeholder = { Text("Email", color = OnSurfaceVariantDark) },
                leadingIcon = {
                    Icon(Icons.Default.Email, null, tint = OnSurfaceVariantDark, modifier = Modifier.size(20.dp))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = DividerColor,
                    focusedTextColor = OnSurfaceDark,
                    unfocusedTextColor = OnSurfaceDark,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    cursorColor = PrimaryPurple
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                placeholder = { Text("Пароль", color = OnSurfaceVariantDark) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, null, tint = OnSurfaceVariantDark, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = OnSurfaceVariantDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    authViewModel.signIn(email, password)
                }),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryPurple,
                    unfocusedBorderColor = DividerColor,
                    focusedTextColor = OnSurfaceDark,
                    unfocusedTextColor = OnSurfaceDark,
                    focusedContainerColor = SurfaceDark,
                    unfocusedContainerColor = SurfaceDark,
                    cursorColor = PrimaryPurple
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onNavigateToForgotPassword,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Забыли пароль?",
                    color = PrimaryPurple,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    authViewModel.signIn(email, password)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPurple,
                    disabledContainerColor = SurfaceVariantDark
                ),
                enabled = authEvent !is AuthEvent.Loading
            ) {
                if (authEvent is AuthEvent.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Войти", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
                Text(
                    text = "  или  ",
                    color = OnSurfaceVariantDark,
                    fontSize = 12.sp
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = { onNavigateToVkAuth() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF4A90D9)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF4A90D9))
            ) {
                Text(
                    text = "ВКонтакте",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4A90D9)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Нет аккаунта? ",
                    color = OnSurfaceVariantDark,
                    fontSize = 14.sp
                )
                TextButton(onClick = onNavigateToRegister, contentPadding = PaddingValues(0.dp)) {
                    Text(
                        text = "Зарегистрироваться",
                        color = PrimaryPurple,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Политика конфиденциальности",
                color = OnSurfaceVariantDark.copy(alpha = 0.5f),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
