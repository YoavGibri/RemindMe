@file:Suppress("ArrayInDataClass")

package com.hirshler.remindme.model

import androidx.room.Entity
import androidx.room.PrimaryKey

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
)
