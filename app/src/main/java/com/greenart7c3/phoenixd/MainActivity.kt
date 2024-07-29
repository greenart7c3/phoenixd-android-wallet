package com.greenart7c3.phoenixd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.greenart7c3.phoenixd.screens.LoginScreen
import com.greenart7c3.phoenixd.screens.MainScreen
import com.greenart7c3.phoenixd.services.LocalPreferences
import com.greenart7c3.phoenixd.services.PhoenixdViewModel
import com.greenart7c3.phoenixd.services.Settings
import com.greenart7c3.phoenixd.ui.theme.PhoenixdTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhoenixdTheme {
                val navController = rememberNavController()
                var isLoading by remember {
                    mutableStateOf(true)
                }
                LaunchedEffect(Unit) {
                    launch(Dispatchers.IO) {
                        LocalPreferences.getSavedSettings(this@MainActivity)
                        isLoading = false
                    }
                }

                Scaffold { innerPadding ->
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    ) {
                        if (isLoading) {
                            Box(
                                Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        } else {
                            NavHost(
                                navController = navController,
                                startDestination = if (Settings.host.isNotEmpty() && Settings.password.isNotEmpty()) "main" else "login",
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
        }
    }
}
