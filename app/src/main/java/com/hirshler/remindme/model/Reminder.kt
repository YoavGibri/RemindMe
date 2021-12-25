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
    var alertRingtonePath: String = "",
    var voiceNotePath: String = "",
    var dismissed: Boolean = false,
    var weekDays: MutableMap<Int, Boolean> = initWeekDays(),
    var alarmTimeOfDay: Int = 0,
    var manualAlarm: Long = 0,
    var snooze: Int = 0,
    var snoozeCount: Int = 0,
    var isListTitle:Boolean = false
) {
    companion object {
        const val KEY_REMINDER_ID = "reminderId"
    }

    var repeat: Boolean
        get() = weekDays.any { day -> day.value }
        set(value) {}


//    val nextAlarmTimeWithSnooze: Long
//        get() = nextAlarmWithSnooze()
//
//
//    private fun nextAlarmWithSnooze(): Long {
//        return nextAlarm(snooze)
//    }


//    val nextAlarmTime: Long
//        get() = nextAlarm()

    fun nextAlarm(): Long = nextAlarm(0)
    fun nextAlarmWithSnooze(): Long = nextAlarm(snooze)


    private fun nextAlarm(thisSnooze: Int = 0): Long {
        if (repeat) {

            val nextWeekDay = getNextDay(thisSnooze)

            val nextAlarm = Calendar.getInstance()

            while (nextAlarm.get(DAY_OF_WEEK) != nextWeekDay) {
                nextAlarm.add(DAY_OF_YEAR, 1)
            }

            val hours = alarmTimeOfDay / 60
            val minutes = alarmTimeOfDay % 60
            nextAlarm.set(HOUR_OF_DAY, hours)
            nextAlarm.set(MINUTE, minutes)

            return nextAlarm.timeInMillis + (thisSnooze * 60000)
        }

        return manualAlarm + (thisSnooze * 60000)
    }

    private fun getNextDay(snooze: Int): Int {
        val now = Calendar.getInstance()
        val todayDayOfWeek = now.get(DAY_OF_WEEK)
        val nowTimeOfDayMinutes = now.timeOfDayInMinutes()

        val selectedWeekDays = weekDays.filter { it.value == true }.keys

        if (selectedWeekDays.contains(todayDayOfWeek))
            if (nowTimeOfDayMinutes < alarmTimeOfDay + snooze)
                return todayDayOfWeek

        var nextAlarmDay = selectedWeekDays.firstOrNull { day -> day > todayDayOfWeek }
        if (nextAlarmDay == null) {
            nextAlarmDay = selectedWeekDays.firstOrNull { day -> day > 0 }
        }

        return nextAlarmDay!!
    }

    fun lastAlarm(): Long {
        if (repeat) {

            val nextWeekDay = getLastDay()

            val nextAlarm = Calendar.getInstance()

            while (nextAlarm.get(DAY_OF_WEEK) != nextWeekDay) {
                nextAlarm.add(DAY_OF_YEAR, -1)
            }

            val hours = alarmTimeOfDay / 60
            val minutes = alarmTimeOfDay % 60
            nextAlarm.set(HOUR_OF_DAY, hours)
            nextAlarm.set(MINUTE, minutes)

            return nextAlarm.timeInMillis
        }

        return manualAlarm
    }

    private fun getLastDay(): Int {
        val now = Calendar.getInstance()
        val todayDayOfWeek = now.get(DAY_OF_WEEK)
        val nowTimeOfDayMinutes = now.timeOfDayInMinutes()

        val selectedWeekDays = weekDays.filter { it.value == true }.keys.reversed()

        if (selectedWeekDays.contains(todayDayOfWeek))
            if (nowTimeOfDayMinutes > alarmTimeOfDay)
                return todayDayOfWeek

        var lastAlarmDay = selectedWeekDays.firstOrNull { day -> day < todayDayOfWeek }
        if (lastAlarmDay == null) {
            lastAlarmDay = selectedWeekDays.firstOrNull { day -> day < 8 }
        }

        return lastAlarmDay!!
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


