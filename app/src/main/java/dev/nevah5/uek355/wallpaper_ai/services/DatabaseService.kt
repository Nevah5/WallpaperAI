package dev.nevah5.uek355.wallpaper_ai.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class DatabaseService : Service() {private val binder = LocalBinder()
    // TODO: make this persistent
    private var apiKey = ""

    inner class LocalBinder : Binder() {
        fun getService(): DatabaseService = this@DatabaseService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun hasApiKey(): Boolean {
        return apiKey.isNotEmpty()
    }

    fun setApiKey(apiKey: String) {
        this.apiKey = apiKey
    }

    fun getApiKey(): String {
        return apiKey
    }
}