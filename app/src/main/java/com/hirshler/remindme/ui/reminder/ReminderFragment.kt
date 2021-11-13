package com.hirshler.remindme.ui.reminder

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.hirshler.remindme.*
import com.hirshler.remindme.activities.MainActivity
import com.hirshler.remindme.databinding.FragmentReminderBinding
import com.hirshler.remindme.model.Reminder
import java.text.SimpleDateFormat
import java.util.*


class ReminderFragment : Fragment() {

    private lateinit var vm: ReminderViewModel
    private var _binding: FragmentReminderBinding? = null
    private lateinit var voiceRecorder: VoiceRecorderManager
    private val toast: Toast? = null

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

        vm.currentCalendar.observe(viewLifecycleOwner, { calendar ->
            binding.timePickerButton.text = SimpleDateFormat("kk:mm", Locale.getDefault()).format(calendar.time)
        })


        arguments?.getString("reminderToEdit")?.let { it ->

            Gson().fromJson(it, Reminder::class.java)?.let { reminderToEdit ->

                vm.currentReminder.value = reminderToEdit
                setViewsFromReminder(reminderToEdit)

            }
        }

        binding.text.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                binding.autoSizingTextView.text = text
                binding.text.textSize = binding.autoSizingTextView.textSize / 3
            },
            afterTextChanged = { vm.currentReminder.value?.text = it.toString() })

        binding.text.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) Utils.hideKeyboard(v) }

        binding.minutesButton.setOnToggleCallback { minutes ->
            vm.setMinutes(minutes)
        }

        binding.daysButton.setOnToggleCallback { days ->
            vm.setDays(days)
        }

        binding.datePickerButton.setOnClickListener {
            val c = vm.currentCalendar.value!!
            val datePicker = DatePickerDialog(
                requireActivity(), { _, year, monthOfYear, dayOfMonth ->
                    vm.setDate(year, monthOfYear, dayOfMonth)
                    binding.daysButton.setDate(year, monthOfYear, dayOfMonth)
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.datePicker.minDate = Calendar.getInstance().timeInMillis
            datePicker.show()
        }

        binding.timePickerButton.setOnClickListener {
            showTimePicker()
        }

        binding.doneButton.setOnClickListener {
            when {
                noTextAndNoViceRecording() -> {
                    Snackbar.make(binding.rootLayout, getString(R.string.validation_error_no_text_or_voice), Snackbar.LENGTH_LONG).show()
                }
                else -> {
                    voiceRecorder.stopRecording()

                    vm.createReminder()
                    vm.saveReminderToDb()
                    vm.setAlerts()


                    Snackbar.make(binding.rootLayout, getAlertString(), Snackbar.LENGTH_LONG).show()
                    (requireActivity() as MainActivity).refreshFragment()
                }
            }
        }


        binding.recordButton.setOnClickListener {
            if (voiceRecorder.isRecording()) {
                voiceRecorder.stopRecording()
            } else {
                voiceRecorder.startRecording()
            }
        }



        voiceRecorder = VoiceRecorderManager(requireActivity(), vm.currentReminder.value!!,
            onRecordCallback = {
                binding.recordButton.flash(200)
                toast.show("Recording started")
            },
            onStopCallback = {
                binding.recordButton.clearAnimation()
            })


    }

    private fun setViewsFromReminder(reminder: Reminder) {
        reminder.apply {
            text?.let { binding.text.setText(it) }
            vm.currentCalendar.value = Calendar.getInstance().apply { timeInMillis = nextAlarmTime }
        }
    }

    private fun noTextAndNoViceRecording() =
        vm.currentReminder.value?.text.isNullOrEmpty() && (vm.currentReminder.value?.voiceNotePath.isNullOrEmpty() && !voiceRecorder.isRecording())


    private fun showTimePicker() {
        val c = vm.currentCalendar.value!!
        val timePicker = TimePickerDialog(
            requireActivity(), AlertDialog.THEME_HOLO_LIGHT, { _, hourOfDay, minute ->
                if (timeIsValidForToday(hourOfDay, minute)) {
                    vm.setTime(hourOfDay, minute)
                    binding.minutesButton.disable()
                } else {
                    toast.show("Please choose only future time")
                    showTimePicker()
                }
            },
            c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true
        )
        timePicker.show()
    }


    private fun timeIsValidForToday(hourOfDay: Int, minute: Int): Boolean {
        val cal = Calendar.getInstance()
        val currHour = cal.get(Calendar.HOUR_OF_DAY)
        val currMinutes = cal.get(Calendar.MINUTE)
        return (hourOfDay * 60 + minute) > (currHour * 60 + currMinutes)
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        voiceRecorder.onStop()
    }


}

