package com.greenart7c3.phoenixd.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.greenart7c3.phoenixd.services.SendViewModel
import com.greenart7c3.phoenixd.ui.ScannerView
import com.journeyapps.barcodescanner.DecoratedBarcodeView

@Composable
fun SendScreen(
    viewModel: SendViewModel,
    navController: NavController,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    var scanView by remember { mutableStateOf<DecoratedBarcodeView?>(null) }
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clear()
        }
    }

    Scaffold { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (state.value.showScanner) {
                ScannerView(
                    onScanViewBinding = {
                        scanView = it
                    },
                    onScannedText = {
                        viewModel.onScannedText(it)
                    },
                )
            } else {
                var textInput by remember(state.value.amount) { mutableStateOf(TextFieldValue(state.value.amount.toString())) }
                LaunchedEffect(Unit) {
                    viewModel.processInput()
                }
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    Arrangement.Center,
                    Alignment.CenterHorizontally,
                ) {
                    if (state.value.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = textInput,
                            onValueChange = {
                                textInput = it
                            },
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        ElevatedButton(
                            onClick = {
                                viewModel.send(context, navController)
                            },
                        ) {
                            Row {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                )
                                Spacer(modifier = Modifier.size(4.dp))
                                Text("Send")
                            }
                        }
                    }
                }
            }
        }
    }
}