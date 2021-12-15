package com.hirshler.remindme.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.hirshler.remindme.R

class ToggleButtonMute(context: Context, attrs: AttributeSet?) : AppCompatImageButton(context, attrs) {

    private var playbackOn = true

    var callback: ((Boolean) -> Unit)? = null

    fun setOnToggleCallback(callback: ((Boolean) -> Unit)?) {
        this.callback = callback;
    }

    init {

        setOnClickListener {
            playbackOn = !playbackOn
            setImageResource(
                if (playbackOn) R.drawable.ic_baseline_volume_off_24
                else R.drawable.ic_baseline_volume_up_24
            )
            callback?.invoke(playbackOn)
        }
    }

}