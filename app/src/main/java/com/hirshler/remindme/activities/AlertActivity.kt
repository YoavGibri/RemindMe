package com.hirshler.remindme.activities

import android.app.DatePickerDialog
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hirshler.remindme.*
import com.hirshler.remindme.databinding.ActivityAlertBinding
import com.hirshler.remindme.model.Reminder.Companion.KEY_REMINDER_ID
import com.hirshler.remindme.ui.alert.AlertViewModel
import java.util.*
import kotlin.concurrent.timerTask

/*

 */

class AlertActivity : AppCompatActivity() {

    private var finishByUser: Boolean = false
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
    private val SNOOZE_BUTTON_DELAY_SECONDS: Long = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this).get(AlertViewModel::class.java)


        vm.currentReminder.observe(this, { reminder ->
            reminder?.apply {
                if (firstLoad) {

                    FlowLog.alertIsAlerting(reminder)

                    vm.setCalendarByReminder()

                    if (reminder.snoozeCount < 5) {
                        vm.currentSnooze = 5
                        reminder.snoozeCount++
                        updateReminder()
                    }

                    firstLoad = false

                    audioManager = App.applicationContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
                    origAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
                    val newAlarmVolume = if (voiceNotePath.isNotEmpty()) AppSettings.getVoiceVolume() else AppSettings.getAlarmVolume()
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, newAlarmVolume, 0)
                    val ringtonePath = voiceNotePath.ifEmpty { alertRingtonePath }
                    ringManager.setRingPath(Uri.parse(ringtonePath))
                }


                val fromAlert = !intent.getBooleanExtra(NotificationsManager.FROM_NOTIFICATION, false)
                if (fromAlert) {
                    ringManager.play()
                    playbackOn = true
                }

                if (reminder.text.isNotEmpty()) {
                    binding.reminderText.apply {
                        visibility = View.VISIBLE
                        text = reminder.text
                        flash(400)
                    }
                } else {
                    binding.voiceNoteImage.apply {
                        visibility = View.VISIBLE
                        flash(400)
                    }
                }
            }

        })

        ringManager = RingManager(this)

        val id = intent.getLongExtra(KEY_REMINDER_ID, -1)
        vm.initCurrentReminderById(id)


        binding.minutesButton.setOnToggleCallback { minutes ->
            vm.currentSnooze = minutes

            minutesButtonTimer?.cancel()

            minutesButtonTimer = Timer().apply {
                schedule(timerTask {


                    runOnUiThread {
                        updateReminder()
                        FlowLog.alertSnoozed(vm.currentReminder.value)

                        finishByUser()
                    }

                }, SNOOZE_BUTTON_DELAY_SECONDS * 1000)
            }
        }

        binding.datePickerButton.setOnClickListener {
            val c = vm.currentCalendar.value!!
            val datePicker = DatePickerDialog(
                this, { _, year, monthOfYear, dayOfMonth ->
                    vm.setDate(year, monthOfYear, dayOfMonth)
                    vm.resetSnooze()
                    updateReminder()
                    finishByUser()
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
            vm.alertWasDismissed = true
            FlowLog.alertDismissed(vm.currentReminder.value)
            finishByUser()
        }


        binding.muteButton.setOnToggleCallback { playbackOn ->
            if (playbackOn) ringManager.play() else ringManager.pause()
            this.playbackOn = playbackOn
        }

        notificationTimer = Timer().apply {
            schedule(timerTask {
                runOnUiThread {
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
        ringManager.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (playbackOn) ringManager.play()
    }

    override fun onDestroy() {
        notificationTimer.cancel()

        if (!finishByUser)
            NotificationsManager.showMissedAlertNotification(this@AlertActivity, vm.currentReminder.value!!)

        if (origAlarmVolume != -1) {
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, origAlarmVolume, 0)
        }

        super.onDestroy()
    }

    private fun finishByUser() {
        finishByUser = true
        finish()
    }


    override fun onAttachedToWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

    }


}
