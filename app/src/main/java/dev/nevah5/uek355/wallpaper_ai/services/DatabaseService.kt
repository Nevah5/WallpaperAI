package dev.nevah5.uek355.wallpaper_ai.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class DatabaseService : Service() {
    private val binder = LocalBinder()
    // TODO: make this persistent
    private var apiKey = ""

    inner class LocalBinder : Binder() {
        fun getService(): DatabaseService = this@DatabaseService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
//        loadDataFromDatabase()
    }

    private fun loadDataFromDatabase() {
        TODO("Code to load from database")
    }

    fun hasApiKey(): Boolean {
        return apiKey.isNotEmpty()
    }

    fun setApiKey(apiKey: String) {
        // TODO: save into database/file
        this.apiKey = apiKey
    }

    fun getApiKey(): String {
        return apiKey
    }
}