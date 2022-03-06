package com.hirshler.remindme

import java.util.*

class TimeManager {
    companion object {

        fun setMinutes(minutes: Int): Calendar {
            return Calendar.getInstance().apply {
                add(Calendar.MINUTE, minutes)
                set(Calendar.SECOND, 0)
            }
        }

        fun setDays(days: Int, currentHours: Int, currentMinutes: Int): Calendar {
            return Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, currentHours)
                set(Calendar.MINUTE, currentMinutes)
                set(Calendar.SECOND, 0)
                add(Calendar.DAY_OF_YEAR, days)
            }
        }

        fun setDate(year: Int, monthOfYear: Int, dayOfMonth: Int, currentHours: Int, currentMinutes: Int): Calendar {
            return Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, currentHours)
                set(Calendar.MINUTE, currentMinutes)
                set(Calendar.SECOND, 0)
                set(year, monthOfYear, dayOfMonth)
            }
        }

        fun setTime(hourOfDay: Int, minute: Int, currentDay: Int): Calendar {
            return Calendar.getInstance().apply {
                set(Calendar.DAY_OF_YEAR, currentDay)
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }
        }


    }
}