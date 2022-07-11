package com.hirshler.remindme.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.RingtoneManager
import android.widget.ArrayAdapter
import androidx.appcompat.content.res.AppCompatResources
import com.hirshler.remindme.AppSettings
import com.hirshler.remindme.R
import com.hirshler.remindme.SP
import com.hirshler.remindme.managers.RingManager
import com.hirshler.remindme.model.AlarmSound

class SelectAlarmSoundDialog(val context: Activity, val fromScreen: FromScreen, val callback: (AlarmSound) -> Unit) {

    companion object {
        const val REQUEST_CODE_GENERAL_ALARM_SOUND: Int = 2354
        const val REQUEST_CODE_REMINDER_ALARM_SOUND: Int = 392567
    }

    private var dialog: AlertDialog? = null
    private val rm = RingManager(context)


    fun showDialog(alarmToSelect: AlarmSound? = null) {
        val list = SP.getAlarmSoundsList()
        val selectedAlarm = alarmToSelect ?: AppSettings.getGeneralAlarm()
        val checkedItem = list.withIndex().first { alarm -> alarm.value.stringUri == selectedAlarm.stringUri }.index

        val builder = AlertDialog.Builder(context)
            .setTitle(R.string.alarm_configuration)
            .setSingleChoiceItems(ArrayAdapter(context, android.R.layout.select_dialog_singlechoice, list), checkedItem)
            { d, w ->
                val selectedSound = list[(d as AlertDialog).listView.checkedItemPosition]
                rm.stop()
                if (selectedSound.stringUri.isNotEmpty()) {
                    rm.setRingPath(selectedSound.stringUri)
                    rm.play(vibrate = false)
                }
            }
            .setPositiveButton(R.string.ok) { dialog, which ->
                rm.stop()
                callback(list[(dialog as AlertDialog).listView.checkedItemPosition])
            }
            .setOnDismissListener { rm.stop() }


//        if (withBrowse) {
        builder.setNegativeButton(R.string.browse) { dialog, which ->
            rm.stop()
            goToSystemAlarmSounds()
        }
//        }
        dialog = builder.create()

        dialog?.window?.setBackgroundDrawable(AppCompatResources.getDrawable(context, R.drawable.round_corners_dialog_background))

        dialog?.show()
    }


    private fun goToSystemAlarmSounds() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        val requestCode = if (fromScreen == FromScreen.REMINDER) REQUEST_CODE_REMINDER_ALARM_SOUND else REQUEST_CODE_GENERAL_ALARM_SOUND
        context.startActivityForResult(intent, requestCode)
    }

    fun refresh(selectedAlarm: AlarmSound? = null) {
        dialog?.dismiss()
        showDialog(selectedAlarm)
    }

}

enum class FromScreen {
    REMINDER, SETTINGS
}
