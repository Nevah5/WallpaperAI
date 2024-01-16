package dev.nevah5.uek355.wallpaper_ai.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import org.json.JSONObject
import java.io.File

class DatabaseService : Service() {
    private val binder = LocalBinder()
    companion object {
        private const val IMAGES_JSON_FILE_NAME = "images.json"
    }

    inner class LocalBinder : Binder() {
        fun getService(): DatabaseService = this@DatabaseService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun saveImage(imageUrl: String, description: String) {
        val images = getImages().toMutableMap()
        images[imageUrl] = description
        writeToJSONFile(IMAGES_JSON_FILE_NAME, images)
    }

    fun getImages(): Map<String, String> {
        return readFromJSONFile(IMAGES_JSON_FILE_NAME)
    }

    private fun writeToJSONFile(fileName: String, data: Map<String, String>) {
        val jsonObject = JSONObject(data)
        val file = File(filesDir, fileName)
        file.writeText(jsonObject.toString())
    }

    private fun readFromJSONFile(fileName: String): Map<String, String> {
        val file = File(filesDir, fileName)
        if (!file.exists()) return emptyMap()

        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)
        val map = mutableMapOf<String, String>()

        jsonObject.keys().forEach {
            map[it] = jsonObject.getString(it)
        }

        return map
    }
}