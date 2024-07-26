package com.greenart7c3.phoenixd.models

import kotlinx.serialization.Serializable

@Serializable
data class Channel(
    val state: String,
    val channelId: String,
    val balanceSat: Long,
    val inboundLiquiditySat: Long,
    val capacitySat: Long,
    val fundingTxId: String,
)
