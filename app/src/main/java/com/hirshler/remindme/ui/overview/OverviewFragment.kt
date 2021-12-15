package com.hirshler.remindme.ui.overview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.hirshler.remindme.R
import com.hirshler.remindme.RemindersOverviewAdapter
import com.hirshler.remindme.RemindersOverviewAdapter.ReminderClickListener
import com.hirshler.remindme.databinding.FragmentOverviewBinding
import com.hirshler.remindme.model.Reminder

@SuppressLint("NotifyDataSetChanged")
class OverviewFragment : Fragment() {

    private val reminders: MutableList<Reminder> = mutableListOf()

    //    private val reminders: MutableList<Reminder> = mutableListOf<Reminder>()
    private lateinit var vm: OverviewViewModel
    private var _binding: FragmentOverviewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vm = ViewModelProvider(this).get(OverviewViewModel::class.java)

        _binding = FragmentOverviewBinding.inflate(inflater, container, false)

        binding.reminderList.adapter = RemindersOverviewAdapter(object : ReminderClickListener {
            override fun onEditClick(reminder: Reminder) {

                findNavController().navigate(
                    R.id.action_navigation_overview_to_navigation_reminder,
                    Bundle().apply { putString("reminderToEdit", Gson().toJson(reminder)) },
                    NavOptions.Builder()
                        .setPopUpTo(R.id.navigation_reminder, true)
                        .build()
                )
            }

            override fun onDeleteClick(reminder: Reminder) {
                vm.deleteReminder(reminder)
            }
        }, reminders)



        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.reminders.observe(viewLifecycleOwner, {
            reminders.apply {
                clear()
                addAll(it)
                binding.reminderList.adapter?.notifyDataSetChanged()
            }

        })

        //vm.getReminders()
    }

    override fun onResume() {
        super.onResume()
        binding.reminderList.adapter?.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}