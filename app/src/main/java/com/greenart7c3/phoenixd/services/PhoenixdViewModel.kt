package com.greenart7c3.phoenixd.services

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenart7c3.phoenixd.models.NodeInfo
import com.greenart7c3.phoenixd.models.Payment
import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class PhoenixdState(
    var balance: Long = 0,
    var payments: MutableList<Payment> = mutableListOf(),
    val isRefreshing: Boolean = false,
)

class PhoenixdViewModel : ViewModel() {
    private val _state = MutableStateFlow(PhoenixdState())
    val state = _state
    private val httpClient = CustomHttpClient()

    init {
        Log.d("PhoenixdViewModel", "init")
        loadData()
    }

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            state.value =
                state.value.copy(
                    isRefreshing = true,
                )
            try {
                val response = httpClient.get("getinfo")
                val body = response.body<NodeInfo>()
                val localPayments = mutableListOf<Payment>()
                val response3 = httpClient.get("payments/incoming")
                val incomingPayments = response3.body<List<Payment>>()
                val response4 = httpClient.get("payments/outgoing")
                val outgoingPayments = response4.body<List<Payment>>()

                localPayments.addAll(incomingPayments)
                localPayments.addAll(outgoingPayments)
                localPayments.sortByDescending { it.createdAt }

                state.value.payments.clear()
                state.value.payments.addAll(localPayments)

                state.value =
                    state.value.copy(
                        balance = body.channels.sumOf { it.balanceSat },
                        isRefreshing = false,
                    )
            } catch (e: Exception) {
                Log.e("error", e.toString())
                state.value =
                    state.value.copy(
                        isRefreshing = false,
                    )
            }
        }
    }

    override fun onCleared() {
        Log.d("PhoenixdViewModel", "onCleared")
        httpClient.close()
        super.onCleared()
    }
}
