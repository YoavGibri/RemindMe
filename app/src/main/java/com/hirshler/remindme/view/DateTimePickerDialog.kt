package com.hirshler.remindme.view

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import androidx.appcompat.app.AlertDialog
import com.hirshler.remindme.R
import com.hirshler.remindme.databinding.DateTimePickerDialogBinding
import java.util.*

class DateTimePickerDialog(
    context: Context,
    private val calendar: Calendar,
    private val callback: (year: Int, month: Int, dayOfMonth: Int, hour: Int, minute: Int) -> Unit
) : AlertDialog(
    context
), DialogInterface.OnClickListener {

    val binding = DateTimePickerDialogBinding.inflate(LayoutInflater.from(getContext()), null, false)

    init {
        val themeContext = getContext()
        setView(binding.root)
        setButton(BUTTON_POSITIVE, themeContext.getString(R.string.ok), this)
        setButton(BUTTON_NEGATIVE, themeContext.getString(R.string.cancel), this)

        binding.datePicker.apply {
            minDate = calendar.timeInMillis

            init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                    view?.playSoundEffect(SoundEffectConstants.CLICK)
                    performHapticFeedback(
                        HapticFeedbackConstants.CLOCK_TICK,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    )
                }
            }


        }

        binding.timePicker.apply {
            hour = calendar.get(Calendar.HOUR_OF_DAY)
            minute = calendar.get(Calendar.MINUTE)
        }


    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                val date = binding.datePicker
                val time = binding.timePicker
                callback(date.year, date.month, date.dayOfMonth, time.hour, time.minute)
            }
        }
    }


}