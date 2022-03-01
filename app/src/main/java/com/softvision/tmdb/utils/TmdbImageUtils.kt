package com.softvision.tmdb.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

object TmdbImageUtils {

    fun glide(context: Context, url: String?, imageView: ImageView, progressDrawable: Drawable? = null, @DrawableRes errorDrawableRes: Int?=null) {
        Glide.with(context)
            .load(url)
            .placeholder(progressDrawable)
            .error(errorDrawableRes)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .into(imageView)
    }

}

object UrlBuilder {

    private const val WIDTH_ORIGINAL = "original"
    private const val WIDTH_1280 = "w1280"
    private const val WIDTH_780 = "w780"
    private const val WIDTH_500 = "w500"
    private const val WIDTH_300 = "w300"
    private const val WIDTH_185 = "w185"
    private const val WIDTH_154 = "w154"
    private const val WIDTH_92 = "w92"
    private const val WIDTH_45 = "w45"
    private const val BASE_URL = "https://image.tmdb.org/t/p"

    fun getPosterImageUrl(context: Context, imageId: String): String {
        return "$BASE_URL/${getPosterAppropriateSize(context)}$imageId"
    }

    fun getBackdropImageUrl(context: Context, imageId: String): String {
        return "$BASE_URL/${getBackdropAppropriateSize(context)}$imageId"
    }

    private fun getPosterAppropriateSize(context: Context): String {
        val density = context.resources.displayMetrics.density
        return when {
            density >= 4.0 -> WIDTH_500
            density >= 3.0 -> WIDTH_300
            density >= 2.0 -> WIDTH_185
            density >= 1.5 -> WIDTH_154
            density >= 1.0 -> WIDTH_92
            else -> WIDTH_45
        }
    }

    private fun getBackdropAppropriateSize(context: Context): String {
        val density = context.resources.displayMetrics.density
        return when {
            density >= 4.0 -> WIDTH_ORIGINAL
            density >= 3.0 -> WIDTH_ORIGINAL
            density >= 2.0 -> WIDTH_1280
            density >= 1.5 -> WIDTH_780
            density >= 1.0 -> WIDTH_300
            else -> WIDTH_300
        }
    }
}
