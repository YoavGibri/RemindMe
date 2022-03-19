package com.hirshler.remindme.view

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.hirshler.remindme.managers.VoiceRecorderManager

class VoiceRecordView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

//    private var binding: VoiceRecorderViewBinding
    private lateinit var voiceRecorder: VoiceRecorderManager
    private val toast: Toast? = null


    init {
//        binding = VoiceRecorderViewBinding.inflate(LayoutInflater.from(context), this)
//
//        voiceRecorder = VoiceRecorderManager(context as Activity, vm.currentReminder.value!!,
//            onRecordCallback = {
//                binding.recordButton.flash(200)
//                toast.show("Recording started")
//            },
//            onStopCallback = {
//                binding.recordButton.clearAnimation()
//            })
    }

    fun init() {

    }
}