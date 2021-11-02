package com.hirshler.remindme.ui.reminder

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.hirshler.remindme.VoiceRecorderManager
import com.hirshler.remindme.activities.MainActivity
import com.hirshler.remindme.databinding.FragmentReminderBinding
import java.text.SimpleDateFormat
import java.util.*


class ReminderFragment : Fragment() {

    private lateinit var vm: ReminderViewModel
    private var _binding: FragmentReminderBinding? = null
    private lateinit var voiceRecorder: VoiceRecorderManager

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

        arguments?.getString("reminderToEdit")?.let {
            // TODO: 24/10/21 populate field to start edit mode!
        }

        binding.text.addTextChangedListener { vm.currentReminder.value?.text = it.toString() }

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
            showTimePicker()
        }

        binding.doneButton.setOnClickListener {
            if (vm.currentReminder.value?.text.isNullOrEmpty() && vm.currentReminder.value?.voiceNotePath.isNullOrEmpty()) {
                Snackbar.make(binding.rootLayout, "You must enter text or record a voice reminder!", Snackbar.LENGTH_LONG).show()
            } else {
                voiceRecorder.stopRecording()
                vm.createReminder()
                vm.saveReminderToDb()
                vm.setAlerts()
                Snackbar.make(binding.rootLayout, getAlertString(), Snackbar.LENGTH_LONG).show()
                (requireActivity() as MainActivity).refreshFragment()
            }
        }


        binding.recordButton.setOnClickListener {
            if (voiceRecorder.isRecording()) {
                voiceRecorder.stopRecording()
            } else {
                voiceRecorder.startRecording()
            }
        }


        vm.currentCalendar.observe(viewLifecycleOwner, { calendar ->
            binding.timePickerButton.text =
                SimpleDateFormat("kk:mm", Locale.getDefault()).format(calendar.time)
        })


        voiceRecorder = VoiceRecorderManager(requireActivity(),
            vm.currentReminder.value!!,
            onRecordCallback = {
                startButtonBlink(binding.recordButton)
                Toast.makeText(requireActivity(), "Recording started", Toast.LENGTH_LONG).show()
            }
        ) { binding.recordButton.clearAnimation() }


    }


    private fun showTimePicker() {
        val c = vm.currentCalendar.value!!
        val timePicker = TimePickerDialog(
            requireActivity(), AlertDialog.THEME_HOLO_LIGHT, { view, hourOfDay, minute ->
                if (timeIsValidForToday(hourOfDay, minute)) {
                    vm.setTime(hourOfDay, minute)
                    binding.minutesButton.disable()
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Please choose only future time",
                        Toast.LENGTH_LONG
                    ).show()
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


    private fun startButtonBlink(button: ImageButton) {
        val anim: Animation = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 200 //You can manage the blinking time with this parameter

        anim.startOffset = 20
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = Animation.INFINITE
        button.startAnimation(anim)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        voiceRecorder.onStop()
    }


}