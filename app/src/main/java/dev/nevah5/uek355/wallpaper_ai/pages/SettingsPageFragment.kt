package dev.nevah5.uek355.wallpaper_ai.pages

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import dev.nevah5.uek355.wallpaper_ai.LoginActivity
import dev.nevah5.uek355.wallpaper_ai.R
import dev.nevah5.uek355.wallpaper_ai.services.DatabaseService

class SettingsPageFragment : Fragment() {
    private lateinit var databaseService: DatabaseService
    private var isBound = false
    private lateinit var view: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Intent(context, DatabaseService::class.java).also { intent ->
            context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.view = view

        val clearApiKeyButton =
            view.findViewById<AppCompatButton>(R.id.settings_button_reset_apikey)
        clearApiKeyButton.setOnClickListener {
            if (!isBound) Toast.makeText(
                requireActivity(),
                "Please wait until the Service is bound",
                Toast.LENGTH_SHORT
            ).show()
            onClearApiKeyButtonClick()
        }

        val setApiKeyButton =
            view.findViewById<AppCompatButton>(R.id.settings_button_set_apikey)
        setApiKeyButton.setOnClickListener {
            if (!isBound) Toast.makeText(
                requireActivity(),
                "Please wait until the Service is bound",
                Toast.LENGTH_SHORT
            ).show()
            onSetApiKeyButtonClick()
        }
    }

    private fun onClearApiKeyButtonClick() {
        databaseService.setApiKey("")
        Toast.makeText(requireActivity(), "Cleared API Key", Toast.LENGTH_LONG).show()
    }

    private fun onSetApiKeyButtonClick() {
        val loginActivityIntent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(loginActivityIntent)
    }

    private fun updateApiKeyButtonVisibilities(view: View) {
        val clearApiKeyButton =
            view.findViewById<AppCompatButton>(R.id.settings_button_reset_apikey)
        val setApiKeyButton =
            view.findViewById<AppCompatButton>(R.id.settings_button_set_apikey)

        if (databaseService.hasApiKey()) {
            clearApiKeyButton.visibility = View.VISIBLE
            setApiKeyButton.visibility = View.GONE
        } else {
            clearApiKeyButton.visibility = View.GONE
            setApiKeyButton.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_page, container, false)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as DatabaseService.LocalBinder
            databaseService = binder.getService()
            isBound = true

            updateApiKeyButtonVisibilities(view)
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            context?.unbindService(connection)
            isBound = false
        }
    }
}
