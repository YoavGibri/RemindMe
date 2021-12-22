package com.hirshler.remindme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.hirshler.remindme.databinding.RemindersRemindersListRowItemBinding
import com.hirshler.remindme.databinding.RemindersRemindersListRowTitleBinding
import com.hirshler.remindme.model.Reminder
import java.text.SimpleDateFormat
import java.util.*


class RemindersListAdapter(private val clickListener: ReminderClickListener, private val reminders: MutableList<Reminder>) :
    RecyclerView.Adapter<RemindersListAdapter.ReminderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {

        val binding: ViewBinding = when (viewType) {
            ITEM -> RemindersRemindersListRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            //TITLE
            else -> RemindersRemindersListRowTitleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        }
        return ReminderViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        if (holder.binding is RemindersRemindersListRowItemBinding) {
            holder.binding.text.text = reminder.text
            holder.binding.dateAndTime.text = generateDateText(reminder.nextAlarmTime)

            holder.binding.imgText.visibility = if (reminder.text.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            holder.binding.imgVoiceNote.visibility = if (reminder.voiceNotePath.isNotEmpty()) View.VISIBLE else View.INVISIBLE

            holder.binding.imgDelete.setOnClickListener { clickListener.onDeleteClick(reminder) }

            holder.binding.root.setOnClickListener { clickListener.onEditClick(reminder) }

        } else if (holder.binding is RemindersRemindersListRowTitleBinding) {
            holder.binding.text.text = reminder.text
        }
    }


    private fun generateDateText(nextAlarmTime: Long): String {
        if (nextAlarmTime == 0L) return ""

        val cal = Calendar.getInstance().apply { timeInMillis = nextAlarmTime }
        val date = SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.time)
        val time = SimpleDateFormat("kk:mm", Locale.getDefault()).format(cal.time)
        return "$date\n$time"
    }

    override fun getItemCount(): Int {
        return reminders.count()
    }


    private val TITLE = 1
    private val ITEM = 0
    override fun getItemViewType(position: Int): Int {
        return if (reminders[position].id == null) TITLE else ITEM
    }

    class ReminderViewHolder(val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root)


    interface ReminderClickListener {
        fun onEditClick(reminder: Reminder)
        fun onDeleteClick(reminder: Reminder)
    }

}