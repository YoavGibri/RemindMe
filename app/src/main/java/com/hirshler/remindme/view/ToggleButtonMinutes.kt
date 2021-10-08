package com.hirshler.remindme.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewTreeLifecycleOwner

@SuppressLint("AppCompatCustomView")
class ToggleButtonMinutes(context: Context, attrs: AttributeSet?) :
    AppCompatButton(context, attrs) {

    var callback: ((Int) -> Unit)? = null
    var disabled = false

    fun setOnToggleCallback(callback: ((Int) -> Unit)?) {
        this.callback = callback;
    }

    fun disable() {
        disabled = true
        text = "-"
    }

    private val minutes: List<Int> = listOf(5, 10, 15, 20, 25, 30, 45, 60, 90, 120, 180)
    var currMinutes = MutableLiveData<Int>(minutes[0])


    init {
        currMinutes.observe(context as LifecycleOwner, { minutes ->
            text = minutes.toString()
            callback?.invoke(minutes)
        })
        setOnClickListener {
            val currentIndex = if (disabled) {
                disabled = !disabled
                -1
            } else minutes.indexOf(currMinutes.value)

            val nextIndex = if (currentIndex + 1 == minutes.size) 0 else currentIndex + 1
            currMinutes.value = minutes[nextIndex]
        }
    }


}