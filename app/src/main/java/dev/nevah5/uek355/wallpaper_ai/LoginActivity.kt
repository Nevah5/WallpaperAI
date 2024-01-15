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
import dev.nevah5.uek355.wallpaper_ai.services.DatabaseService

class LoginActivity : AppCompatActivity() {
    private lateinit var databaseService: DatabaseService
    private var isBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Intent(this, DatabaseService::class.java).also { intent ->
            this.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as DatabaseService.LocalBinder
            databaseService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    fun onButtonContinue(view: View) {
        val editField = findViewById<EditText>(R.id.input_text_apikey)
        if (editField.text.isEmpty()) {
            Toast.makeText(this, "Please enter an API Key before continuing.", Toast.LENGTH_LONG)
                .show()
            return
        }
        databaseService.setApiKey(editField.text.toString())
        setResult(Activity.RESULT_OK)
        finish()
    }

    fun onButtonSkip(view: View) {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound) {
            this.unbindService(connection)
            isBound = false
        }
    }
}