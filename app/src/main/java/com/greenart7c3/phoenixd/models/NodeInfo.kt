package com.greenart7c3.phoenixd.models

import kotlinx.serialization.Serializable

@Serializable
data class NodeInfo(
    val nodeId: String,
    val channels: List<Channel>,
    val chain: String,
    val blockHeight: Int,
    val version: String
)