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
import androidx.fragment.app.Fragment
import dev.nevah5.uek355.wallpaper_ai.LoginActivity
import dev.nevah5.uek355.wallpaper_ai.R
import dev.nevah5.uek355.wallpaper_ai.services.DatabaseService


class CreatePageFragment : Fragment() {
    private lateinit var databaseService: DatabaseService
    private var isBound = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_page, container, false)
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as DatabaseService.LocalBinder
            databaseService = binder.getService()
            isBound = true

            if(!databaseService.hasApiKey()) {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                return
            }
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(context, DatabaseService::class.java).also { intent ->
            context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
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
