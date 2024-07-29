package com.greenart7c3.phoenixd.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.greenart7c3.phoenixd.services.ReceiveViewModel
import com.greenart7c3.phoenixd.ui.theme.QrCodeDrawer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiveScreen(
    viewModel: ReceiveViewModel,
    navController: NavController,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        },
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (state.isRefreshing) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (showBottomSheet) {
                    ModalBottomSheet(
                        modifier = Modifier.heightIn(max = 700.dp),
                        sheetState = bottomSheetState,
                        onDismissRequest = {
                            scope.launch {
                                showBottomSheet = false
                                bottomSheetState.hide()
                            }
                        },
                    ) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            QrCodeDrawer(
                                contents = state.qrCodeBolt12,
                                modifier = Modifier.fillMaxWidth(0.85f),
                            )
                            Spacer(modifier = Modifier.size(8.dp))

                            ElevatedButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(state.qrCodeBolt12))
                                },
                            ) {
                                Text(text = "Copy")
                            }
                        }
                    }
                }

                QrCodeDrawer(
                    contents = state.qrCode,
                    modifier = Modifier.fillMaxWidth(0.85f),
                )
                Spacer(modifier = Modifier.size(8.dp))

                ElevatedButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(state.qrCode))
                    },
                ) {
                    Text(text = "Copy")
                }

                Spacer(modifier = Modifier.size(8.dp))
                ElevatedButton(
                    onClick = {
                        scope.launch {
                            showBottomSheet = true
                            bottomSheetState.show()
                        }
                    },
                ) {
                    Text(text = "Bolt12")
                }
            }
        }
    }
}
