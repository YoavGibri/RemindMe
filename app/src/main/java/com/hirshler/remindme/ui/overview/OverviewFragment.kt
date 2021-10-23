package com.hirshler.remindme.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hirshler.remindme.RemindersOverviewAdapter
import com.hirshler.remindme.databinding.FragmentOverviewBinding
import com.hirshler.remindme.model.Reminder

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

        binding.reminderList.apply {
            adapter = RemindersOverviewAdapter(requireActivity(), reminders)
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        vm.reminders.observe(viewLifecycleOwner, {
            reminders.apply {
                clear()
                addAll(it)
            }

        })

        vm.getReminders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}