package com.hirshler.remindme.managers

import android.content.res.Resources
import com.hirshler.remindme.R
import com.hirshler.remindme.SP

private const val THEME_COLOR_KEY = "theme_color_key"
private const val THEME_TEXTURE_KEY = "theme_texture_key"
private const val DEFAULT_THEME_COLOR = R.style.Theme_RemindMe_blackOverlay
private const val DEFAULT_THEME_TEXTURE = R.style.textureOverlayBamboo

class ThemeManager {

    companion object {
//        var color: Int? = null
//        var texture: Int? = null

        //region public

        fun setAppTheme(theme: Resources.Theme) {
            setColor(theme)
            setTexture(theme)
        }


        //endregion


        //region private
        private fun setColor(theme: Resources.Theme) {
            theme.applyStyle(getThemeColor(), true)
        }

        private fun setTexture(theme: Resources.Theme) {
            theme.applyStyle(getThemeTexture(), true)
        }


        fun getThemeColor(): Int {
            return SP.get().getInt(THEME_COLOR_KEY, DEFAULT_THEME_COLOR)
        }

        fun getThemeTexture(): Int {
            return SP.get().getInt(THEME_TEXTURE_KEY, DEFAULT_THEME_TEXTURE)
        }

        fun setThemeColor(newColor: Int) {
            if (newColor != -1) {
                SP.set().putInt(THEME_COLOR_KEY, newColor).apply()
            }
        }

        fun setThemeTexture(newTexture: Int) {
            SP.set().putInt(THEME_TEXTURE_KEY, newTexture).apply()
        }

        //endregion

    }
}
