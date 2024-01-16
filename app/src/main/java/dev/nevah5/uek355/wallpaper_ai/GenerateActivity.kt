package dev.nevah5.uek355.wallpaper_ai

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import dev.nevah5.uek355.wallpaper_ai.services.PreferenceService
import dev.nevah5.uek355.wallpaper_ai.services.OpenAiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GenerateActivity : AppCompatActivity() {
    private lateinit var preferenceService: PreferenceService
    private lateinit var openAiService: OpenAiService
    private var isDatabaseServiceBound = false
    private var isOpenAiServiceBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generate)

        Intent(this, PreferenceService::class.java).also { intent ->
            this.bindService(intent, databaseConnection, Context.BIND_AUTO_CREATE)
        }

        Intent(this, OpenAiService::class.java).also { intent ->
            this.bindService(intent, openAiConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun checkServicesConnected() {
        if (!isDatabaseServiceBound || !isOpenAiServiceBound) return
        println(databaseConnection)
        println(openAiConnection)

        val isWallpaper = intent.getBooleanExtra("isWallpaper", true)
        val description = intent.getStringExtra("description") as String

        println("Wallpaper: $isWallpaper")
        println("Description: $description")

        GlobalScope.launch(Dispatchers.IO) {
            val resultBool = openAiService.generateImage(preferenceService.getApiKey(), description, isWallpaper)
            launch(Dispatchers.Main) {
                println("GENERATED IMAGE RESULT BOOL: $resultBool")
                finish()
            }
        }
    }

    private val databaseConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as PreferenceService.LocalBinder
            preferenceService = binder.getService()
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
        if (isDatabaseServiceBound) {
            this.unbindService(databaseConnection)
            isDatabaseServiceBound = false
        }
        if (isOpenAiServiceBound) {
            this.unbindService(openAiConnection)
            isOpenAiServiceBound = false
        }
    }
}