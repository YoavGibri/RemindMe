package com.hirshler.remindme

import java.util.*

class TimeManager {
    companion object {

        fun setMinutes(minutes: Int): Calendar {
            return Calendar.getInstance().apply {
                add(Calendar.MINUTE, minutes)
            }
        }

        fun setDays(days: Int, currentMinutes: Int): Calendar {
            return Calendar.getInstance().apply {
                set(Calendar.MINUTE, currentMinutes)
                add(Calendar.DAY_OF_YEAR, days)
            }
        }

        fun setDate(year: Int, monthOfYear: Int, dayOfMonth: Int, currentMinutes: Int): Calendar {
            return Calendar.getInstance().apply {
                set(Calendar.MINUTE, currentMinutes)
                set(year, monthOfYear, dayOfMonth)
            }
        }

        fun setTime(hourOfDay: Int, minute: Int, currentDay: Int): Calendar {
            return Calendar.getInstance().apply {
                set(Calendar.DAY_OF_YEAR, currentDay)
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
        }


    }
}