package com.hirshler.remindme.view

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.hirshler.remindme.R
import com.hirshler.remindme.databinding.TimePickerDialogBinding
import java.util.*


class TimePickerDialog(
    context: Context,
    private val calendar: Calendar,
    private val callback: (hour: Int, minute: Int) -> Unit
) : AlertDialog(
    context
), DialogInterface.OnClickListener {

    val binding = TimePickerDialogBinding.inflate(LayoutInflater.from(getContext()), null, false)

    init {
        val themeContext = getContext()
        setView(binding.root)
        setButton(BUTTON_POSITIVE, themeContext.getString(R.string.ok), this)
        setButton(BUTTON_NEGATIVE, themeContext.getString(R.string.cancel), this)


        binding.timePicker.apply {
            hour = calendar.get(Calendar.HOUR_OF_DAY)
            minute = calendar.get(Calendar.MINUTE)
        }


    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                val time = binding.timePicker
                callback(time.hour, time.minute)
            }
        }
    }


}