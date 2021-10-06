package com.hirshler.remindme.ui.reminder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.hirshler.remindme.databinding.FragmentReminderBinding
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.AppDatabase
import com.hirshler.remindme.room.ReminderRepo
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class ReminderFragment : Fragment() {

    private lateinit var vm: ReminderViewModel
    private var _binding: FragmentReminderBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vm = ViewModelProvider(this).get(ReminderViewModel::class.java)
        _binding = FragmentReminderBinding.inflate(inflater, container, false)

        binding.minutesButton.setOnToggleCallback { minutes ->
            vm.setMinutes(minutes)
        }

        binding.daysButton.setOnToggleCallback { days ->
            vm.setDays(days)
        }


        vm.currentCalendar.observe(viewLifecycleOwner, { calendar ->
            binding.dateDisplay.text = SimpleDateFormat("EEEE, dd/MM/YY kk:mm").format(calendar.time)
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val reminder = Reminder(text = "this is my first reminder, you'll!")
        AppDatabase.getInstance(requireContext())
        val reminderRepo = ReminderRepo(requireContext())
        reminderRepo.insert(reminder)
        reminderRepo.getAll().forEach { Log.d("ReminderFragment", Gson().toJson(it)) }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}