package com.greenart7c3.phoenixd.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.greenart7c3.phoenixd.services.CustomHttpClient
import com.greenart7c3.phoenixd.services.LocalPreferences
import com.greenart7c3.phoenixd.services.Settings
import io.ktor.http.URLProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    Scaffold { innerPadding ->
        var host by remember {
            mutableStateOf(TextFieldValue(""))
        }
        var port by remember {
            mutableStateOf(TextFieldValue(""))
        }
        var password by remember {
            mutableStateOf(TextFieldValue(""))
        }
        var loading by remember { mutableStateOf(true) }
        var useSSL by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        val clipboardManager = LocalClipboardManager.current
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            scope.launch(Dispatchers.IO) {
                LocalPreferences.getSavedSettings(context)
                if (Settings.host.isNotEmpty() && Settings.password.isNotEmpty()) {
                    scope.launch(Dispatchers.Main) {
                        navController.navigate("main") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
                loading = false
            }
        }

        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (loading) {
                CircularProgressIndicator()
            } else {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = host,
                    onValueChange = {
                        host = it
                    },
                    label = {
                        Text("Host")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                clipboardManager.getText()?.let {
                                    host = TextFieldValue(it)
                                }
                            },
                        ) {
                            Icon(
                                Icons.Default.ContentPaste,
                                contentDescription = "Paste from clipboard",
                            )
                        }
                    },
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = port,
                    onValueChange = {
                        port = it
                    },
                    label = {
                        Text("Port")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                clipboardManager.getText()?.let {
                                    port = TextFieldValue(it)
                                }
                            },
                        ) {
                            Icon(
                                Icons.Default.ContentPaste,
                                contentDescription = "Paste from clipboard",
                            )
                        }
                    },
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    label = {
                        Text("Http password")
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                clipboardManager.getText()?.let {
                                    password = TextFieldValue(it)
                                }
                            },
                        ) {
                            Icon(
                                Icons.Default.ContentPaste,
                                contentDescription = "Paste from clipboard",
                            )
                        }
                    },
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier =
                    Modifier
                        .padding(bottom = 16.dp, top = 8.dp)
                        .clickable {
                            useSSL = !useSSL
                        },
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "Use SSL",
                        fontSize = 18.sp,
                    )
                    Switch(
                        checked = useSSL,
                        onCheckedChange = {
                            useSSL = !useSSL
                        },
                    )
                }
                ElevatedButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            loading = true
                            Settings.host = host.text
                            Settings.port = port.text.toIntOrNull() ?: 0
                            Settings.password = password.text
                            Settings.protocol =
                                if (useSSL) {
                                    URLProtocol.HTTPS
                                } else {
                                    URLProtocol.HTTP
                                }
                            try {
                                val response = CustomHttpClient.get("getinfo")
                                if (response.status.value == 200) {
                                    LocalPreferences.saveSettings(context)
                                }
                                scope.launch(Dispatchers.Main) {
                                    if (response.status.value == 200) {
                                        Toast.makeText(
                                            context,
                                            "Successfully connected to Phoenixd server",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                        navController.navigate("main") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        loading = false
                                        Toast.makeText(
                                            context,
                                            "Failed to connect to Phoenixd server",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Log.d("error", e.toString())
                                loading = false
                                scope.launch(Dispatchers.Main) {
                                    Toast.makeText(
                                        context,
                                        "Failed to connect to Phoenixd server",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }
                            }
                        }
                    },
                ) {
                    Text(text = "Login")
                }
            }
        }
    }
}
