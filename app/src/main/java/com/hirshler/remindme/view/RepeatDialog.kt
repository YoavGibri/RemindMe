package com.hirshler.remindme.view

import android.app.Activity
import android.app.AlertDialog
import com.hirshler.remindme.R
import com.hirshler.remindme.databinding.RepeatDialogLayoutBinding
import java.util.*

//, val callback: (MutableMap<Int, Boolean>) -> Unit

class RepeatDialog(private val activity: Activity, private val origWeekDays: MutableMap<Int, Boolean>) {

    private val dialogWeekDays = origWeekDays.toMutableMap()

    private val binding: RepeatDialogLayoutBinding = RepeatDialogLayoutBinding.inflate(activity.layoutInflater)

    private val dialog: AlertDialog = AlertDialog.Builder(activity)
        .setTitle(R.string.repeat_dialog_title)
        .setPositiveButton(R.string.repeat_dialog_button_positive) { dialog, which ->
            origWeekDays.clear()
            origWeekDays.putAll(dialogWeekDays)
        }
        .setNegativeButton(R.string.repeat_dialog_button_negative, null)
//        .setNeutralButton(R.string.repeat_dialog_button_neutral, this)
        .setView(binding.root)
        .create()


    fun show() {
        // set today as default:
//        val today = Calendar.getInstance()[Calendar.DAY_OF_WEEK]
//        dialogWeekDays[today] = true

        binding.sunday.isChecked = dialogWeekDays[1] == true
        binding.monday.isChecked = dialogWeekDays[2] == true
        binding.tuesday.isChecked = dialogWeekDays[3] == true
        binding.wednesday.isChecked = dialogWeekDays[4] == true
        binding.thursday.isChecked = dialogWeekDays[5] == true
        binding.friday.isChecked = dialogWeekDays[6] == true
        binding.saturday.isChecked = dialogWeekDays[7] == true

        binding.sunday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[1] = isChecked }
        binding.monday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[2] = isChecked }
        binding.tuesday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[3] = isChecked }
        binding.wednesday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[4] = isChecked }
        binding.thursday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[5] = isChecked }
        binding.friday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[6] = isChecked }
        binding.saturday.setOnCheckedChangeListener { _, isChecked -> dialogWeekDays[7] = isChecked }

        dialog.show()
    }

//    override fun onClick(dialog: DialogInterface?, which: Int) {
//        when (which) {
//
//            //Done
//            Dialog.BUTTON_POSITIVE -> {
//                origWeekDays.clear()
//                origWeekDays.putAll(dialogWeekDays)
//            }
//
////            //Reset
////            Dialog.BUTTON_NEGATIVE -> {
////                origWeekDays.forEach { (it as MutableMap.MutableEntry).setValue(false) }
////            }
//
//        }
//
//    }

}