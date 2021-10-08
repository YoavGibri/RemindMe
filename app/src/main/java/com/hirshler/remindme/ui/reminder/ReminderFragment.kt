package com.hirshler.remindme.ui.reminder

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.hirshler.remindme.databinding.FragmentReminderBinding
import com.hirshler.remindme.model.Alert
import com.hirshler.remindme.model.Reminder
import java.text.SimpleDateFormat
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
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.minutesButton.setOnToggleCallback { minutes ->
            vm.setMinutes(minutes)
        }

        binding.daysButton.setOnToggleCallback { days ->
            vm.setDays(days)
        }

        binding.datePickerButton.setOnClickListener {
            val c = vm.currentCalendar.value!!
            val datePicker = DatePickerDialog(
                requireActivity(), { view, year, monthOfYear, dayOfMonth ->
                    vm.setDate(year, monthOfYear, dayOfMonth)
                    binding.daysButton.setDate(year, monthOfYear, dayOfMonth)
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = Calendar.getInstance().timeInMillis;
            datePicker.show()
        }

        binding.timePickerButton.setOnClickListener {
            val c = vm.currentCalendar.value!!
            val timePicker = TimePickerDialog(
                requireActivity(), AlertDialog.THEME_HOLO_LIGHT, { view, hourOfDay, minute ->
                    vm.setTime(hourOfDay, minute)
                    binding.minutesButton.disable()
                },
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true
            )

            timePicker.show()

        }

        binding.doneButton.setOnClickListener {
            createReminder()
            vm.saveReminderToDb(requireContext())
            Snackbar.make(binding.rootLayout, getAlertString(), Snackbar.LENGTH_LONG).show()
        }


        vm.currentCalendar.observe(viewLifecycleOwner, { calendar ->
            binding.timePickerButton.text =
                SimpleDateFormat("kk:mm", Locale.getDefault()).format(calendar.time)
//            binding.daysButton.setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        })

    }

    private fun getAlertString(): String {
        val date = SimpleDateFormat(
            "EEEE, dd/MM/yy",
            Locale.getDefault()
        ).format(vm.currentCalendar.value!!.time)
        val time =
            SimpleDateFormat("kk:mm", Locale.getDefault()).format(vm.currentCalendar.value!!.time)
        return "New reminder is set to $date at $time"
    }


    private fun createReminder() {
        vm.currentReminder.value = Reminder().apply {
            text = binding.text.text.toString()
            delayInMinutes = vm.minutesDelay
            alerts = listOf(Alert(vm.currentCalendar.value!!.timeInMillis))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}