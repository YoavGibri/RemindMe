package com.hirshler.remindme.activities

import android.content.Intent
import android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.isVisible
import com.google.android.gms.ads.AdRequest
import com.google.gson.Gson
import com.hirshler.remindme.AppSettings
import com.hirshler.remindme.BuildConfig
import com.hirshler.remindme.StateAdapter
import com.hirshler.remindme.databinding.ActivityMainBinding
import com.hirshler.remindme.model.AlarmSound
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.ui.infosplash.SplashImagesAdapter
import com.hirshler.remindme.view.SelectAlarmSoundDialog.Companion.REQUEST_CODE_GENERAL_ALARM_SOUND
import com.hirshler.remindme.view.SelectAlarmSoundDialog.Companion.REQUEST_CODE_REMINDER_ALARM_SOUND
import com.hirshler.remindme.view.UserNameDialog

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        const val ON_ACTIVITY_START_GO_TO_REMINDERS_LIST: String = "onActivityStartGoToRemindersList"
        const val ON_ACTIVITY_START_GO_TO_SETTINGS: String = "onActivityStartGoToSettings"
        const val REMINDER_TO_EDIT: String = "reminderToEdit"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (AppSettings.firstLaunch()) {
            binding.splashPager.isVisible = true
            binding.splashPager.adapter = SplashImagesAdapter(this) {
                when (it) {
                    0 -> binding.splashPager.setCurrentItem(1, true)
                    else -> binding.splashPager.isVisible = false
                }
            }
        }

        val reminder = intent.getStringExtra(REMINDER_TO_EDIT)?.let { Gson().fromJson(it, Reminder::class.java) }

        binding.viewPager.adapter = StateAdapter(this, reminder)
        binding.dotsIndicator.setViewPager2(binding.viewPager)
        binding.viewPager.offscreenPageLimit = 2


        val goToList = intent.getBooleanExtra(ON_ACTIVITY_START_GO_TO_REMINDERS_LIST, false)
        if (goToList) {
            binding.viewPager.setCurrentItem(1, true)
        }

        val goToSettings = intent.getBooleanExtra(ON_ACTIVITY_START_GO_TO_SETTINGS, false)
        if (goToSettings) {
            binding.viewPager.setCurrentItem(2, false)
        }



        if (BuildConfig.DEBUG && AppSettings.getUserName() == "") {
            UserNameDialog.showUserNameDialog(this)
        }

        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_GENERAL_ALARM_SOUND -> {
                val uri = data?.getParcelableExtra<Uri>(EXTRA_RINGTONE_PICKED_URI)
                uri?.let {
                    val newSound = AlarmSound(uri.toString())
                    (binding.viewPager.adapter as StateAdapter).settingsFragment.onSystemAlarmSoundsResult(newSound)
                }
            }

            REQUEST_CODE_REMINDER_ALARM_SOUND -> {
                val uri = data?.getParcelableExtra<Uri>(EXTRA_RINGTONE_PICKED_URI)
                uri?.let {
                    val newSound = AlarmSound(uri.toString())
                    (binding.viewPager.adapter as StateAdapter).reminderFragment.onSystemAlarmSoundsResult(newSound)
                }
            }


        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onAttachedToWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }


    override fun onBackPressed() {
        if (binding.viewPager.currentItem != 0) {
            binding.viewPager.setCurrentItem(0, true)
        } else
            super.onBackPressed()
    }

    fun refreshActivity() {

    }


}