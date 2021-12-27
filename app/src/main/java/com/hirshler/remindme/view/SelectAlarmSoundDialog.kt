package com.hirshler.remindme.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.RingtoneManager
import android.widget.ArrayAdapter
import com.hirshler.remindme.AppSettings
import com.hirshler.remindme.R
import com.hirshler.remindme.RingManager
import com.hirshler.remindme.SP
import com.hirshler.remindme.model.AlarmSound
import com.hirshler.remindme.ui.settings.SettingsFragment

class SelectAlarmSoundDialog(val context: Activity, val callback: (AlarmSound) -> Unit) {


    private var dialog: AlertDialog? = null
    private val rm = RingManager(context)


    fun show() {
        val list = SP.getAlarmSoundsList()
        val generalAlarmSound = AppSettings.getGeneralAlarm()
        val checkedItem = list.indexOf(generalAlarmSound)

        dialog = AlertDialog.Builder(context)
            .setTitle(R.string.alarm_configuration)
            .setSingleChoiceItems(ArrayAdapter(context, R.layout.alarm_sound_dialog_item, list), checkedItem)
            { d, w ->
                val selectedSound = (dialog as AlertDialog).listView.selectedItem as AlarmSound
                rm.setRingPath(selectedSound.uri)
                rm.play(vibrate = false)
            }
            .setPositiveButton(R.string.ok) { dialog, which ->
                rm.pause()
                callback((dialog as AlertDialog).listView.selectedItem as AlarmSound)
            }
            .setNegativeButton(R.string.browse) { dialog, which ->
                rm.pause()
                goToSystemAlarmSounds()
            }
            .create()

        dialog?.show()
    }


    private fun goToSystemAlarmSounds() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        context.startActivityForResult(intent, SettingsFragment.REQUEST_CODE_GENERAL_ALARM_SOUND)
    }

    fun refresh() {
        dialog?.dismiss()
        show()
    }

}