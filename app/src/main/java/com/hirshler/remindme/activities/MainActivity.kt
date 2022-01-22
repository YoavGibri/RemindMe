package com.hirshler.remindme.activities

import android.app.AlarmManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.hirshler.remindme.App
import com.hirshler.remindme.AppSettings
import com.hirshler.remindme.BuildConfig
import com.hirshler.remindme.StateAdapter
import com.hirshler.remindme.databinding.ActivityMainBinding
import com.hirshler.remindme.model.AlarmSound
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.view.SelectAlarmSoundDialog.Companion.REQUEST_CODE_GENERAL_ALARM_SOUND

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val ON_ACTIVITY_START_GO_TO_REMINDERS_LIST: String = "onActivityStartGoToRemindersList"
    }


    override fun setTheme(resId: Int) {
        Log.d("TAG", "MainActivity: theme: $resId")
        super.setTheme(resId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reminder = intent.getStringExtra("reminderToEdit")?.let { Gson().fromJson(it, Reminder::class.java) }

        binding.viewPager.adapter = StateAdapter(this, reminder)
        binding.dotsIndicator.setViewPager2(binding.viewPager)
        binding.viewPager.offscreenPageLimit = 2
        val goToList = intent.getBooleanExtra(ON_ACTIVITY_START_GO_TO_REMINDERS_LIST, false)
        if (goToList) {
            goToRemindersList()
        }
    }

    fun goToRemindersList() {
        binding.viewPager.setCurrentItem(1, true)
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            if (!Settings.canDrawOverlays(this)) {
                AlertDialog.Builder(this)
                    .setTitle("Allow Permission")
                    .setMessage("You are using Android version 10 or grater.\nWithout \"display on other apps\" permission the reminders will not show")
                    .setPositiveButton("grant permission") { _, _ ->
                        val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        startActivity(myIntent)
                    }
                    .setNegativeButton("leave app") { _, _ -> finish() }
                    .setCancelable(false)
                    .show()
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            if (!(App.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()) {
                AlertDialog.Builder(this)
                    .setTitle("Allow Permission")
                    .setMessage("You are using Android version 12 or grater.\nWithout \"Alarms & Reminders\" permission the reminders will not show")
                    .setPositiveButton("grant permission") { _, _ ->
                        val myIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        startActivity(myIntent)
                    }
                    .setNegativeButton("leave app") { _, _ -> finish() }
                    .setCancelable(false)
                    .show()
            }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_GENERAL_ALARM_SOUND) {
            val uri = data?.getParcelableExtra<Uri>(EXTRA_RINGTONE_PICKED_URI)
            uri?.let {
                val newSound = AlarmSound(uri.toString())
                AppSettings.addSoundToAlarmSounds(newSound)
                AppSettings.setGeneralAlarm(newSound)
            }

            (binding.viewPager.adapter as StateAdapter)?.settingsFragment.refreshAlarmSoundsDialog()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onAttachedToWindow() {
        if (BuildConfig.DEBUG) {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
        }
    }

    override fun onBackPressed() {
        if (binding.viewPager.currentItem != 0) {
            binding.viewPager.setCurrentItem(0, true)
        } else
            super.onBackPressed()
    }


}