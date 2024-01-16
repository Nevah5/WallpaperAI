package dev.nevah5.uek355.wallpaper_ai

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.EditText
import android.widget.Toast
import dev.nevah5.uek355.wallpaper_ai.services.PreferenceService
import dev.nevah5.uek355.wallpaper_ai.services.OpenAiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var preferenceService: PreferenceService
    private lateinit var openAiService: OpenAiService
    private var isDatabaseServiceBound = false
    private var isOpenAiServiceBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Intent(this, PreferenceService::class.java).also { intent ->
            this.bindService(intent, databaseConnection, Context.BIND_AUTO_CREATE)
        }

        Intent(this, OpenAiService::class.java).also { intent ->
            this.bindService(intent, openAiConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private val databaseConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as PreferenceService.LocalBinder
            preferenceService = binder.getService()
            isDatabaseServiceBound = true
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
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isOpenAiServiceBound = false
        }
    }

    fun onButtonContinue(view: View) {
        val editField = findViewById<EditText>(R.id.input_text_apikey)
        val apiKey = editField.text.toString().trim()

        if (apiKey.isEmpty()) {
            showToast("Please enter an API Key before continuing.")
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            val isValidKey = openAiService.verifyApiKey(apiKey)
            launch(Dispatchers.Main) {
                if (isValidKey) {
                    preferenceService.setApiKey(apiKey)
                    setResult(Activity.RESULT_OK)
                    showToast("Success!")
                    finish()
                } else {
                    showToast(openAiService.getErrorString())
                }
            }
        }
    }

    fun onButtonSkip(view: View) {
        setResult(Activity.RESULT_CANCELED)
        finish()
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}