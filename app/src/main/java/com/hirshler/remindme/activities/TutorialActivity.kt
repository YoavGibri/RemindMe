package com.hirshler.remindme.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hirshler.remindme.databinding.ActivityTutorialBinding


class TutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTutorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.viewPager.adapter = StateAdapter(this)
//        binding.viewPager.offscreenPageLimit = 2

    }





}