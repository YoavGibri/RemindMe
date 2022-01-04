package com.hirshler.remindme.view

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.SoundEffectConstants
import android.widget.TimePicker

class TimePickerDialog(context: Context?, themeResId: Int, listener: OnTimeSetListener?, hourOfDay: Int, minute: Int, is24HourView: Boolean) :
    TimePickerDialog(context, themeResId, listener, hourOfDay, minute, is24HourView) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onTimeChanged(view: TimePicker?, hourOfDay: Int, minute: Int) {
        view?.playSoundEffect(SoundEffectConstants.CLICK)
    }



}