package com.greenart7c3.phoenixd.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.greenart7c3.phoenixd.services.PhoenixdViewModel
import java.text.DecimalFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: PhoenixdViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp),
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
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
                        ElevatedButton(onClick = { /*TODO*/ }) {
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
                            fontSize = 24.sp,
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
