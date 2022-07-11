package com.hirshler.remindme.ui.infosplash

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hirshler.remindme.R

class SplashImagesAdapter(fragmentActivity: FragmentActivity, val callback: (position: Int) -> Unit) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ImageFragment.newInstance(R.drawable.splash1) { callback(position) }
            1 -> ImageFragment.newInstance(R.drawable.splash2) { callback(position) }
            else -> ImageFragment.newInstance()
        }
    }

}