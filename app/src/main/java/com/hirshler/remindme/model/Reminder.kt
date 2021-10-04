@file:Suppress("ArrayInDataClass")

package com.hirshler.remindme.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    var text: String? = null,
    var voiceNotePath: String? = null,
    var alerts: List<Alert>? = null,
    var delayInMinutes: Int? = null,
    var alertRingtonePath: String? = null
)
