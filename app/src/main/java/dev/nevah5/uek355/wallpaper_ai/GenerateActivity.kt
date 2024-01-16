package dev.nevah5.uek355.wallpaper_ai

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import dev.nevah5.uek355.wallpaper_ai.services.DatabaseService
import dev.nevah5.uek355.wallpaper_ai.services.PreferenceService
import dev.nevah5.uek355.wallpaper_ai.services.OpenAiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GenerateActivity : AppCompatActivity() {
    private lateinit var preferenceService: PreferenceService
    private lateinit var openAiService: OpenAiService
    private lateinit var databaseService: DatabaseService
    private var isPreferenceServiceBound = false
    private var isOpenAiServiceBound = false
    private var isDatabaseServiceBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate)

        Intent(this, PreferenceService::class.java).also { intent ->
            this.bindService(intent, preferenceConnection, Context.BIND_AUTO_CREATE)
        }

        Intent(this, OpenAiService::class.java).also { intent ->
            this.bindService(intent, openAiConnection, Context.BIND_AUTO_CREATE)
        }

        Intent(this, DatabaseService::class.java).also { intent ->
            this.bindService(intent, databaseConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun checkServicesConnected() {
        if (!isPreferenceServiceBound || !isOpenAiServiceBound || !isDatabaseServiceBound) return

        val isWallpaper = intent.getBooleanExtra("isWallpaper", true)
        val description = intent.getStringExtra("description") as String

        println("Wallpaper: $isWallpaper")
        println("Description: $description")

        GlobalScope.launch(Dispatchers.IO) {
            val imageUrl = openAiService.generateImage(preferenceService.getApiKey(), description, isWallpaper)
            launch(Dispatchers.Main) {
                println("GENERATED IMAGE RESULT: $imageUrl")
                databaseService.saveImage(imageUrl, description)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private val preferenceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as PreferenceService.LocalBinder
            preferenceService = binder.getService()
            isPreferenceServiceBound = true
            checkServicesConnected()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isPreferenceServiceBound = false
        }
    }

    private val databaseConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as DatabaseService.LocalBinder
            databaseService = binder.getService()
            isDatabaseServiceBound = true
            checkServicesConnected()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isDatabaseServiceBound = false
        }
    }

    private val openAiConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as OpenAiService.LocalBinder
            openAiService = binder.getService()
            isOpenAiServiceBound = true
            checkServicesConnected()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isOpenAiServiceBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPreferenceServiceBound) {
            this.unbindService(preferenceConnection)
            isPreferenceServiceBound = false
        }
        if (isOpenAiServiceBound) {
            this.unbindService(openAiConnection)
            isOpenAiServiceBound = false
        }
        if (isDatabaseServiceBound) {
            this.unbindService(databaseConnection)
            isDatabaseServiceBound = false
        }
    }
}