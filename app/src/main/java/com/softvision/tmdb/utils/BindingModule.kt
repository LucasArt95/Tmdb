package com.softvision.tmdb.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.softvision.tmdb.R


object BindingModule {

    @JvmStatic
    @BindingAdapter("show")
    fun show(view: View, visible: Boolean) {
        if (visible) view.visibility = View.VISIBLE
        else view.visibility = View.GONE
    }

    @JvmStatic
    @BindingAdapter("tmdbImagePoster")
    fun tmdbImagePoster(view: ImageView, imageId: String?) {
        val context = view.context
        view.setImageDrawable(null)
        val url = imageId?.let { UrlBuilder.getPosterImageUrl(context, it) }
        val placeholder = getProgressBarIndeterminate(context, 50f, 5f)
        TmdbImageUtils.glide(context, url, view, placeholder, R.drawable.ic_broken_image)
    }

    @JvmStatic
    @BindingAdapter("tmdbImageBackdrop")
    fun tmdbImageBackdrop(view: ImageView, imageId: String?) {
        val context = view.context
        view.setImageDrawable(null)
        val url = imageId?.let { UrlBuilder.getBackdropImageUrl(context, it) }
        val placeholder = getProgressBarIndeterminate(context, 200f, 20f)
        TmdbImageUtils.glide(context, url, view, placeholder, R.drawable.ic_broken_image)
    }

    private fun getProgressBarIndeterminate(
        context: Context,
        radiusSize: Float,
        strokeWidth: Float,
    ): Drawable {
        val placeholder = CircularProgressDrawable(context)
        placeholder.setStyle(CircularProgressDrawable.LARGE)
        placeholder.setColorSchemeColors(ContextCompat.getColor(context, R.color.loading))
        placeholder.centerRadius = radiusSize
        placeholder.strokeWidth = strokeWidth
        placeholder.start()
        return placeholder
    }


    @JvmStatic
    @BindingAdapter("favoriteIcon")
    fun favoriteIcon(view: FloatingActionButton, isFavorite: Boolean) {
        view.setImageResource(if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_empty)
    }
}