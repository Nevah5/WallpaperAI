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
import android.widget.EditText
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
                view?.findViewById<EditText>(R.id.create_input_imagination)?.text?.clear()
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
        val inputImageImagination = view.findViewById<EditText>(R.id.create_input_imagination)

        generateWallpaperButton.setOnClickListener {
            if(validateImaginationInputText(inputImageImagination.text.toString())) {
                startGenerationActivity(inputImageImagination, true)
            }
        }
        generateWidgetButton.setOnClickListener {
            if(validateImaginationInputText(inputImageImagination.text.toString())) {
                startGenerationActivity(inputImageImagination, false)
            }
        }
    }

    private fun startGenerationActivity(inputImageImagination: EditText, isWallpaper: Boolean){
        val generateActivityIntent = Intent(requireActivity(), GenerateActivity::class.java)
        generateActivityIntent.putExtra("description", inputImageImagination.text.toString())
        generateActivityIntent.putExtra("isWallpaper", isWallpaper)
        generateActivityResultListener.launch(generateActivityIntent)
    }

    private fun validateImaginationInputText(text: String): Boolean {
        // I know, I know, this does not belong here. No logic... Normally this would go into the ViewModel,
        // but since I am not using the MVVMC design pattern, more like the MVC pattern, I put this here.
        // This is not critical code... So testing it would be a waste of time in my time management.
        if(text.isEmpty()) {
            Toast.makeText(requireActivity(), "Please provide your fancy and creating imagination.", Toast.LENGTH_SHORT)
                .show()
            return false
        } else if (text.length < 20) {
            Toast.makeText(requireActivity(), "Please provide at least 20 characters.", Toast.LENGTH_SHORT)
                .show()
            return false
        }
        return true
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
