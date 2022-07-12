package com.hirshler.remindme.ui.reminder

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.TextUtilsCompat
import androidx.core.view.ViewCompat.LAYOUT_DIRECTION_RTL
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.hirshler.remindme.*
import com.hirshler.remindme.activities.MainActivity.Companion.ON_ACTIVITY_START_GO_TO_REMINDERS_LIST
import com.hirshler.remindme.databinding.FragmentReminderBinding
import com.hirshler.remindme.managers.VoiceRecorderManager
import com.hirshler.remindme.model.AlarmSound
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.ui.MainActivityFragment
import com.hirshler.remindme.view.FromScreen
import com.hirshler.remindme.view.RepeatDialog
import com.hirshler.remindme.view.SelectAlarmSoundDialog
import com.hirshler.remindme.view.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask

private const val SECONDS_TO_AUTO_CLOSE: Long = 2

class ReminderFragment(private val reminderToEdit: Reminder? = null) : MainActivityFragment() {

    private var alarmSoundDialog: SelectAlarmSoundDialog? = null
    private lateinit var vm: ReminderViewModel
    private var _binding: FragmentReminderBinding? = null
    private lateinit var voiceRecorder: VoiceRecorderManager
    private val toast: Toast? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vm = ViewModelProvider(this)[ReminderViewModel::class.java]
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.apply {

            debugSwitch.isVisible = BuildConfig.DEBUG
            debugSwitch.isChecked = AppSettings.getIsDebugMode()


            vm.currentCalendar.observe(viewLifecycleOwner) { calendar ->
//            daysButton.setDate(calendar)
                timePickerButton.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
            }

            val localeDirection = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault())
            val rtl = if (localeDirection == LAYOUT_DIRECTION_RTL) "\u200F" else ""
            reminderText.hint = "$rtl${getString(R.string.write_something)}"


            reminderText.addTextChangedListener(
                afterTextChanged = { vm.currentReminder.value?.text = it.toString() }
            )

            reminderText.setOnFocusChangeListener { v, hasFocus -> if (!hasFocus) Utils.hideKeyboard(v) }

            minutesButton.setOnToggleCallback { minutes ->
                vm.setMinutes(minutes)
                Utils.hideKeyboard(minutesButton)
            }

            daysButton.setOnToggleCallback { days ->
                vm.setDays(days)
            }

            datePickerButton.setOnClickListener {
                val c = vm.currentCalendar.value!!
                val datePicker = DatePickerDialog(
                    requireActivity(), { _, year, monthOfYear, dayOfMonth ->
                        vm.setDate(year, monthOfYear, dayOfMonth)
                        daysButton.setDate(year, monthOfYear, dayOfMonth)
                    },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
                )
                datePicker.datePicker.minDate = Calendar.getInstance().timeInMillis
                datePicker.show()
            }

            timePickerButton.setOnClickListener {
                showTimePicker()
            }



            recordButton.setOnClickListener {
                if (voiceRecorder.isRecording()) {
                    voiceRecorder.stopRecording()
                    playPreviewButton.visibility = View.VISIBLE
                } else {
                    voiceRecorder.startRecording()
                }
            }

            playPreviewButton.setOnClickListener {
                voiceRecorder.playPreview()
                playPreviewButton.visibility = View.GONE
                stopPreviewButton.visibility = View.VISIBLE
            }

            stopPreviewButton.setOnClickListener {
                voiceRecorder.stopPreview()
                playPreviewButton.visibility = View.VISIBLE
                stopPreviewButton.visibility = View.GONE
            }

            chooseAlarmSoundButton.setOnClickListener {
                alarmSoundDialog = SelectAlarmSoundDialog(requireActivity(), FromScreen.REMINDER) { newAlarmSound ->
                    vm.currentReminder.value?.alertRingtonePath = newAlarmSound.stringUri
                }
                val alarmSound = vm.currentReminder.value?.alertRingtonePath?.let { if (it.isNotEmpty()) AlarmSound(it) else null }
                alarmSoundDialog?.showDialog(alarmSound)
            }

            repeatAlarmDialogButton.setOnClickListener {
                RepeatDialog(requireActivity(), vm.currentReminder.value!!.weekDays).show()
            }

            debugSwitch.setOnCheckedChangeListener { _, isChecked -> AppSettings.setIsDebugMode(isChecked) }


            doneButton.setOnClickListener {

                if (permissionsOk()) {


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


                            Timer().schedule(timerTask {


                                if (reminderToEdit != null) {
                                    refreshActivity(goToScreen = ON_ACTIVITY_START_GO_TO_REMINDERS_LIST)
                                } else {
                                    if (AppSettings.getCloseAppAfterReminderSet()) {
                                        closeApplication()
                                    } else
                                        refreshActivity()
                                }

                            }, SECONDS_TO_AUTO_CLOSE * 1000)


                        }
                    }
                }
            }

            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    //if starting new reminder and some text is written, clear the text
                    if (binding.reminderText.text.toString().isNotEmpty() && vm.currentReminder.value?.id == null) {
                        binding.reminderText.setText("")

                    } else if (reminderToEdit != null) {
                        refreshActivity(goToScreen = ON_ACTIVITY_START_GO_TO_REMINDERS_LIST)
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })

            initVoiceRecorder(vm.currentReminder.value!!)
        }

    }

    private fun setViewsFromReminder(reminder: Reminder) {
        reminder.apply {
            text.let { binding.reminderText.setText(it) }
            vm.currentCalendar.value = Calendar.getInstance().apply { timeInMillis = nextAlarmWithSnooze() }
            binding.daysButton.setDate(vm.currentCalendar.value!!)
            binding.playPreviewButton.visibility = if (voiceNotePath.isNotEmpty()) View.VISIBLE else View.GONE
            initVoiceRecorder(this)
        }
    }

    private fun initVoiceRecorder(reminder: Reminder) {
        voiceRecorder = VoiceRecorderManager(requireActivity(), reminder,
            onRecordCallback = {
                binding.recordButton.flash(200)
                toast.show("Recording started")
            },
            onStopCallback = {
                binding.recordButton.clearAnimation()
            })
    }

    private fun noTextAndNoViceRecording() =
        vm.currentReminder.value?.text.isNullOrEmpty() && (vm.currentReminder.value?.voiceNotePath.isNullOrEmpty() && !voiceRecorder.isRecording())

    private fun alertIsInThePast(): Boolean =
        vm.currentReminder.value?.isRepeat == false &&
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
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(vm.currentCalendar.value!!.time)
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

        reminderToEdit?.let {
            vm.currentReminder.value = reminderToEdit
            setViewsFromReminder(reminderToEdit)
            FlowLog.reminderEditStart(reminderToEdit)
        }
    }

    private fun permissionsOk(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            if (!Settings.canDrawOverlays(requireActivity())) {
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.draw_on_other_apps_permission_title)
                    .setMessage(R.string.draw_on_other_apps_permission_message)
                    .setPositiveButton(R.string.draw_on_other_apps_permission_button_positive) { _, _ ->
                        val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        startActivity(myIntent)
                    }
                    .setNegativeButton(R.string.draw_on_other_apps_permission_button_negative) { _, _ -> }
                    .setCancelable(false)
                    .show()
                return false
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            if (!(App.applicationContext().getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager).canScheduleExactAlarms()) {
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.alarms_and_reminders_permission_title)
                    .setMessage(R.string.alarms_and_reminders_permission_message)
                    .setPositiveButton(R.string.alarms_and_reminders_permission_button_positive) { _, _ ->
                        val myIntent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        startActivity(myIntent)
                    }
                    .setNegativeButton(R.string.alarms_and_reminders_permission_button_negative) { _, _ -> }
                    .setCancelable(false)
                    .show()
                return false
            }

        return true
    }

    fun onSystemAlarmSoundsResult(newSound: AlarmSound) {
        AppSettings.addSoundToAlarmSounds(newSound)
        vm.currentReminder.value?.alertRingtonePath = newSound.stringUri
        alarmSoundDialog?.refresh(newSound)
    }


}

