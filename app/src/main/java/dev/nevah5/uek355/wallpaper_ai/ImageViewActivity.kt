package dev.nevah5.uek355.wallpaper_ai

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Transition
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget

class ImageViewActivity : AppCompatActivity() {
    private lateinit var imageUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
    }

    override fun onStart() {
        super.onStart()

        imageUri = intent.getStringExtra("url") as String
        val imageDescription = intent.getStringExtra("description") as String

        val image = findViewById<ImageView>(R.id.imageview_wallpaper)
        Glide.with(this)
            .load(imageUri.toUri())
            .placeholder(R.drawable.baseline_image_24) // replace with your placeholder drawable
            .error(R.drawable.baseline_image_not_supported_24) // replace with your error drawable
            .into(image)

        val description = findViewById<TextView>(R.id.imageview_description)
        description.text = imageDescription
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG)
            .show()

    }

    fun onButtonSetWallpaper(view: View) {
        Glide.with(this)
            .asBitmap()
            .load(imageUri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    val wallpaperManager = WallpaperManager.getInstance(applicationContext)
                    try {
                        wallpaperManager.setBitmap(resource)
                        showToast("Success!")
                        finish()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        showToast("Something went wrong whilst trying to set wallpaper.")
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // not implemented
                }
            })
    }

    fun onButtonBackToLibrary(view: View){
        finish()
    }
}