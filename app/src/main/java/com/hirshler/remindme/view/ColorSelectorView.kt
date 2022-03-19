package com.hirshler.remindme.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import com.hirshler.remindme.R
import com.hirshler.remindme.databinding.ColorSelectorViewBinding
import com.hirshler.remindme.managers.ThemeManager

class ColorSelectorView(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs), View.OnClickListener {

    private var onColorChanged: (() -> Unit)? = null
    private val pickers: MutableList<ImageButton>
    private val overlays: MutableList<Int>

    private var binding: ColorSelectorViewBinding =
        ColorSelectorViewBinding.inflate(LayoutInflater.from(context), null, false)

    init {
        addView(binding.root)

        pickers = mutableListOf(
            binding.black,
            binding.blue,
            binding.green,
            binding.grey,
            binding.pink,
            binding.red,
            binding.white,
            binding.puple
        )

        pickers.forEach { it.setOnClickListener(this) }

        overlays = mutableListOf(
            R.style.Theme_RemindMe_blackOverlay,
            R.style.Theme_RemindMe_blueOverlay,
//            R.style.greenOverlay,
//            R.style.greyOverlay,
//            R.style.pinkOverlay,
//            R.style.redOverlay,
//            R.style.whiteOverlay,
//            R.style.purpleOverlay,
        )


        pickers[overlays.indexOf(ThemeManager.color)].setImageResource(R.drawable.icon_v)

    }

    override fun onClick(v: View?) {
        if (ThemeManager.color != overlays[pickers.indexOf(v)]) {
            ThemeManager.color = overlays[pickers.indexOf(v)]
            onColorChanged?.invoke()
        }
    }

    fun setColorChangeListener(onColorChanged: () -> Unit) {
        this.onColorChanged = onColorChanged
    }


}