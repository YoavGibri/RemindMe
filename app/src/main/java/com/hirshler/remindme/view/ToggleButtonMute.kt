package com.hirshler.remindme.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.hirshler.remindme.R

class ToggleButtonMute(context: Context, attrs: AttributeSet?) :
    AppCompatImageButton(context, attrs) {

    var playback = MutableLiveData(true)

    var callback: ((Boolean) -> Unit)? = null
    fun setOnToggleCallback(callback: ((Boolean) -> Unit)?) {
        this.callback = callback;
    }

    init {

        playback.observe(context as LifecycleOwner, { on ->
            setImageResource(
                if (on) R.drawable.ic_baseline_volume_off_24
                else R.drawable.ic_baseline_volume_up_24
            )
            callback?.invoke(on)
        })

        setOnClickListener {
            playback.value = !playback.value!!
        }
    }

}