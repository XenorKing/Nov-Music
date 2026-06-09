package com.novmusic

  import android.os.Bundle
  import androidx.activity.ComponentActivity
  import androidx.activity.compose.setContent
  import androidx.activity.enableEdgeToEdge
  import com.novmusic.ui.NovMusicNavHost
  import com.novmusic.ui.theme.NovMusicTheme
  import dagger.hilt.android.AndroidEntryPoint

  @AndroidEntryPoint
  class MainActivity : ComponentActivity() {

      override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          enableEdgeToEdge()
          setContent {
              NovMusicTheme {
                  NovMusicNavHost()
              }
          }
      }
  }
  