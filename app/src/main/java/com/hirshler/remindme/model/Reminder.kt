@file:Suppress("ArrayInDataClass")

package com.hirshler.remindme.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*
import java.util.Calendar.*

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var text: String? = null,
    var snooze: Int? = null,
    var alertRingtonePath: String? = null,
    var voiceNotePath: String? = null,
    var repeat: Boolean = false,
    var dismissed: Boolean = false,
    var weekDays: MutableMap<String, Boolean> = initWeekDays(),
    var manualAlarm: Long
) {

//    val nextAlarmTime: Long
//        get() = nextAlarm()

    companion object {
        const val KEY_REMINDER_ID = "reminderId"
    }


    private fun nextAlarm(): Long {
        val now = Calendar.getInstance().timeInMillis
        if (repeat) {
            weekDays.filter {  }
        }
// nextalarm is by next true day, or if no days are true so all days are true so take today or tomorrow(by time), or the the onTime value
        if (manualAlarm != 0L) return manualAlarm



    }

}

fun initWeekDays(): MutableMap<Int, Boolean> {
    return mutableMapOf(
        SUNDAY to false,
        MONDAY to false,
        TUESDAY to false,
        WEDNESDAY to false,
        TUESDAY to false,
        FRIDAY to false,
        SATURDAY to false,
    )
}
