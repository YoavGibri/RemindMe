package com.hirshler.remindme.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewTreeLifecycleOwner
import com.hirshler.remindme.R
import java.util.*

@SuppressLint("AppCompatCustomView")
class ToggleButtonDays(context: Context, attrs: AttributeSet?) :
    Button(context, attrs) {

    private var currDays = MutableLiveData<Int>(0)
    var callback: ((Int) -> Unit)? = null


    fun setOnToggleCallback(callback: ((Int) -> Unit)?) {
        this.callback = callback;
    }


    init {
        setOnClickListener {
            if (currDays.value == 6)
                currDays.value = 0
            else {
                currDays.value = (currDays.value ?: 0) + 1
            }
        }

        currDays.observe(context as LifecycleOwner, { days ->
            text = getDayDesByDays(days)
            callback?.invoke(days)
        })
    }

    private fun getDayDesByDays(days: Int): String {
        val today: Calendar = Calendar.getInstance()
        when (days) {
            0 -> return "Today"
            1 -> return "Tomorrow"
            else -> {
                today.add(Calendar.DAY_OF_MONTH, days)
                when (today.get(Calendar.DAY_OF_WEEK)) {
                    1 -> return context.getString(R.string.sunday)
                    2 -> return context.getString(R.string.monday)
                    3 -> return context.getString(R.string.tuesday)
                    4 -> return context.getString(R.string.wednesday)
                    5 -> return context.getString(R.string.thursday)
                    6 -> return context.getString(R.string.friday)
                    7 -> return context.getString(R.string.saturday)
                }
            }
        }
        return ""
    }


}