package com.hirshler.remindme.ui.reminder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.hirshler.remindme.databinding.FragmentReminderBinding
import com.hirshler.remindme.model.Reminder
import com.hirshler.remindme.room.AppDatabase
import com.hirshler.remindme.room.ReminderRepo

class ReminderFragment : Fragment() {

    private lateinit var reminderViewModel: ReminderViewModel
    private var _binding: FragmentReminderBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        reminderViewModel = ViewModelProvider(this).get(ReminderViewModel::class.java)

        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        reminderViewModel.text.observe(viewLifecycleOwner, {
//           = it
        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val reminder = Reminder(text = "this is my first reminder, you'll!")
        AppDatabase.getInstance(requireContext())
        val reminderRepo = ReminderRepo(requireContext())
        reminderRepo.insert(reminder)
        reminderRepo.getAll().forEach { Log.d("ReminderFragment", Gson().toJson(it)) }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}