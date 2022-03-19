package com.hirshler.remindme.activities

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import com.hirshler.remindme.managers.ThemeManager


open class BaseActivity : AppCompatActivity() {


    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        ThemeManager.setAppTheme(theme)
        return theme
    }
}