package com.greenart7c3.phoenixd.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ContentPaste
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.greenart7c3.phoenixd.services.SendViewModel
import com.greenart7c3.phoenixd.ui.ScannerView
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import fr.acinq.lightning.utils.sat

@Composable
fun SendScreen(
    viewModel: SendViewModel,
    navController: NavController,
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    var scanView by remember { mutableStateOf<DecoratedBarcodeView?>(null) }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    DisposableEffect(Unit) {
        onDispose {
            scanView?.pause()
            viewModel.clear()
            viewModel.amount = 0
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
                        if (!viewModel.validateInput(it)) {
                            Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                            return@ScannerView
                        }
                        viewModel.onScannedText(it)
                    },
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .align(Alignment.BottomCenter)
                        .padding(24.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ElevatedButton(
                            onClick = {
                                clipboardManager.getText()?.let {
                                    if (!viewModel.validateInput(it.text)) {
                                        Toast.makeText(context, "Invalid input", Toast.LENGTH_SHORT).show()
                                        return@ElevatedButton
                                    }
                                    viewModel.onScannedText(it.text)
                                }
                            },
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Icon(
                                    Icons.Default.ContentPaste,
                                    contentDescription = "Paste from clipboard",
                                )
                                Text("Paste from clipboard")
                            }
                        }
                    }
                }
            } else {
                var textInput by remember { mutableStateOf(TextFieldValue(viewModel.amount.toString())) }
                LaunchedEffect(Unit) {
                    textInput = TextFieldValue(viewModel.processInput().toString())
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
                        Text(
                            text = state.value.sanitizedInput,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = textInput,
                            onValueChange = {
                                textInput = it
                                viewModel.changeAmount(it.text)
                            },
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Row {
                            Text(text = "Fee ")
                            Text(text = state.value.fee.sat.sat.toString())
                            Text(text = " sats")
                        }
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
