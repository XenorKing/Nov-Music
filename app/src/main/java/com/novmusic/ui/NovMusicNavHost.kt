package com.novmusic.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.novmusic.ui.screens.ForgotPasswordScreen
import com.novmusic.ui.screens.HomeScreen
import com.novmusic.ui.screens.LoginScreen
import com.novmusic.ui.screens.RegisterScreen
import com.novmusic.ui.screens.SavedTracksScreen
import com.novmusic.ui.screens.SearchScreen
import com.novmusic.ui.screens.VkAuthWebViewScreen
import com.novmusic.ui.theme.BackgroundDark
import com.novmusic.ui.theme.PrimaryPurple
import com.novmusic.ui.theme.SurfaceDark
import com.novmusic.ui.viewmodel.AuthState
import com.novmusic.ui.viewmodel.AuthViewModel
import com.novmusic.ui.viewmodel.MusicViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val VK_AUTH = "vk_auth"
    const val HOME = "home"
    const val SEARCH = "search"
    const val SAVED = "saved"
}

@Composable
fun NovMusicNavHost(
    authState: AuthState,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val musicViewModel: MusicViewModel = hiltViewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainRoutes = listOf(Routes.HOME, Routes.SEARCH, Routes.SAVED)
    val showBottomBar = authState is AuthState.Authenticated && currentRoute in mainRoutes

    val startDestination = when (authState) {
        is AuthState.Loading -> Routes.LOGIN
        is AuthState.Unauthenticated -> Routes.LOGIN
        is AuthState.Authenticated -> Routes.HOME
    }

    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
            ) {
                NovMusicBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable(Routes.LOGIN) {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                        onNavigateToForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) },
                        onNavigateToVkAuth = { navController.navigate(Routes.VK_AUTH) },
                        onNavigateToHome = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Routes.REGISTER) {
                    RegisterScreen(
                        authViewModel = authViewModel,
                        onNavigateToLogin = { navController.navigateUp() },
                        onNavigateToHome = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Routes.FORGOT_PASSWORD) {
                    ForgotPasswordScreen(
                        authViewModel = authViewModel,
                        onNavigateBack = { navController.navigateUp() }
                    )
                }
                composable(Routes.VK_AUTH) {
                    VkAuthWebViewScreen(
                        authUrl = authViewModel.getVkAuthUrl(),
                        onTokenReceived = { token, userId ->
                            authViewModel.handleVkTokenReceived(token, userId)
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        },
                        onBack = { navController.navigateUp() }
                    )
                }
                composable(Routes.HOME) {
                    HomeScreen(
                        musicViewModel = musicViewModel,
                        authViewModel = authViewModel,
                        onNavigateToVkAuth = { navController.navigate(Routes.VK_AUTH) }
                    )
                }
                composable(Routes.SEARCH) {
                    SearchScreen(
                        musicViewModel = musicViewModel,
                        authViewModel = authViewModel,
                        onNavigateToVkAuth = { navController.navigate(Routes.VK_AUTH) }
                    )
                }
                composable(Routes.SAVED) {
                    SavedTracksScreen(musicViewModel = musicViewModel)
                }
            }
        }
    }
}

@Composable
fun NovMusicBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = SurfaceDark,
        contentColor = PrimaryPurple
    ) {
        NavigationBarItem(
            selected = currentRoute == Routes.HOME,
            onClick = { onNavigate(Routes.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == Routes.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Главная"
                )
            },
            label = { Text("Главная") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryPurple,
                selectedTextColor = PrimaryPurple,
                unselectedIconColor = Color(0xFF5A5A7A),
                unselectedTextColor = Color(0xFF5A5A7A),
                indicatorColor = Color(0xFF1E1E40)
            )
        )
        NavigationBarItem(
            selected = currentRoute == Routes.SEARCH,
            onClick = { onNavigate(Routes.SEARCH) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == Routes.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                    contentDescription = "Поиск"
                )
            },
            label = { Text("Поиск") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryPurple,
                selectedTextColor = PrimaryPurple,
                unselectedIconColor = Color(0xFF5A5A7A),
                unselectedTextColor = Color(0xFF5A5A7A),
                indicatorColor = Color(0xFF1E1E40)
            )
        )
        NavigationBarItem(
            selected = currentRoute == Routes.SAVED,
            onClick = { onNavigate(Routes.SAVED) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == Routes.SAVED) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = "Избранное"
                )
            },
            label = { Text("Избранное") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PrimaryPurple,
                selectedTextColor = PrimaryPurple,
                unselectedIconColor = Color(0xFF5A5A7A),
                unselectedTextColor = Color(0xFF5A5A7A),
                indicatorColor = Color(0xFF1E1E40)
            )
        )
    }
}
