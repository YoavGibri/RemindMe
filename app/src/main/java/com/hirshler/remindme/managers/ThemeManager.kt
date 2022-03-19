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
        var color: Int? = null
        var texture: Int? = null

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


        private fun getThemeColor(): Int {
            if (color == null)
                color = SP.get().getInt(THEME_COLOR_KEY, DEFAULT_THEME_COLOR)
            return color!!
        }

        private fun getThemeTexture(): Int {
            if (texture == null)
                texture = SP.get().getInt(THEME_TEXTURE_KEY, DEFAULT_THEME_TEXTURE)
            return texture!!
        }

        //endregion

    }
}
