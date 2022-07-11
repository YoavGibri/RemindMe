package com.hirshler.remindme.ui.settings

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider
import com.hirshler.remindme.App
import com.hirshler.remindme.AppSettings
import com.hirshler.remindme.BuildConfig
import com.hirshler.remindme.R
import com.hirshler.remindme.activities.MainActivity
import com.hirshler.remindme.databinding.FragmentSettingsBinding
import com.hirshler.remindme.model.AlarmSound
import com.hirshler.remindme.ui.MainActivityFragment
import com.hirshler.remindme.view.FromScreen
import com.hirshler.remindme.view.SelectAlarmSoundDialog
import com.hirshler.remindme.view.UserNameDialog


class SettingsFragment : MainActivityFragment() {

    private var alarmSoundDialog: SelectAlarmSoundDialog? = null
    private lateinit var vm: SettingsViewModel
    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)


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
            val url = getString(R.string.help_url)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }

        binding.shareButton.setOnClickListener {
            val shareText = getString(R.string.check_my_app_at_play_store, getString(R.string.app_play_store_url))
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, shareText)
            startActivity(Intent.createChooser(intent, getString(R.string.share_chooser_title)))
        }

        binding.chooseAlarmSoundButton.setOnClickListener {
            alarmSoundDialog = SelectAlarmSoundDialog(requireActivity(), FromScreen.SETTINGS) {
                AppSettings.setGeneralAlarm(it)
                setAlarmTextFromSettings()
            }
            alarmSoundDialog?.showDialog(AppSettings.getGeneralAlarm())
        }


        binding.colorSelector.setColorChangeListener {
            refreshActivity(
                MainActivity.ON_ACTIVITY_START_GO_TO_SETTINGS,
                options = ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle()
            )
        }

        binding.userNameDialog.setOnClickListener { UserNameDialog.showUserNameDialog(requireActivity()) }

        val versionText = "v. ${BuildConfig.VERSION_CODE}/${BuildConfig.VERSION_NAME}"
        binding.version.text = versionText

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

//    fun refreshAlarmSoundsDialog() {
//        alarmSoundDialog?.refresh()
//    }

    fun onSystemAlarmSoundsResult(newSound: AlarmSound) {
        AppSettings.addSoundToAlarmSounds(newSound)
        AppSettings.setGeneralAlarm(newSound)
        alarmSoundDialog?.refresh()
    }


}