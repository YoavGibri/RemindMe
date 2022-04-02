package com.hirshler.remindme.view

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.hirshler.remindme.R
import com.hirshler.remindme.databinding.RepeatDialogLayoutBinding


class RepeatDialog(private val activity: Activity, private val origWeekDays: MutableMap<Int, Boolean>) {

    private val dialogWeekDays = origWeekDays.toMutableMap()

    private val binding: RepeatDialogLayoutBinding = RepeatDialogLayoutBinding.inflate(activity.layoutInflater)

    private val dialog = AlertDialog.Builder(activity)
        .setTitle(R.string.repeat_dialog_title)
        .setView(binding.root)
        .create()


    fun show() {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.apply {

            done.setOnClickListener {
                origWeekDays.clear()
                origWeekDays.putAll(dialogWeekDays)
                dialog.dismiss()
            }

            cancel.setOnClickListener {
                dialog.dismiss()
            }


            sunday.isChecked = dialogWeekDays[1] == true
            monday.isChecked = dialogWeekDays[2] == true
            tuesday.isChecked = dialogWeekDays[3] == true
            wednesday.isChecked = dialogWeekDays[4] == true
            thursday.isChecked = dialogWeekDays[5] == true
            friday.isChecked = dialogWeekDays[6] == true
            saturday.isChecked = dialogWeekDays[7] == true

            sunday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[1] = isChecked }
            monday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[2] = isChecked }
            tuesday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[3] = isChecked }
            wednesday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[4] = isChecked }
            thursday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[5] = isChecked }
            friday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[6] = isChecked }
            saturday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[7] = isChecked }

        }
        dialog.show()
    }


}