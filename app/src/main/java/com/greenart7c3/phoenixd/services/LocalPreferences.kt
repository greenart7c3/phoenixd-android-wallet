package com.greenart7c3.phoenixd.services

import android.content.Context
import android.content.SharedPreferences
import io.ktor.http.URLProtocol

private object PrefKeys {
    const val HOST = "host"
    const val PORT = "port"
    const val PASSWORD = "password"
    const val PROTOCOL = "protocol"
}

object LocalPreferences {
    suspend fun getSavedSettings(context: Context) {
        val preferences = encryptedPreferences(context)

        Settings.host = preferences.getString(PrefKeys.HOST, "") ?: ""
        Settings.port = preferences.getInt(PrefKeys.PORT, 0)
        Settings.password = preferences.getString(PrefKeys.PASSWORD, "") ?: ""
        val protocol = preferences.getString(PrefKeys.PROTOCOL, "") ?: ""
        if (protocol == "https") {
            Settings.protocol = URLProtocol.HTTPS
        } else {
            Settings.protocol = URLProtocol.HTTP
        }
    }

    suspend fun saveSettings(context: Context) {
        val preferences = encryptedPreferences(context)
        val editor = preferences.edit()

        editor.putString(PrefKeys.HOST, Settings.host)
        editor.putInt(PrefKeys.PORT, Settings.port)
        editor.putString(PrefKeys.PASSWORD, Settings.password)
        editor.putString(PrefKeys.PROTOCOL, Settings.protocol.name)

        editor.apply()
    }

    private fun encryptedPreferences(
        context: Context,
    ): SharedPreferences {
        return EncryptedStorage.preferences(context)
    }
}