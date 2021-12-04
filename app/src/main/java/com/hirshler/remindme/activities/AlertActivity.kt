package com.hirshler.remindme.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hirshler.remindme.AlertsManager
import com.hirshler.remindme.NotificationsManager
import com.hirshler.remindme.RingManager
import com.hirshler.remindme.databinding.ActivityAlertBinding
import com.hirshler.remindme.model.Reminder.Companion.KEY_REMINDER_ID
import com.hirshler.remindme.ui.alert.AlertViewModel
import java.util.*
import kotlin.concurrent.timerTask

class AlertActivity : AppCompatActivity() {

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
                    vm.updateSnooze(5)
                    reminder.snoozeCount++
                    updateReminder()
                    firstLoad = false
                }

                val ringtonePath = voiceNotePath.ifEmpty { alertRingtonePath }
                ringManager = RingManager.getInstance(this@AlertActivity, ringtonePath)
                ringManager.play()


                binding.reminderText.text = text

                if (voiceNotePath.isEmpty() || text.isNotEmpty()) {
                    binding.voiceNoteImage.visibility = View.GONE
                }
            }

        })


        val id = intent.getLongExtra(KEY_REMINDER_ID, -1)
        vm.initCurrentReminderById(id)


        binding.minutesButton.setOnToggleCallback { minutes ->
//            vm.setMinutes(minutes)
            vm.updateSnooze(minutes)

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

    override fun onDestroy() {
        super.onDestroy()
        ringManager.pause()
    }

    override fun onAttachedToWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

    }


}
