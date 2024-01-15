package dev.nevah5.uek355.wallpaper_ai.pages

import android.app.Activity
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import dev.nevah5.uek355.wallpaper_ai.GenerateActivity
import dev.nevah5.uek355.wallpaper_ai.LoginActivity
import dev.nevah5.uek355.wallpaper_ai.R
import dev.nevah5.uek355.wallpaper_ai.services.DatabaseService


class CreatePageFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var databaseService: DatabaseService
    private var isBound = false

    private val loginActivityResultListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_CANCELED) {
                navController.navigate(R.id.libraryPageFragment)
            }
        }

    private val generateActivityResultListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                navController.navigate(R.id.libraryPageFragment)
            } else {
                Toast.makeText(requireActivity(), "The generation was cancelled or crashed unexpectedly.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()

        val generateWallpaperButton = view.findViewById<AppCompatButton>(R.id.create_button_wallpaper)
        val generateWidgetButton = view.findViewById<AppCompatButton>(R.id.create_button_widget)

        generateWallpaperButton.setOnClickListener {
            // TODO: Pass in wallpaper/widget
            val generateActivityIntent = Intent(requireActivity(), GenerateActivity::class.java)
            generateActivityResultListener.launch(generateActivityIntent)
        }
        generateWidgetButton.setOnClickListener {
            // TODO: Pass in wallpaper/widget
            val generateActivityIntent = Intent(requireActivity(), GenerateActivity::class.java)
            generateActivityResultListener.launch(generateActivityIntent)
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

    override fun onResume() {
        super.onResume()

        if (!databaseService.hasApiKey()) {
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            loginActivityResultListener.launch(intent)
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
