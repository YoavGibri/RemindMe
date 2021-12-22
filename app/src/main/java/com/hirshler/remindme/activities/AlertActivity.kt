package com.hirshler.remindme.activities

import android.app.DatePickerDialog
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hirshler.remindme.*
import com.hirshler.remindme.databinding.ActivityAlertBinding
import com.hirshler.remindme.model.Reminder.Companion.KEY_REMINDER_ID
import com.hirshler.remindme.receivers.AlertReceiver
import com.hirshler.remindme.ui.alert.AlertViewModel
import java.util.*
import kotlin.concurrent.timerTask


class AlertActivity : AppCompatActivity() {

    private lateinit var audioManager: AudioManager
    private var origAlarmVolume: Int = -1
    private var playbackOn: Boolean = false
    private var firstLoad: Boolean = true
    private lateinit var binding: ActivityAlertBinding
    private lateinit var vm: AlertViewModel
    private lateinit var ringManager: RingManager

    private var minutesButtonTimer: Timer? = null
    private lateinit var notificationTimer: Timer

    private val SECONDS_TO_NOTIFICATION: Long = 50

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this).get(AlertViewModel::class.java)


        vm.currentReminder.observe(this, { reminder ->
            reminder?.apply {


                if (firstLoad && reminder.snoozeCount < 5) {
                    vm.setCalendarByReminder()
                    vm.appendToSnooze(5)
                    reminder.snoozeCount++
                    updateReminder()
                    firstLoad = false

                    audioManager = App.applicationContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    origAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
                    val newAlarmVolume = AppSettings.getAlarmVolume()
//                    val vibrateFlag = if (AppSettings.getVibrate()) FLAG_VIBRATE else 0
                    val vibrateFlag = 0
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, newAlarmVolume, vibrateFlag)
                    val ringtonePath = voiceNotePath.ifEmpty { alertRingtonePath }
                    ringManager.setRingPath(ringtonePath)
                }



                if (intent.getBooleanExtra(AlertReceiver.FROM_ALERT, true)) {
                    ringManager.play()
                    playbackOn = true
                }

                if (text.isNotEmpty()) {
                    binding.reminderText.apply {
                        visibility = View.VISIBLE
                        text = text
                        flash(200)
                    }
                } else {
                    binding.voiceNoteImage.apply {
                        visibility = View.VISIBLE
                        flash(200)
                    }
                }
            }

        })

        ringManager = RingManager(this)

        val id = intent.getLongExtra(KEY_REMINDER_ID, -1)
        vm.initCurrentReminderById(id)


        binding.minutesButton.setOnToggleCallback { minutes ->
//            vm.setMinutes(minutes)
            vm.appendToSnooze(minutes)

            minutesButtonTimer?.cancel()

            minutesButtonTimer = Timer().apply {
                schedule(timerTask {
                    runOnUiThread {
                        updateReminder()
                        finish()
                    }
                }, 2000)
            }
        }

        binding.datePickerButton.setOnClickListener {
            val c = vm.currentCalendar.value!!
            val datePicker = DatePickerDialog(
                this, { _, year, monthOfYear, dayOfMonth ->
                    vm.setDate(year, monthOfYear, dayOfMonth)
                    updateReminder()
                    finish()
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = Calendar.getInstance().timeInMillis;
            datePicker.show()
        }



        binding.dismissButton.setOnClickListener {
            vm.currentReminder.value?.apply {

                if (repeat) {
                    snooze = 0
                    vm.saveReminderToDb()
                    vm.setNextAlert()

                } else {
                    vm.dismissReminder()
                    AlertsManager.cancelAlert(this)
                }
            }

            finish()
        }


        binding.muteButton.setOnToggleCallback { playbackOn ->
            if (playbackOn) ringManager.play() else ringManager.pause()
            this.playbackOn = playbackOn
        }

        notificationTimer = Timer().apply {
            schedule(timerTask {
                runOnUiThread {
                    NotificationsManager.showMissedAlertNotification(this@AlertActivity, vm.currentReminder.value!!)
                    finish()
                }
            }, SECONDS_TO_NOTIFICATION * 1000)
        }

    }

    private fun updateReminder() {
        vm.updateCurrentReminder()
        vm.saveReminderToDb()
        vm.setNextAlert()
    }


    override fun onPause() {
        super.onPause()
        ringManager.pause()
    }

    override fun onResume() {
        super.onResume()
        if (playbackOn) ringManager.play()
    }

    override fun onDestroy() {
        notificationTimer.cancel()
        NotificationsManager.showMissedAlertNotification(this@AlertActivity, vm.currentReminder.value!!)
        if (origAlarmVolume != -1) {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, origAlarmVolume, 0)
        }
        super.onDestroy()
    }


    override fun onAttachedToWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

    }


}
