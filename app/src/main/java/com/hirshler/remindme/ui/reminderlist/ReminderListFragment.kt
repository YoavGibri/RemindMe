package com.hirshler.remindme.ui.reminderlist

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hirshler.remindme.RemindersListAdapter
import com.hirshler.remindme.RemindersListAdapter.ReminderClickListener
import com.hirshler.remindme.activities.MainActivity
import com.hirshler.remindme.databinding.FragmentRemindersListBinding
import com.hirshler.remindme.model.Reminder

@SuppressLint("NotifyDataSetChanged")
class ReminderListFragment : Fragment() {

    private val reminders: MutableList<Reminder> = mutableListOf()

    //    private val reminders: MutableList<Reminder> = mutableListOf<Reminder>()
    private lateinit var vm: RemindersListViewModel
    private lateinit var binding: FragmentRemindersListBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        vm = ViewModelProvider(this).get(RemindersListViewModel::class.java)
        binding = FragmentRemindersListBinding.inflate(inflater, container, false)

        binding.reminderList.adapter = RemindersListAdapter(object : ReminderClickListener {
            override fun onEditClick(reminder: Reminder) {

//                findNavController().navigate(
//                    R.id.action_navigation_reminders_list_to_navigation_reminder,
//                    Bundle().apply { putString("reminderToEdit", Gson().toJson(reminder)) },
//                    NavOptions.Builder()
//                        .setPopUpTo(R.id.navigation_reminder, true)
//                        .build()
//                )
                startActivity(Intent(requireActivity(), MainActivity::class.java).apply { putExtra("reminderToEdit", reminder.toString()) })
               // requireActivity().finish()
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
                if (it.isEmpty()) {
                    binding.reminderList.visibility = View.GONE
                    binding.noRemindersText.visibility = View.VISIBLE
                } else {
                    binding.reminderList.visibility = View.VISIBLE
                    binding.noRemindersText.visibility = View.GONE
                }
            }

        })

        //vm.getReminders()
    }

    override fun onResume() {
        super.onResume()
        binding.reminderList.adapter?.notifyDataSetChanged()
    }


}