package com.greenart7c3.phoenixd.services

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.basicAuth
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLProtocol
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

    suspend fun submitForm(
        url: String,
        parameters: List<Pair<String, String>>,
    ): HttpResponse {
        val localUrl = if (url.startsWith("/")) url else "/$url"
        val protocol = if (Settings.protocol == URLProtocol.HTTP) "http" else "https"
        return httpClient.submitForm(
            url = "$protocol://${Settings.host}:${Settings.port}$localUrl",
            formParameters = parameters {
                parameters.forEach {
                    append(it.first, it.second)
                }
            },
        ) {
            basicAuth("", Settings.password)
        }
    }

    suspend fun post(
        url: String,
    ): HttpResponse {
        val localUrl = if (url.startsWith("/")) url else "/$url"
        return httpClient.post {
            url {
                protocol = Settings.protocol
                host = Settings.host
                port = Settings.port
                encodedPath = localUrl
            }
            basicAuth("", Settings.password)
        }
    }
}
