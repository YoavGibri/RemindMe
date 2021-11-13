@file:Suppress("ArrayInDataClass")

package com.hirshler.remindme.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) var id: Long? = null,
    var text: String? = null,
    var nextAlarmTime: Long = 0,
    var alerts: List<Alert>? = null,
    var delayInMinutes: Int? = null,
    var alertRingtonePath: String? = null,
    var voiceNotePath: String? = null,
    var repeat: Boolean = false,
    var weekly: Boolean = false,
    var isDismissed: Boolean = false,
) {
    companion object {
        const val KEY_REMINDER_ID = "reminderId"
    }

    fun initNextAlert() {
        val now = Calendar.getInstance().timeInMillis
        nextAlarmTime = alerts
            ?.sortedBy { a -> a.time }
            ?.find { alert -> alert.time > now }?.time ?: 0
    }
}
