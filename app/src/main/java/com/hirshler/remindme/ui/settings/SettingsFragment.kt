package com.hirshler.remindme.ui.settings

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hirshler.remindme.App
import com.hirshler.remindme.AppSettings
import com.hirshler.remindme.activities.MainActivity
import com.hirshler.remindme.databinding.FragmentSettingsBinding
import com.hirshler.remindme.view.SelectAlarmSoundDialog
import com.hirshler.remindme.view.UserNameDialog


class SettingsFragment : Fragment() {

    private var alarmSoundDialog: SelectAlarmSoundDialog? = null
    private lateinit var vm: SettingsViewModel
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
//        binding.button.setOnClickListener{throw RuntimeException("Test Crash")}


        binding.voiceVolumeSeekBar.apply {
            max = maxAlarmSteamVolume()
            progress = AppSettings.getVoiceVolume()
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (progress == 0) seekBar?.progress = 1
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar?.progress?.let { AppSettings.setVoiceVolume(it) }
                }
            })

        }

        binding.alarmVolumeSeekBar.apply {
            max = maxAlarmSteamVolume()
            progress = AppSettings.getAlarmVolume()
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (progress == 0) seekBar?.progress = 1
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    seekBar?.progress?.let { AppSettings.setAlarmVolume(it) }
                }
            })
        }




        binding.vibrateCheckBox.apply {
            isChecked = AppSettings.getVibrate()
            setOnCheckedChangeListener { buttonView, isChecked ->
                AppSettings.setVibrate(isChecked)
            }
        }

        binding.closeAppCheckBox.apply {
            isChecked = AppSettings.getCloseAppAfterReminderSet()
            setOnCheckedChangeListener { buttonView, isChecked ->
                AppSettings.setCloseAppAfterReminderSet(isChecked)
            }

        }

        binding.moreInfoButton.setOnClickListener {
            val url = "https://www.interpresence.land/effective-reminder-help"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        binding.shareButton.setOnClickListener {
            val shareText = "Hey check out my app at: https://play.google.com/store/apps/details?id=fujdevelopers.com.remindme"
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(intent, "Share using"))
        }

        binding.chooseAlarmSoundButton.setOnClickListener {
            alarmSoundDialog = SelectAlarmSoundDialog(requireActivity()) {
                AppSettings.setGeneralAlarm(it)
                setAlarmTextFromSettings()
            }
            alarmSoundDialog?.showGeneral()
        }

        binding.userNameDialog.setOnClickListener { UserNameDialog.showUserNameDialog(requireActivity()) }

        binding.colorSelector.setColorChangeListener {
            startActivity(Intent(requireActivity(), MainActivity::class.java)
                .apply { putExtra(MainActivity.ON_ACTIVITY_START_GO_TO_SETTINGS, true) })
        }

        return binding.root
    }

    private fun maxAlarmSteamVolume() = (App.applicationContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager)
        .getStreamMaxVolume(AudioManager.STREAM_ALARM)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm = ViewModelProvider(this).get(SettingsViewModel::class.java)

        setAlarmTextFromSettings()
    }

    private fun setAlarmTextFromSettings() {
        val alarmSound = AppSettings.getGeneralAlarm()
        binding.currentAlarmSound.text = alarmSound.displayName
    }

    fun refreshAlarmSoundsDialog() {
        alarmSoundDialog?.refresh()
    }


}