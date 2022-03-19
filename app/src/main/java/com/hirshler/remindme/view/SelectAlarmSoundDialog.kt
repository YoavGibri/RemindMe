package com.hirshler.remindme.view

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.media.RingtoneManager
import android.widget.ArrayAdapter
import com.hirshler.remindme.AppSettings
import com.hirshler.remindme.R
import com.hirshler.remindme.SP
import com.hirshler.remindme.managers.RingManager
import com.hirshler.remindme.model.AlarmSound

class SelectAlarmSoundDialog(val context: Activity, val callback: (AlarmSound) -> Unit) {

    companion object {
        const val REQUEST_CODE_GENERAL_ALARM_SOUND: Int = 2354
    }

    private var dialog: AlertDialog? = null
    private val rm = RingManager(context)


    fun showGeneral() {
        showDialog(true)
    }

    fun showSpecific() {
        showDialog(false)
    }

    private fun showDialog(withBrowse: Boolean = false) {
        val list = SP.getAlarmSoundsList()
        val generalAlarmSound = AppSettings.getGeneralAlarm()
        val checkedItem = list.indexOf(generalAlarmSound)

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


        if (withBrowse) {
            builder.setNegativeButton(R.string.browse) { dialog, which ->
                rm.stop()
                goToSystemAlarmSounds()
            }
        }
        dialog = builder.create()

        dialog?.show()
    }


    private fun goToSystemAlarmSounds() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER)
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
        context.startActivityForResult(intent, REQUEST_CODE_GENERAL_ALARM_SOUND)
    }

    fun refresh() {
        dialog?.dismiss()
        showGeneral()
    }

}