package com.hirshler.remindme.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.hirshler.remindme.R
import com.hirshler.remindme.pxToDp
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ToggleButtonDays(context: Context, attrs: AttributeSet?) :
    AppCompatButton(context, attrs) {

    private var initialTextSize = context.pxToDp(textSize).toFloat()
    private var currDays = MutableLiveData<Int>(0)
    private var callback: ((Int) -> Unit)? = null
    private var date = MutableLiveData<Calendar?>(null)


    fun setOnToggleCallback(callback: ((Int) -> Unit)?) {
        this.callback = callback;
    }


    init {
        setOnClickListener {
            if (date.value != null) {
                date.value = null
                currDays.value = 0
            } else if (currDays.value == 6)
                currDays.value = 0
            else {
                currDays.value = (currDays.value ?: 0) + 1
            }
        }

        currDays.observe(context as LifecycleOwner, { days ->
            text = getDayDesByDays(days)
            callback?.invoke(days)
        })

        date.observe(context as LifecycleOwner, { date ->

            if (date != null) {
                val today = Calendar.getInstance()
                val days = TimeUnit.DAYS.convert(date.time.time - today.time.time, TimeUnit.MILLISECONDS).toInt()
                var dayDes = getDayDesByDays(days)
                if (days > 6) {
                    dayDes += " ${SimpleDateFormat("dd/MM", Locale.getDefault()).format(date.time)}"
//                    textSize = initialTextSize - 8
                } else {
//                    textSize = initialTextSize
                }
                text = dayDes
            }
//                text = SimpleDateFormat("dd/MM", Locale.getDefault()).format(date.time)
        })
    }

    private fun getDayDesByDays(days: Int): String {
        val today: Calendar = Calendar.getInstance()
        return when (days) {
            0 -> "Today"
            1 -> "Tomorrow"
            else -> {
                today.add(Calendar.DAY_OF_MONTH, days)
                var dayDes = getDayDesByDate(today)
                if (days > 6) dayDes = dayDes.substring(0, 2)
                return dayDes
            }
        }

    }

    private fun getDayDesByDate(day: Calendar): String {
        when (day.get(Calendar.DAY_OF_WEEK)) {
            1 -> return context.getString(R.string.sunday)
            2 -> return context.getString(R.string.monday)
            3 -> return context.getString(R.string.tuesday)
            4 -> return context.getString(R.string.wednesday)
            5 -> return context.getString(R.string.thursday)
            6 -> return context.getString(R.string.friday)
            7 -> return context.getString(R.string.saturday)
        }
        return SimpleDateFormat("dd/MM", Locale.getDefault()).format(day.time)
    }

    fun setDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        date.value = Calendar.getInstance().apply { set(year, monthOfYear, dayOfMonth) }

    }

    fun setDate(cal: Calendar) {
        date.value = cal

    }


}