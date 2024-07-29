package com.greenart7c3.phoenixd.models

import kotlinx.serialization.Serializable

@Serializable
data class GeneratedInvoice(
    val amountSat: Long? = null,
    val paymentHash: String,
    val serialized: String,
)
