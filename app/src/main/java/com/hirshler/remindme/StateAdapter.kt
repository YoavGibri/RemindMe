package com.hirshler.remindme

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.ui.log.LogFragment
import com.hirshler.remindme.ui.reminder.ReminderFragment
import com.hirshler.remindme.ui.reminderlist.ReminderListFragment
import com.hirshler.remindme.ui.settings.SettingsFragment

class StateAdapter(activity: FragmentActivity, val reminder: Reminder?) : FragmentStateAdapter(activity) {
     lateinit var settingsFragment: SettingsFragment

    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ReminderFragment(reminder)
            1 -> ReminderListFragment()
            2 -> {
                settingsFragment = SettingsFragment()
                return settingsFragment
            }
            3 -> LogFragment()
            else -> SettingsFragment()
        }

    }








}