package com.softvision.preferences

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.softvision.preferences.TmdbSharedPreferences.TmdbPreferenceKey
import org.junit.Assert
import org.junit.Test
import java.util.*

class TmdbSharedPreferencesTest {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val tmdbSharedPreferences = TmdbSharedPreferences(context)

    @Test
    fun `savePreference writes correctly to SharedPreferences`() {
        val valueToSave = UUID.randomUUID().toString()
        tmdbSharedPreferences.savePreference(TmdbPreferenceKey.SESSION_ID, valueToSave)
        val valueFromPreferences = tmdbSharedPreferences.getPreference<String>(TmdbPreferenceKey.SESSION_ID)
        Assert.assertEquals(valueToSave, valueFromPreferences)
    }

    @Test
    fun `clearPreference removes from SharedPreferences`() {
        val valueToSave = UUID.randomUUID().toString()
        tmdbSharedPreferences.savePreference(TmdbPreferenceKey.SESSION_ID, valueToSave)
        tmdbSharedPreferences.clearPreference(TmdbPreferenceKey.SESSION_ID)
        val valueFromPreferences = tmdbSharedPreferences.getPreference<String>(TmdbPreferenceKey.SESSION_ID)
        Assert.assertEquals(null, valueFromPreferences)
    }

}