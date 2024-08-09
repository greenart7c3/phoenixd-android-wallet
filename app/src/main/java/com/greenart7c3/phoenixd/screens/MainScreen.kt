package com.greenart7c3.phoenixd.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.greenart7c3.phoenixd.services.PhoenixdViewModel
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlinx.coroutines.launch

fun Long.formatSat(): String {
    return "${DecimalFormat("#,###").format(this)} sat"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: PhoenixdViewModel,
    navController: NavController,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            var showChannels by remember { mutableStateOf(false) }

            if (showChannels) {
                ModalBottomSheet(
                    modifier = Modifier.padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
                    sheetState = sheetState,
                    onDismissRequest = {
                        scope.launch {
                            sheetState.hide()
                            showChannels = false
                        }
                    },
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        itemsIndexed(viewModel.state.value.channels) { _, channel ->
                            Column {
                                val progress = if (channel.inboundLiquiditySat > 0) {
                                    (channel.balanceSat.toFloat() / channel.inboundLiquiditySat.toFloat())
                                } else {
                                    0f
                                }
                                Log.d("MainScreen", "progress: $progress")

                                LinearProgressIndicator(
                                    progress = {
                                        progress
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                                        .height(8.dp),
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        channel.state,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text("${channel.balanceSat.formatSat()} / ${channel.inboundLiquiditySat.formatSat()}")
                                }
                            }
                        }
                    }
                }
            }

            NavigationBar {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                showChannels = true
                                sheetState.show()
                            }
                        },
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Settings",
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                        Modifier
                            .fillMaxWidth(),
                    ) {
                        ElevatedButton(
                            onClick = {
                                navController.navigate("receive")
                            },
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Default.ArrowDownward,
                                    contentDescription = "Settings",
                                )
                                Text("Receive")
                            }
                        }
                        Spacer(modifier = Modifier.size(16.dp))
                        ElevatedButton(
                            onClick = {
                                navController.navigate("send")
                            },
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Default.ArrowUpward,
                                    contentDescription = "Settings",
                                )
                                Text("Send")
                            }
                        }
                    }
                }
            }
        },
    ) { innerPadding ->
        val state = viewModel.state.collectAsStateWithLifecycle()
        val refreshState = rememberPullToRefreshState()

        PullToRefreshBox(
            state = refreshState,
            isRefreshing = state.value.isRefreshing,
            onRefresh = {
                viewModel.loadData()
            },
        ) {
            LazyColumn(
                modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                item {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = DecimalFormat("#,###").format(state.value.balance),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(text = "sat")
                    }
                }

                itemsIndexed(state.value.payments) { _, payment ->
                    Card(
                        modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    payment.receivedSat?.let {
                                        Text("You Received")
                                        Column(
                                            horizontalAlignment = Alignment.End,
                                        ) {
                                            Text(
                                                text = "${DecimalFormat("#,###").format(it)} sat",
                                                textAlign = TextAlign.End,
                                            )
                                            formatCreatedAt(payment.createdAt)?.let {
                                                Text(
                                                    it,
                                                    fontWeight = FontWeight.Light,
                                                )
                                            }
                                        }
                                    }
                                    payment.sent?.let {
                                        Text("You sent")
                                        Column(
                                            horizontalAlignment = Alignment.End,
                                        ) {
                                            val fees = payment.fees ?: 0
                                            val fee = if (fees > 0) fees / 1000 else 0
                                            val sent = if (fees > 0) it - fee else it
                                            Text(
                                                text = "${DecimalFormat("#,###").format(sent)} sat + ${DecimalFormat("#,###").format(fee)} sat fee",
                                                textAlign = TextAlign.End,
                                            )
                                            formatCreatedAt(payment.createdAt)?.let {
                                                Text(
                                                    it,
                                                    fontWeight = FontWeight.Light,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            payment.description?.let {
                                Text(
                                    it,
                                    modifier = Modifier.padding(8.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun formatCreatedAt(
    createdAt: Long?,
    locale: Locale = Locale.getDefault(),
): String? {
    return createdAt?.let {
        val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale)
        dateTime.format(formatter)
    }
}
