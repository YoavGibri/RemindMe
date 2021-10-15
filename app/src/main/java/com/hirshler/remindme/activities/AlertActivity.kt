package com.hirshler.remindme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.hirshler.remindme.AlertsManager
import com.hirshler.remindme.App
import com.hirshler.remindme.R
import com.hirshler.remindme.databinding.ActivityAlertBinding
import com.hirshler.remindme.model.Alert
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.ui.alert.AlertViewModel
import com.hirshler.remindme.ui.reminder.ReminderViewModel
import java.lang.IllegalStateException
import java.util.*
import kotlin.concurrent.timerTask

class AlertActivity : AppCompatActivity() {
    lateinit var binding: ActivityAlertBinding
    private lateinit var vm: AlertViewModel

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this).get(AlertViewModel::class.java)
        vm.currentReminder.value =
            Gson().fromJson(intent.getStringExtra("reminder"), Reminder::class.java)
        vm.origReminder.value =
            Gson().fromJson(intent.getStringExtra("reminder"), Reminder::class.java)

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
                        Toast.makeText(
                            App.applicationContext(),
                            "timer ${Calendar.getInstance().timeInMillis}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, 2000)
            }


        }



        binding.dismissButton.setOnClickListener {
            AlertsManager.cancelAlert(
                vm.currentReminder.value!!,
                vm.currentReminder.value?.alerts?.get(0)!!
            )
            finish()
        }


    }

    override fun onAttachedToWindow() {
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

    }


}
