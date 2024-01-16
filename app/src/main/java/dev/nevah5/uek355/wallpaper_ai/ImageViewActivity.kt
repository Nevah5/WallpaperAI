package dev.nevah5.uek355.wallpaper_ai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.net.toUri
import com.bumptech.glide.Glide

class ImageViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
    }

    override fun onStart() {
        super.onStart()

        val imageUrl = intent.getStringExtra("url") as String
        val imageDescription = intent.getStringExtra("description") as String

        val image = findViewById<ImageView>(R.id.imageview_wallpaper)
        Glide.with(this)
            .load(imageUrl.toUri())
            .placeholder(R.drawable.baseline_image_24) // replace with your placeholder drawable
            .error(R.drawable.baseline_image_not_supported_24) // replace with your error drawable
            .into(image)

        val description = findViewById<TextView>(R.id.imageview_description)
        description.text = imageDescription
    }
}