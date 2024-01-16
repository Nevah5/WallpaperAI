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
import dev.nevah5.uek355.wallpaper_ai.services.models.ImageResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

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

    suspend fun generateImage(apiKey: String, description: String, isWallpaper: Boolean): ImageResponseModel {
        val client = OkHttpClient.Builder()
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
        val payload = """
        {
            "model": "dall-e-3",
            "prompt": "$description",
            "n": 1,
            "size": "${ if(isWallpaper) "1024x1792" else "1792x1024"}"
        }
        """
        println("Message payload: $payload")
        val request = Request.Builder()
            .url("https://api.openai.com/v1/images/generations")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(payload.toRequestBody())
            .build()

        return try {
            val response = withContext(Dispatchers.IO) {
                client.newCall(request).execute()
            }

            val responseBody = response.body?.string() ?: return ImageResponseModel("Error: Empty response", description, false)
            val jsonObject = JSONObject(responseBody)
            val dataArray = jsonObject.getJSONArray("data")

            if (dataArray.length() > 0) {
                val firstItem = dataArray.getJSONObject(0)
                val url = firstItem.getString("url")
                ImageResponseModel(url, description, true)
            } else {
                ImageResponseModel("Error: No data in response", description, false)
            }
        } catch (e: IOException) {
            ImageResponseModel("Something went wrong: ${e.message}", description, false)
        }
    }

    fun getErrorString(): String {
        return errorString
    }
}