package dev.nevah5.uek355.wallpaper_ai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.nevah5.uek355.wallpaper_ai.services.DatabaseService
import dev.nevah5.uek355.wallpaper_ai.services.OpenAiService

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        val databaseServiceIntent = Intent(this, DatabaseService::class.java)
        startService(databaseServiceIntent)

        val openAiServiceIntent = Intent(this, OpenAiService::class.java)
        startService(openAiServiceIntent)
    }
}