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

    private var lastHour: Int = -1
    private var lastMinute: Int = -1

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
//            val pickerLayout = ((getChildAt(0) as ViewGroup).getChildAt(0) as ViewGroup)
//            val hourPicker = pickerLayout.getChildAt(0) as NumberPicker
//            val hourPickerInput = hourPicker.getChildAt(0) as EditText
//            val minutesPicker = pickerLayout.getChildAt(2) as NumberPicker
//            val minutesPickerInput = minutesPicker.getChildAt(0) as EditText
//
//            hourPickerInput.apply {
//                doOnTextChanged { text, start, before, count ->
//                    text?.let {
//                        if (it.length == 2 && count == 1) {
//                            minutesPickerInput.requestFocus()
////                        val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
////                        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
//                        }
//                    }
//                }
//
//                setOnFocusChangeListener { v, hasFocus ->
//                    if (hasFocus) (v as EditText).selectAll()
//                }
//            }
//
//
//            minutesPickerInput.apply {
//                doOnTextChanged { text, start, before, count ->
//                    text?.let {
//                        if (it.length == 2 && count == 1) {
//                            minutesPickerInput.onEditorAction(EditorInfo.IME_ACTION_DONE)
//                        }
//                    }
//                }
//            }
//
//
//
//
//
//
//            setIs24HourView(true)
//
//            setOnTimeChangedListener { view, hourOfDay, minute ->
//
//                if (lastHour != -1 && this.hour != lastHour && this.minute != lastMinute) {
//                    this.hour = lastHour
//                } else {
//                    view?.playSoundEffect(SoundEffectConstants.CLICK)
//                    performHapticFeedback(
//                        HapticFeedbackConstants.VIRTUAL_KEY,
//                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
//                    )
//                }
//
//                lastHour = this.hour
//                lastMinute = this.minute
//            }


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