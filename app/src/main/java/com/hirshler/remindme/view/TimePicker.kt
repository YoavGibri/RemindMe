package com.hirshler.remindme.view

import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.core.widget.doOnTextChanged

class TimePicker(context: Context?, attrs: AttributeSet?) : TimePicker(context, attrs) {
    private var lastHour: Int = -1
    private var lastMinute: Int = -1

    init {

        val pickerLayout = ((getChildAt(0) as ViewGroup).getChildAt(0) as ViewGroup)
        val hourPicker = pickerLayout.getChildAt(0) as NumberPicker
        val hourPickerInput = hourPicker.getChildAt(0) as EditText
        val minutesPicker = pickerLayout.getChildAt(2) as NumberPicker
        val minutesPickerInput = minutesPicker.getChildAt(0) as EditText

        hourPickerInput.apply {
            doOnTextChanged { text, start, before, count ->
                text?.let {
                    if (it.length == 2 && count == 1) {
                        minutesPickerInput.requestFocus()
                    }
                }
            }

            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus)
                    (v as EditText).selectAll()
                else {
                    val newHour = hourPickerInput.text.toString()
                    if (newHour != "") {
                        hour = newHour.toInt()
                    }
                }
            }
        }


        minutesPickerInput.apply {
            doOnTextChanged { text, start, before, count ->
                text?.let {
                    if (it.length == 2 && count == 1) {
                        onEditorAction(EditorInfo.IME_ACTION_DONE)
                    }
                }
            }

        }


        setIs24HourView(true)

        setOnTimeChangedListener { view, hourOfDay, minute ->

            if (lastHour != -1 && this.hour != lastHour && this.minute != lastMinute) {
                this.hour = lastHour
            } else {
                view?.playSoundEffect(SoundEffectConstants.CLICK)
                performHapticFeedback(
                    HapticFeedbackConstants.VIRTUAL_KEY,
                    HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                )
            }

            lastHour = this.hour
            lastMinute = this.minute
        }
    }


}