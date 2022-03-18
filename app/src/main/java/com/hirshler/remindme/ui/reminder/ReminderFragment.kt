package com.hirshler.remindme.ui.reminder

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.hirshler.remindme.*
import com.hirshler.remindme.activities.MainActivity
import com.hirshler.remindme.activities.MainActivity.Companion.ON_ACTIVITY_START_GO_TO_REMINDERS_LIST
import com.hirshler.remindme.databinding.FragmentReminderBinding
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.view.RepeatDialog
import com.hirshler.remindme.view.SelectAlarmSoundDialog
import com.hirshler.remindme.view.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask


class ReminderFragment(private val reminderToEdit: Reminder?) : Fragment() {

    private val SECONDS_TO_AUTOCLOSE: Long = 2
    private lateinit var vm: ReminderViewModel
    private var _binding: FragmentReminderBinding? = null
    private lateinit var voiceRecorder: VoiceRecorderManager
    private val toast: Toast? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vm = ViewModelProvider(this).get(ReminderViewModel::class.java)
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.debugSwitch.isChecked = AppSettings.getIsDebugMode()

        vm.currentCalendar.observe(viewLifecycleOwner, { calendar ->
            binding.timePickerButton.text = SimpleDateFormat("kk:mm", Locale.getDefault()).format(calendar.time)
        })



        reminderToEdit?.let {
            vm.currentReminder.value = reminderToEdit
            setViewsFromReminder(reminderToEdit)
            FlowLog.reminderEditStart(reminderToEdit)
        }


        binding.text.addTextChangedListener(
            afterTextChanged = { vm.currentReminder.value?.text = it.toString() }
        )


        binding.text.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) Utils.hideKeyboard(v) }

        binding.minutesButton.setOnToggleCallback { minutes ->
            vm.setMinutes(minutes)
            Utils.hideKeyboard(binding.minutesButton)
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



        binding.recordButton.setOnClickListener {
            if (voiceRecorder.isRecording()) {
                voiceRecorder.stopRecording()
                binding.playPreviewButton.visibility = View.VISIBLE
            } else {
                voiceRecorder.startRecording()
            }
        }

        binding.playPreviewButton.setOnClickListener {
            voiceRecorder.playPreview()
            binding.playPreviewButton.visibility = View.GONE
            binding.stopPreviewButton.visibility = View.VISIBLE
        }

        binding.stopPreviewButton.setOnClickListener {
            voiceRecorder.stopPreview()
            binding.playPreviewButton.visibility = View.VISIBLE
            binding.stopPreviewButton.visibility = View.GONE
        }

        binding.chooseAlarmSoundButton.setOnClickListener {
            val alarmSoundDialog = SelectAlarmSoundDialog(requireActivity()) {
                vm.currentReminder.value?.alertRingtonePath = it.stringUri
            }
            alarmSoundDialog.showSpecific()
        }

        binding.repeatAlarmDialogButton.setOnClickListener {
            RepeatDialog(requireActivity(), vm.currentReminder.value!!.weekDays).show()
        }

        binding.debugSwitch.setOnCheckedChangeListener { buttonView, isChecked -> AppSettings.setIsDebugMode(isChecked) }



        voiceRecorder = VoiceRecorderManager(requireActivity(), vm.currentReminder.value!!,
            onRecordCallback = {
                binding.recordButton.flash(200)
                toast.show("Recording started")
            },
            onStopCallback = {
                binding.recordButton.clearAnimation()
            })

        binding.doneButton.setOnClickListener {
            when {
                noTextAndNoViceRecording() -> {
                    showErrorSnackBar(R.string.validation_error_no_text_or_voice)
                }
                alertIsInThePast() -> {
                    showErrorSnackBar(R.string.error_past_time)
                }

                else -> {
                    voiceRecorder.stopRecording()

                    vm.createReminder()
                    vm.saveReminderToDb()
                    vm.setAlert()

                    showSuccessSnackBar(text = getAlertString())
//                    if (AppSettings.getCloseAppAfterReminderSet()) {
//                        Timer().schedule(timerTask {
//                            requireActivity().finish()
//                        }, SECONDS_TO_AUTOCLOSE * 1000)
//                    } else {
//                        startActivity(Intent(requireActivity(), MainActivity::class.java))
//                        requireActivity().finish()
//                    }

                    Timer().schedule(timerTask {


                        if (reminderToEdit != null) {
                            startActivity(Intent(requireActivity(), MainActivity::class.java)
                                .apply { putExtra(ON_ACTIVITY_START_GO_TO_REMINDERS_LIST, true) })
                        } else {
                            if (!AppSettings.getCloseAppAfterReminderSet()) {
                                startActivity(Intent(requireActivity(), MainActivity::class.java))
                            }
                        }

                        requireActivity().finish()


                    }, SECONDS_TO_AUTOCLOSE * 1000)


                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //if starting new reminder and some text is written, clear the text
                if (binding.text.text.toString().isNotEmpty() && vm.currentReminder.value?.id == null) {
                    binding.text.setText("")

                } else if (reminderToEdit != null) {
                    startActivity(Intent(requireActivity(), MainActivity::class.java)
                        .apply { putExtra(ON_ACTIVITY_START_GO_TO_REMINDERS_LIST, true) })

                } else {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        })

    }

    private fun setViewsFromReminder(reminder: Reminder) {
        reminder.apply {
            text.let { binding.text.setText(it) }
            vm.currentCalendar.value = Calendar.getInstance().apply { timeInMillis = nextAlarm() }
            binding.playPreviewButton.visibility = if (voiceNotePath.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun noTextAndNoViceRecording() =
        vm.currentReminder.value?.text.isNullOrEmpty() && (vm.currentReminder.value?.voiceNotePath.isNullOrEmpty() && !voiceRecorder.isRecording())

    private fun alertIsInThePast(): Boolean =
        vm.currentReminder.value?.repeat == false &&
                vm.currentCalendar.value?.time?.before(Calendar.getInstance().time) == true


    private fun showTimePicker() {
        val timePicker = TimePickerDialog(requireActivity(), vm.currentCalendar.value!!) { hourOfDay, minute ->
            vm.setTime(hourOfDay, minute)
            binding.minutesButton.disable()
        }

        timePicker.show()
    }

    private fun showSuccessSnackBar(@StringRes resId: Int = 0, text: String = "") {
        showSnackBar(resId, text, R.color.success)
    }

    private fun showErrorSnackBar(@StringRes resId: Int = 0, text: String = "") {
        showSnackBar(resId, text, R.color.error)
    }

    private fun showSnackBar(@StringRes resId: Int, text: String, @ColorRes color: Int) {
        val displayText = if (resId != 0) App.applicationContext().getString(resId) else text
        Snackbar.make(binding.rootLayout, displayText, Snackbar.LENGTH_LONG)
            .setBackgroundTint(App.applicationContext().getColor(color)).show()
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
        voiceRecorder.onStopRecord()
        voiceRecorder.stopPreview()
    }

    override fun onResume() {
        super.onResume()
        vm.currentCalendar.value = Calendar.getInstance()
        binding.minutesButton.reset()
    }


}

