package com.hirshler.remindme

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.Toast
import androidx.annotation.StringRes
import java.text.SimpleDateFormat
import java.util.*


fun View.flash(duration: Long) {
    val anim: Animation = AlphaAnimation(0.0f, 1.0f)
    anim.duration = duration //You can manage the blinking time with this parameter
    anim.startOffset = 20
    anim.repeatMode = Animation.REVERSE
    anim.repeatCount = Animation.INFINITE

    startAnimation(anim)
}

fun View.beat(duration: Long) {
    val anim = ScaleAnimation(0.5F, 1.0F, 0.5F, 1.0F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F)
    anim.duration = duration
    anim.startOffset = 20
    anim.repeatMode = Animation.REVERSE
    anim.repeatCount = Animation.INFINITE

    startAnimation(anim)
}

fun Toast?.showInDebug(text: String = "", @StringRes resId: Int = 0, duration: Int = Toast.LENGTH_SHORT) {
    if (BuildConfig.DEBUG) {
        show(text, resId, duration)
    }
}

fun Toast?.show(text: String = "", @StringRes resId: Int = 0, duration: Int = Toast.LENGTH_LONG) {
    val toastText = if (resId != 0) App.applicationContext().getString(resId) else text
    Toast.makeText(App.applicationContext(), toastText, duration)
        .apply {
            setGravity(Gravity.CENTER, 0, 0)
            show()
        }
}


fun Calendar.timeOfDayInMinutes(): Int {
    return (this.get(Calendar.HOUR_OF_DAY) * 60) + this.get(Calendar.MINUTE)
}

fun Calendar.setTimeOfDay(timeOfDay: Int) {
    val hours = timeOfDay / 60
    val minutes = timeOfDay % 60
    set(Calendar.HOUR_OF_DAY, hours)
    set(Calendar.MINUTE, minutes)
}

fun Calendar.format(pattern: String): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this.time)
}

fun Calendar.cloneCalendar(): Calendar {
    val newCal = Calendar.getInstance()
    newCal.timeInMillis = timeInMillis
    return newCal
}

fun Calendar.setTimeInMillis(timeInMillis: Long?): Calendar {
    return apply { this.timeInMillis = timeInMillis ?: 0 }
}

fun Context.dpToPx(dp: Int): Float {
    return (dp * resources.displayMetrics.density)
}

fun Context.pxToDp(px: Float): Int {
    return (px / resources.displayMetrics.density).toInt()
}

val Float.asDp: Int get() = (this / App.applicationContext().resources.displayMetrics.density).toInt()
