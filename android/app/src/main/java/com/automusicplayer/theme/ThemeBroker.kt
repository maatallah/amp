package com.automusicplayer.theme

import android.content.res.Configuration
import androidx.car.app.CarContext

class ThemeBroker(private val carContext: CarContext) {

    enum class ThemeMode {
        DAY,
        NIGHT
    }

    fun getCurrentThemeMode(): ThemeMode {
        val nightModeFlags = carContext.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> ThemeMode.NIGHT
            else -> ThemeMode.DAY
        }
    }

    fun isNightModeActive(): Boolean {
        return getCurrentThemeMode() == ThemeMode.NIGHT
    }
}
