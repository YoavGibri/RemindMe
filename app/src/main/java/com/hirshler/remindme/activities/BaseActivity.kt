package com.hirshler.remindme.activities

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import com.hirshler.remindme.managers.ThemeManager


open class BaseActivity : AppCompatActivity() {


    override fun onApplyThemeResource(theme: Resources.Theme?, resid: Int, first: Boolean) {
        super.onApplyThemeResource(theme, resid, first)
        if (first && theme != null) {
            ThemeManager.setAppTheme(theme)
        }
    }
}