package com.hirshler.remindme.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.hirshler.remindme.R
import com.hirshler.remindme.databinding.ColorSelectorViewBinding
import com.hirshler.remindme.managers.ThemeManager

class ColorSelectorView : ConstraintLayout, View.OnClickListener {

    private var onColorChanged: (() -> Unit)? = null
    private lateinit var pickers: MutableList<ImageButton>
    private lateinit var overlays: MutableList<Int>


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    private fun initView() {
        val binding = ColorSelectorViewBinding.inflate(LayoutInflater.from(context), this)

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
            R.style.Theme_RemindMe_greenOverlay,
            R.style.Theme_RemindMe_greyOverlay,
            R.style.Theme_RemindMe_pinkOverlay,
            R.style.Theme_RemindMe_redOverlay,
            R.style.Theme_RemindMe_whiteOverlay,
            R.style.Theme_RemindMe_purpleOverlay,
        )

        setSelectedColor()
    }

    private fun setSelectedColor() {
        if(!isInEditMode) {
            pickers[overlays.indexOf(ThemeManager.getThemeColor())].setImageResource(R.drawable.icon_v)
        }
    }


    override fun onClick(v: View?) {
        if (ThemeManager.getThemeColor() != overlays[pickers.indexOf(v)]) {
            ThemeManager.setThemeColor(overlays[pickers.indexOf(v)])
            onColorChanged?.invoke()
        }
    }

    fun setColorChangeListener(onColorChanged: () -> Unit) {
        this.onColorChanged = onColorChanged
    }


}