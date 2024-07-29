/*
 * Copyright 2022 ACINQ SAS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.greenart7c3.phoenixd.utils

import fr.acinq.bitcoin.utils.Try
import fr.acinq.lightning.payment.Bolt11Invoice
import fr.acinq.lightning.wire.OfferTypes

object Parser {

    /** Order matters, as the prefixes are matched with startsWith. Longest prefixes should be at the beginning to avoid trimming only a part of the prefix. */
    val lightningPrefixes = listOf(
        "phoenix:lightning://",
        "phoenix:lightning:",
        "lightning://",
        "lightning:",
    )

    val bitcoinPrefixes = listOf(
        "phoenix:bitcoin://",
        "phoenix:bitcoin:",
        "bitcoin://",
        "bitcoin:",
    )

    val lnurlPrefixes = listOf(
        "phoenix:lnurl://",
        "phoenix:lnurl:",
        "lnurl://",
        "lnurl:",
    )

    fun removeExcessInput(input: String) = input.lines().firstOrNull { it.isNotBlank() }?.replace("\\u00A0", "")?.trim() ?: ""

    fun removePrefix(input: String): String {
        val prefixes = lightningPrefixes + bitcoinPrefixes + lnurlPrefixes

        return prefixes.firstOrNull { input.startsWith(it, ignoreCase = true) }?.let {
            input.drop(it.length)
        } ?: input
    }

    /**
     * Remove the prefix from the input, if any. Trimming is done in a case-insensitive manner because often QR codes will
     * use upper-case for the prefix, such as LIGHTNING:LNURL1...
     */
    fun trimMatchingPrefix(
        input: String,
        prefixes: List<String>,
    ): String {
        val matchingPrefix = prefixes.firstOrNull { input.startsWith(it, ignoreCase = true) }
        return if (matchingPrefix != null) {
            input.drop(matchingPrefix.length)
        } else {
            input
        }
    }

    /** Reads a payment request after stripping prefixes. Return null if input is invalid. */
    fun readBolt11Invoice(input: String): Bolt11Invoice? {
        return when (val res = Bolt11Invoice.read(trimMatchingPrefix(removeExcessInput(input), lightningPrefixes))) {
            is Try.Success -> res.get()
            is Try.Failure -> null
        }
    }

    fun readOffer(input: String): OfferTypes.Offer? {
        val cleanInput = trimMatchingPrefix(removeExcessInput(input), lightningPrefixes)
        return when (val res = OfferTypes.Offer.decode(cleanInput)) {
            is Try.Success -> res.get()
            is Try.Failure -> {
                null
            }
        }
    }
}
