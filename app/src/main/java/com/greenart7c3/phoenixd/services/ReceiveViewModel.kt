package com.greenart7c3.phoenixd.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.greenart7c3.phoenixd.models.GeneratedInvoice
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class ReceiveState(
    var qrCode: String = "",
    var qrCodeBolt12: String = "",
    var amount: Long = 0,
    var description: String = "",
    var isRefreshing: Boolean = false,
)

class ReceiveViewModel : ViewModel() {
    private val _state = MutableStateFlow(ReceiveState())
    val state = _state
    private val httpClient = CustomHttpClient()

    init {
        loadData()
    }

    private fun loadData() {
        state.value =
            state.value.copy(
                isRefreshing = true,
            )
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = httpClient.get("getoffer")
                val body = response.bodyAsText()

                if (body.isNotBlank()) {
                    state.value =
                        state.value.copy(
                            qrCodeBolt12 = body,
                        )
                }
                val responseInvoice = httpClient.submitForm(
                    url = "createinvoice",
                    parameters = listOf(
                        Pair("description", ""),
                    ),
                )
                val bodyInvoice = responseInvoice.body<GeneratedInvoice>()
                state.value = state.value.copy(
                    qrCode = bodyInvoice.serialized,
                )
            } finally {
                state.value =
                    state.value.copy(
                        isRefreshing = false,
                    )
            }
        }
    }
}
