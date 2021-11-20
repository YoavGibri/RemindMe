package com.hirshler.remindme

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SP {
    companion object {

        private val sp: SharedPreferences =
            App.applicationContext().getSharedPreferences("remindMe", Context.MODE_PRIVATE)


        private const val DEFAULT_RING_PATH: String = "default_ring_path"
        fun getDefaultRingtonePath(): String {
            return sp.getString(DEFAULT_RING_PATH, "")!!
        }

        fun setDefaultRingtonePath(path: String) {
            sp.edit { putString(DEFAULT_RING_PATH, path) }
        }


        private const val IS_DEBUG_MODE: String = "is_debug_mode"
        fun getIsDebugMode(): Boolean {
            return sp.getBoolean(IS_DEBUG_MODE, false)
        }

        fun setIsDebugMode(isDebug: Boolean) {
            sp.edit { putBoolean(IS_DEBUG_MODE, isDebug) }
        }

    }
}