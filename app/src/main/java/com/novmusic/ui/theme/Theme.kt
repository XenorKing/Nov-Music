package com.novmusic.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val PrimaryPurple = Color(0xFF7B5FFF)
val PrimaryPurpleVariant = Color(0xFF5B3FDF)
val AccentCyan = Color(0xFF22E4FF)
val AccentCyanDim = Color(0xFF0BBEDD)
val SecondaryPink = Color(0xFFFF4F9B)
val BackgroundDark = Color(0xFF0D0D1A)
val SurfaceDark = Color(0xFF13132B)
val SurfaceVariantDark = Color(0xFF1E1E3F)
val SurfaceCard = Color(0xFF191935)
val OnSurfaceDark = Color(0xFFF5F2FF)
val OnSurfaceVariantDark = Color(0xFF8A8AB0)
val DividerColor = Color(0xFF2A2A50)

val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = PrimaryPurpleVariant,
    onPrimaryContainer = Color.White,
    secondary = AccentCyan,
    onSecondary = BackgroundDark,
    tertiary = SecondaryPink,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = DividerColor,
    error = Color(0xFFFF5555)
)

val LightColorScheme = lightColorScheme(
    primary = PrimaryPurpleVariant,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE7FF),
    onPrimaryContainer = PrimaryPurpleVariant,
    secondary = AccentCyanDim,
    onSecondary = Color.White,
    background = Color(0xFFF8F5FF),
    onBackground = Color(0xFF1A1A2E),
    surface = Color.White,
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFFF0EEFF),
    onSurfaceVariant = Color(0xFF4A4A6A),
    error = Color(0xFFDC2626)
)

@Composable
fun NovMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
