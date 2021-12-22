package com.hirshler.remindme

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hirshler.remindme.ui.reminder.ReminderFragment
import com.hirshler.remindme.ui.reminderlist.ReminderListFragment
import com.hirshler.remindme.ui.settings.SettingsFragment

class StateAdapter(val activity: FragmentActivity) : FragmentStateAdapter(activity) {
     lateinit var settingsFragment: SettingsFragment

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ReminderFragment()
            1 -> ReminderListFragment()
            2 -> {
                settingsFragment = SettingsFragment()
                return settingsFragment
            }
            else -> SettingsFragment()
        }

    }








}