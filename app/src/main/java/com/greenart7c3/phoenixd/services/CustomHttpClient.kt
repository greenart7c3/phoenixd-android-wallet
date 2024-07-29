package com.greenart7c3.phoenixd.services

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.encodedPath
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class CustomHttpClient {
    private val httpClient =
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    },
                )
            }
        }

    fun close() {
        httpClient.close()
    }

    suspend fun get(url: String): HttpResponse {
        val localUrl = if (url.startsWith("/")) url else "/$url"
        return httpClient.get {
            url {
                protocol = Settings.protocol
                host = Settings.host
                port = Settings.port
                encodedPath = localUrl
            }
            parameters {
                append("limit", "1000")
            }
            basicAuth("", Settings.password)
        }
    }
}
