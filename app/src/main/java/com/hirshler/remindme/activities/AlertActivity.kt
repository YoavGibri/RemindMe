package com.hirshler.remindme.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.hirshler.remindme.AlertsManager
import com.hirshler.remindme.RingManager
import com.hirshler.remindme.databinding.ActivityAlertBinding
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.ui.alert.AlertViewModel
import java.util.*
import kotlin.concurrent.timerTask

class AlertActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlertBinding
    private lateinit var vm: AlertViewModel
    private lateinit var ringManager: RingManager

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = ViewModelProvider(this).get(AlertViewModel::class.java)
        vm.currentReminder.value = Gson().fromJson(intent.getStringExtra("reminder"), Reminder::class.java)
//        vm.origReminder.value = Gson().fromJson(intent.getStringExtra("reminder"), Reminder::class.java)


        val ringtonePath = vm.currentReminder.value.let { it?.voiceNotePath ?: it?.alertRingtonePath }
        ringManager = RingManager(this, ringtonePath)
        ringManager.play()


        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.reminderText.text = vm.currentReminder.value?.text

        if (vm.currentReminder.value?.let { reminder -> reminder.voiceNotePath == null || (reminder.voiceNotePath != null && reminder.text != null) }!!) {
            binding.voiceNoteImage.visibility = View.GONE
        }


        binding.minutesButton.setOnToggleCallback { minutes ->
            vm.setMinutes(minutes)

            timer?.cancel()

            timer = Timer().apply {
                schedule(timerTask {
                    runOnUiThread {
                        updateReminder()
                    }
                }, 2000)
            }
        }

        binding.datePickerButton.setOnClickListener {
            val c = vm.currentCalendar.value!!
            val datePicker = DatePickerDialog(
                this, { _, year, monthOfYear, dayOfMonth ->
                    vm.setDate(year, monthOfYear, dayOfMonth)
                    //binding.daysButton.setDate(year, monthOfYear, dayOfMonth)
                    updateReminder()
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = Calendar.getInstance().timeInMillis;
            datePicker.show()
        }



        binding.dismissButton.setOnClickListener {
            AlertsManager.cancelAlert(
                vm.currentReminder.value!!,
                vm.currentReminder.value?.alerts?.get(0)!!
            )
            vm.dismissReminder()
            finish()
        }

        binding.muteButton.setOnToggleCallback { playbackOn ->
            if (playbackOn) ringManager.play() else ringManager.pause()
        }


    }

    private fun updateReminder() {
        vm.updateReminder()
        vm.saveReminderToDb()
        vm.setAlerts()
        finish()
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
