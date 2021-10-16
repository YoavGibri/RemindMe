package com.hirshler.remindme

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SP {
    companion object {

        private const val DEFAULT_RING_PATH: String = "default_ring_path"
        private val sp: SharedPreferences =
            App.applicationContext().getSharedPreferences("remindMe", Context.MODE_PRIVATE)


        fun getDefaultRingtonePath(): String {
            return sp.getString(DEFAULT_RING_PATH, "")!!
        }

        fun setDefaultRingtonePath(path: String) {
            sp.edit { putString(DEFAULT_RING_PATH, path) }
        }

    }
}