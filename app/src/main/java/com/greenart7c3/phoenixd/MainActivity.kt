package com.greenart7c3.phoenixd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.greenart7c3.phoenixd.screens.LoginScreen
import com.greenart7c3.phoenixd.screens.MainScreen
import com.greenart7c3.phoenixd.services.PhoenixdViewModel
import com.greenart7c3.phoenixd.ui.theme.PhoenixdTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhoenixdTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(navController)
                    }

                    composable("main") {
                        val viewModel by viewModels<PhoenixdViewModel>()
                        MainScreen(viewModel)
                    }
                }
            }
        }
    }
}

