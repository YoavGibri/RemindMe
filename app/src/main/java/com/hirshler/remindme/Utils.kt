package com.hirshler.remindme

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

class Utils {

    companion object {
        fun hideKeyboard(view: View) {
            val inputMethodManager = ContextCompat.getSystemService(App.applicationContext(), InputMethodManager::class.java)!!
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

}