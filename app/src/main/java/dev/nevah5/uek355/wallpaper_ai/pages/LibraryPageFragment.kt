package dev.nevah5.uek355.wallpaper_ai.pages

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import dev.nevah5.uek355.wallpaper_ai.ImageViewActivity
import dev.nevah5.uek355.wallpaper_ai.R

class LibraryPageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val templates = HashMap<String, String>()
        templates["https://cdn.midjourney.com/639e3bbe-84a5-47a8-a4a3-9ec4c376f1d7/0_3.webp"] = "black ink illustration of empress, light pink accents, in the style of eve ventrue, 2d game art, clamp, porcelain, high-contrast shading, illustration, hurufiyya"
        templates["https://cdn.midjourney.com/213483d2-0cab-4514-b245-b400ad8e1e71/0_3.webp"] = "breathtakingly beautiful scene where a divine pink dragon"
        templates["https://cdn.midjourney.com/9d6de1eb-be8a-4fd2-b01f-226bd759cce1/0_2.webp"] = "Epic ink bending shot, A ancient Chinesewarrior wearing hanfu fghting with a dragon,POV view, First person, traditional Chinese inkpainting style, exaggerated perspective,breathtaking moment, 8k, simple style, style byWu Guanzhong"

        drawImages(view.findViewById(R.id.library_templates), templates)
    }

    private fun drawImages(container: LinearLayout, images: Map<String, String>) {
        container.removeAllViews()
        images.forEach { (uri, description) ->
            val imageView = ImageView(requireActivity()).apply {
                val layoutParams = LinearLayout.LayoutParams(
                    dpToPx(500),
                    dpToPx(500)
                )
                Glide.with(this@LibraryPageFragment)
                    .load(uri)
                    .placeholder(R.drawable.baseline_image_24) // replace with your placeholder drawable
                    .error(R.drawable.baseline_image_not_supported_24) // replace with your error drawable
                    .into(this)

                val marginInPixels = dpToPx(20) // For 10dp margin, for example
                layoutParams.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
                this.layoutParams = layoutParams
            }

            container.addView(imageView)

            imageView.setOnClickListener {
                startImageViewActivity(uri, description)
            }
        }
    }

    private fun startImageViewActivity(url: String, description: String){
        val imageViewIntent = Intent(requireActivity(), ImageViewActivity::class.java)
        imageViewIntent.putExtra("url", url)
        imageViewIntent.putExtra("description", description)
        startActivity(imageViewIntent)
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }
}

