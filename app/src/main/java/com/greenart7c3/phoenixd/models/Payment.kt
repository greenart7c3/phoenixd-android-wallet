package com.greenart7c3.phoenixd.models

import kotlinx.serialization.Serializable

@Serializable
data class Payment(
    val paymentId: String? = null,
    val paymentHash: String? = null,
    val preimage: String? = null,
    val isPaid: Boolean? = null,
    val sent: Long? = null,
    val fees: Long? = null,
    val invoice: String? = null,
    val completedAt: Long? = null,
    val createdAt: Long? = null,
    val receivedSat: Long? = null,
)