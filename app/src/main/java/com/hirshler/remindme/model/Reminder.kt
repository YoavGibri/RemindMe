@file:Suppress("ArrayInDataClass")

package com.hirshler.remindme.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.hirshler.remindme.timeOfDayInMinutes
import java.util.*
import java.util.Calendar.*

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var text: String = "",
    var snooze: Int = 0,
    var alertRingtonePath: String = "",
    var voiceNotePath: String = "",
    var repeat: Boolean = false,
    var dismissed: Boolean = false,
    var weekDays: MutableMap<Int, Boolean> = initWeekDays(),
    var alarmTimeOfDay: Int = 0,
    var manualAlarm: Long = 0,
    var snoozeCount: Int = 0
) {

    val nextAlarmTime: Long
        get() = nextAlarm()

    companion object {
        const val KEY_REMINDER_ID = "reminderId"
    }


    private fun nextAlarm(): Long {
        if (repeat) {

            val now = Calendar.getInstance()
            val todayDayOfWeek = now.get(DAY_OF_WEEK)
            val nowTimeOfDayMinutes = now.timeOfDayInMinutes()
            val selectedWeekDays = weekDays.filter { it.value }.keys

            val nextWeekDay = (if (alarmTimeOfDay < nowTimeOfDayMinutes)
                selectedWeekDays.firstOrNull { day -> day > todayDayOfWeek }
            else
                selectedWeekDays.firstOrNull { day -> day >= todayDayOfWeek }
                    ) ?: selectedWeekDays.first()

            val nextAlarm = Calendar.getInstance()

            while (nextAlarm.get(DAY_OF_WEEK) != nextWeekDay) {
                nextAlarm.add(DAY_OF_YEAR, 1)
            }

            val hours = alarmTimeOfDay / 60
            val minutes = alarmTimeOfDay % 60
            nextAlarm.set(HOUR_OF_DAY, hours)
            nextAlarm.set(MINUTE, minutes)


            nextAlarm.add(MINUTE, snooze)

            return nextAlarm.timeInMillis
        }

        return manualAlarm
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}

fun initWeekDays(): MutableMap<Int, Boolean> {
    return mutableMapOf(
        SUNDAY to false,
        MONDAY to false,
        TUESDAY to false,
        WEDNESDAY to false,
        THURSDAY to false,
        FRIDAY to false,
        SATURDAY to false,
    )
}
