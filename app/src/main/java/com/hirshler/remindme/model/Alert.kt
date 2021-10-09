package com.hirshler.remindme.model


data class Alert(
    var id: Int,
    var time: Long,
    var isRepeat: Boolean = false
)
