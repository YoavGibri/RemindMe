package com.hirshler.remindme.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.hirshler.remindme.R
import com.hirshler.remindme.databinding.ActivityAlertBinding
import com.hirshler.remindme.model.Reminder

class AlertActivity : AppCompatActivity() {
    lateinit var binding: ActivityAlertBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlertBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val reminder = Gson().fromJson(intent.getStringExtra("reminder"), Reminder::class.java)
        val reminderText = reminder.text
        binding.text.text = reminderText
        binding.dismissButton.setOnClickListener { finish() }
        binding.snoozeButton.setOnClickListener { finish() }
    }
}