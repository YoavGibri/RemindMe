package com.hirshler.remindme.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.hirshler.remindme.R
import com.hirshler.remindme.databinding.ColorViewBinding

class ColorView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    val binding = ColorViewBinding.inflate((context as Activity).layoutInflater, this)

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ColorView)
        val background = attributes.getResourceId(R.styleable.ColorView_color_picker_background, 0)
        attributes.recycle()

        if (background != 0) {
            binding.button.setImageResource(background)
        }

        binding.button.setOnClickListener {
            this.callOnClick()
        }
    }


    fun setImageResource(res: Int) {
        binding.icon.setImageResource(res)
    }



}