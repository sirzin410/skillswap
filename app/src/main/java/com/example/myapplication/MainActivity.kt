package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.screens.ChatScreen
import com.example.myapplication.ui.screens.ExploreScreen
import com.example.myapplication.ui.screens.LoginScreen
import com.example.myapplication.ui.screens.ProfileScreen
import com.example.myapplication.ui.screens.SwapRequestsScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.viewmodel.SkillSwapViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SkillSwapAppContent()
            }
        }
    }
}

@Composable
fun SkillSwapAppContent() {
    val viewModel: SkillSwapViewModel = hiltViewModel()
    val currentUserId by viewModel.currentUserId.collectAsState()

    if (currentUserId == null) {
        LoginScreen(viewModel = viewModel)
    } else {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            bottomBar = {
                if (currentRoute != null && !currentRoute.startsWith("chat/")) {
                    NavigationBar {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Home, contentDescription = "Explore") },
                            label = { Text("Explore") },
                            selected = currentRoute == "explore",
                            onClick = { navController.navigate("explore") { launchSingleTop = true } }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.List, contentDescription = "Requests") },
                            label = { Text("Requests") },
                            selected = currentRoute == "requests",
                            onClick = { navController.navigate("requests") { launchSingleTop = true } }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                            label = { Text("Profile") },
                            selected = currentRoute == "profile",
                            onClick = { navController.navigate("profile") { launchSingleTop = true } }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "explore",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("explore") { ExploreScreen(viewModel) }
                composable("requests") {
                    SwapRequestsScreen(
                        viewModel = viewModel,
                        onNavigateToChat = { otherUserId, otherUserName ->
                            val encodedName = URLEncoder.encode(otherUserName, StandardCharsets.UTF_8.toString())
                            navController.navigate("chat/$otherUserId/$encodedName")
                        }
                    )
                }
                composable("profile") { ProfileScreen(viewModel) }
                composable(
                    route = "chat/{otherUserId}/{otherUserName}",
                    arguments = listOf(
                        navArgument("otherUserId") { type = NavType.LongType },
                        navArgument("otherUserName") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val otherUserId = backStackEntry.arguments?.getLong("otherUserId") ?: 0L
                    val encodedName = backStackEntry.arguments?.getString("otherUserName") ?: ""
                    val otherUserName = URLDecoder.decode(encodedName, StandardCharsets.UTF_8.toString())
                    ChatScreen(
                        viewModel = viewModel,
                        otherUserId = otherUserId,
                        otherUserName = otherUserName,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
