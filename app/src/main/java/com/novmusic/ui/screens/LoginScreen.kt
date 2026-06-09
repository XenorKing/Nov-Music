package com.novmusic.ui.screens

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
  import androidx.compose.ui.text.font.FontWeight
  import androidx.compose.ui.text.style.TextAlign
  import androidx.compose.ui.unit.dp
  import androidx.compose.ui.unit.sp
  import com.novmusic.ui.theme.*

  @Composable
  fun LoginScreen(
      onNavigateToHome: () -> Unit
  ) {
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
                      .size(120.dp)
                      .background(
                          Brush.linearGradient(colors = listOf(PrimaryPurple, AccentCyan)),
                          shape = RoundedCornerShape(30.dp)
                      ),
                  contentAlignment = Alignment.Center
              ) {
                  Icon(
                      imageVector = Icons.Default.MusicNote,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(64.dp)
                  )
              }

              Spacer(modifier = Modifier.height(32.dp))

              Text(
                  text = "novМузыка",
                  color = OnSurfaceDark,
                  fontSize = 34.sp,
                  fontWeight = FontWeight.ExtraBold,
                  textAlign = TextAlign.Center
              )

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                  text = "Миллионы треков —\nбесплатно и без регистрации",
                  color = OnSurfaceVariantDark,
                  fontSize = 15.sp,
                  textAlign = TextAlign.Center,
                  lineHeight = 22.sp
              )

              Spacer(modifier = Modifier.height(52.dp))

              Button(
                  onClick = onNavigateToHome,
                  modifier = Modifier
                      .fillMaxWidth()
                      .height(56.dp),
                  shape = RoundedCornerShape(16.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                  contentPadding = PaddingValues(0.dp)
              ) {
                  Box(
                      modifier = Modifier
                          .fillMaxSize()
                          .background(
                              Brush.linearGradient(colors = listOf(PrimaryPurple, AccentCyan)),
                              shape = RoundedCornerShape(16.dp)
                          ),
                      contentAlignment = Alignment.Center
                  ) {
                      Text(
                          text = "Начать слушать",
                          color = Color.White,
                          fontSize = 17.sp,
                          fontWeight = FontWeight.SemiBold
                      )
                  }
              }

              Spacer(modifier = Modifier.height(16.dp))

              Text(
                  text = "30-секундные превью через iTunes",
                  color = OnSurfaceVariantDark.copy(alpha = 0.5f),
                  fontSize = 11.sp,
                  textAlign = TextAlign.Center
              )
          }
      }
  }
  