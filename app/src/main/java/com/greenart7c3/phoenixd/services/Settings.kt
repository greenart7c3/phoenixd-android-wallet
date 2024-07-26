package com.greenart7c3.phoenixd.services

import io.ktor.http.URLProtocol

object Settings {
    var protocol: URLProtocol = URLProtocol.HTTP
    var host: String = ""
    var port: Int = 0
    var password: String = ""
}
