package com.hirshler.remindme

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import androidx.annotation.StringRes


fun View.flash(duration: Long) {
    val anim: Animation = AlphaAnimation(0.0f, 1.0f)
    anim.duration = duration //You can manage the blinking time with this parameter
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
    Toast.makeText(App.applicationContext(), toastText, duration).show()
}
