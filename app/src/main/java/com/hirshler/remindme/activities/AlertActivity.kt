package com.hirshler.remindme.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.hirshler.remindme.AlertsManager
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

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this).get(AlertViewModel::class.java)


        vm.currentReminder.observe(this, { reminder ->
            reminder?.apply {

//                // set snooze reminder
//                reminder.alerts?.get(0)?.time =
//                    Calendar.getInstance().apply { add(Calendar.MINUTE, 5) }.timeInMillis
//                AlertsManager.setAlert(reminder, reminder.alerts?.get(0)!!)

                //todo fix issue when reminder's time sometime present and sometime not
                if (firstLoad) {
                    vm.setMinutes(5)
                    updateReminder()
                    firstLoad = false
                }

                val ringtonePath = voiceNotePath ?: alertRingtonePath
                ringManager = RingManager.getInstance(this@AlertActivity, ringtonePath)
                ringManager.play()


                binding.reminderText.text = text

                if (voiceNotePath == null || (voiceNotePath != null && text != null)) {
                    binding.voiceNoteImage.visibility = View.GONE
                }
            }

        })


        val id = intent.getLongExtra(KEY_REMINDER_ID, -1)
        vm.initCurrentReminderById(id)


        binding.minutesButton.setOnToggleCallback { minutes ->
            vm.setMinutes(minutes)

            timer?.cancel()

            timer = Timer().apply {
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


    }

    private fun updateReminder() {
        vm.updateReminder()
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
