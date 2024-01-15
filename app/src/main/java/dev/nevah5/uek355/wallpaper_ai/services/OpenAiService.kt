package dev.nevah5.uek355.wallpaper_ai.services

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.JsonReader
import android.view.View
import android.widget.EditText
import android.widget.Toast
import dev.nevah5.uek355.wallpaper_ai.MainActivity
import dev.nevah5.uek355.wallpaper_ai.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class OpenAiService : Service() {
    private val binder = LocalBinder()
    private var errorString = ""

    inner class LocalBinder : Binder() {
        fun getService(): OpenAiService = this@OpenAiService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    suspend fun verifyApiKey(apiKey: String): Boolean {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://api.openai.com/v1/models")
            .addHeader("Authorization", "Bearer $apiKey")
            .build()

        return try {
            // TODO: Use debug logger
            println("Making API Request with Key: $apiKey")
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }
            // TODO: Use debug logger
            println("Response: $response")

            if (response.isSuccessful) {
                true
            } else {
                errorString = "Invalid API Key"
                false
            }
        } catch (e: IOException) {
            errorString = "Check internet connection?"
            false
        }
    }

    fun getErrorString(): String {
        return errorString
    }

    private fun reportErrorToUser() {
        TODO("implement")
    }
}