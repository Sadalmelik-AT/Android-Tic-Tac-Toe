package com.example.silviobaldwintsangacadet_dm2_projet
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

class Images(private val context: Context) {

    val imageMap: Map<String, Drawable?> = mapOf(
        "circle" to ContextCompat.getDrawable(context, R.drawable.circle),
        "cross" to ContextCompat.getDrawable(context, R.drawable.cross),
        "star" to ContextCompat.getDrawable(context, R.drawable.star),
        "planet" to ContextCompat.getDrawable(context, R.drawable.planet),
        "atom" to ContextCompat.getDrawable(context, R.drawable.atom),
        "circle_win" to ContextCompat.getDrawable(context, R.drawable.circle_win),
        "cross_win" to ContextCompat.getDrawable(context, R.drawable.cross_win),
        "star_win" to ContextCompat.getDrawable(context, R.drawable.star_win),
        "planet_win" to ContextCompat.getDrawable(context, R.drawable.planet_win),
        "atom_win" to ContextCompat.getDrawable(context, R.drawable.atom_win)
    )

    // Access individual images by resource name
    fun getImage(resourceName: String): Drawable? {
        return imageMap[resourceName]
    }

}
