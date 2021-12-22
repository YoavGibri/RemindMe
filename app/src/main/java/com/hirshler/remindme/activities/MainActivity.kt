package com.hirshler.remindme.activities

import android.app.AlertDialog
import android.content.Intent
import android.media.RingtoneManager.EXTRA_RINGTONE_PICKED_URI
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.hirshler.remindme.AppSettings
import com.hirshler.remindme.BuildConfig
import com.hirshler.remindme.StateAdapter
import com.hirshler.remindme.databinding.ActivityMainBinding
import com.hirshler.remindme.ui.settings.SettingsFragment.Companion.REQUEST_CODE_GENERAL_ALARM_SOUND

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val navView: BottomNavigationView = binding.navView

//        val navController = findNavController(R.id.nav_host_fragment_activity_main)
//        navView.setupWithNavController(navController)

        binding.viewPager.adapter = StateAdapter(this)
        binding.dotsIndicator.setViewPager2(binding.viewPager)
        binding.viewPager.offscreenPageLimit = 2
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

    }

    fun refreshReminderFragment() {
//        findNavController(R.id.nav_host_fragment_activity_main).navigate(
//            R.id.navigation_reminder,
//            null,
//            NavOptions.Builder()
//                .setPopUpTo(R.id.navigation_reminder, true)
//                .build()
//        )
//        (binding.viewPager.adapter as StateAdapter)?.notifyItemChanged(0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_GENERAL_ALARM_SOUND) {
            val uri = data?.getParcelableExtra<Uri>(EXTRA_RINGTONE_PICKED_URI)
            AppSettings.setGeneralAlarm(uri)

//            val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)?.childFragmentManager?.fragments?.get(0)
//            if (currentFragment != null && currentFragment is SettingsFragment) {
//                currentFragment.setAlarmButtonTextFromSettings()
//            }
            (binding.viewPager.adapter as StateAdapter)?.settingsFragment.setAlarmTextFromSettings()
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