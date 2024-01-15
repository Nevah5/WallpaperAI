package dev.nevah5.uek355.wallpaper_ai.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

// TODO: Refactor to `PreferenceService`
class DatabaseService : Service() {
    private val binder = LocalBinder()
    // TODO: make this persistent
    private var apiKey = ""

    companion object {
        private const val API_KEY_KEY = "API_KEY"
    }

    inner class LocalBinder : Binder() {
        fun getService(): DatabaseService = this@DatabaseService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun hasApiKey(): Boolean {
        return this.getApiKey().isNotEmpty()
    }

    fun setApiKey(apiKey: String) {
        val encryptedPrefs = getEncryptedSharedPreferences(this)
        encryptedPrefs.edit().putString(API_KEY_KEY, apiKey).apply()
    }

    fun getApiKey(): String {
        val encryptedPrefs = getEncryptedSharedPreferences(this)
        return encryptedPrefs.getString(API_KEY_KEY, "").toString()
    }

    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return EncryptedSharedPreferences.create(
            "encrypted_preferences",
            masterKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}