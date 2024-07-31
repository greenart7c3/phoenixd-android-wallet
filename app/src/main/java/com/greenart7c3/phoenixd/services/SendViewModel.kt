package com.greenart7c3.phoenixd.services

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.greenart7c3.phoenixd.utils.Parser
import fr.acinq.lightning.payment.Bolt11Invoice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

data class SendState(
    val sanitizedInput: String = "",
    val showScanner: Boolean = true,
    val isLoading: Boolean = false,
)

class SendViewModel : ViewModel() {
    private val _state = MutableStateFlow(SendState())
    val state = _state
    private val httpClient = CustomHttpClient()
    var amount: Long = 0

    fun onScannedText(text: String) {
        val input = Parser.removePrefix(Parser.removeExcessInput(text))
        state.value = state.value.copy(
            sanitizedInput = input,
            showScanner = false,
        )
    }

    override fun onCleared() {
        httpClient.close()
        super.onCleared()
    }

    fun clear() {
        state.value = SendState()
    }

    fun processInput() {
        val input = state.value.sanitizedInput
        if (input.startsWith("lnbc")) {
            val invoice = Bolt11Invoice.read(input)
            invoice.get().let {
                amount = it.amount?.truncateToSatoshi()?.sat ?: 0L
            }
        }
    }

    fun send(context: Context, navController: NavController) {
        if (amount <= 0) {
            Toast.makeText(
                context,
                "Amount must be greater than 0",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        state.value = state.value.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            val input = state.value.sanitizedInput
            val response = if (input.startsWith("lnbc")) {
                httpClient.submitForm(
                    "payinvoice",
                    listOf(
                        Pair("amountSat", amount.toString()),
                        Pair("invoice", input),
                    ),
                )
            } else if (input.startsWith("lno1")) {
                httpClient.submitForm(
                    "payoffer",
                    listOf(
                        Pair("amountSat", amount.toString()),
                        Pair("offer", input),
                    ),
                )
            } else if (input.contains("@", ignoreCase = true)) {
                httpClient.submitForm(
                    "paylnaddress",
                    listOf(
                        Pair("amountSat", amount.toString()),
                        Pair("address", input),
                    ),
                )
            } else if (input.startsWith("bc1")) {
                httpClient.submitForm(
                    "sendtoaddress",
                    listOf(
                        Pair("amountSat", amount.toString()),
                        Pair("address", input),
                    ),
                )
            } else {
                viewModelScope.launch(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Invalid input",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                state.value = state.value.copy(isLoading = false)
                return@launch
            }

            state.value = state.value.copy(isLoading = false)
            if (response.status.value == 200) {
                viewModelScope.launch(Dispatchers.Main) {
                    navController.navigateUp()
                }
            } else {
                viewModelScope.launch(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Failed to send payment",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    fun changeAmount(text: String) {
        val localAmount = text.toLongOrNull() ?: 0
        amount = localAmount
    }
}
