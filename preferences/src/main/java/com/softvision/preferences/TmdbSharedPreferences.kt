package com.softvision.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class TmdbSharedPreferences @Inject internal constructor(@ApplicationContext context: Context) {

    val sharedPreferences: SharedPreferences = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    val gson = Gson()

    inline fun <reified T : Any> getPreference(tmdbPreferenceKey: TmdbPreferenceKey): T? {
        return try {
            val content = sharedPreferences.getString(tmdbPreferenceKey.name, null)
            if (content == null) null
            else gson.fromJson(content, T::class.java)
        } catch (e: Exception) {
            Timber.e(e)
            null
        }
    }

    fun <T> savePreference(tmdbPreferenceKey: TmdbPreferenceKey, value: T) {
        sharedPreferences.edit {
            putString(tmdbPreferenceKey.name, gson.toJson(value))
        }
    }

    fun clearPreference(tmdbPreferenceKey: TmdbPreferenceKey) {
        sharedPreferences.edit {
            remove(tmdbPreferenceKey.name)
        }
    }

    enum class TmdbPreferenceKey {
        SESSION_ID,
        ACCOUNT_DETAILS
    }
}