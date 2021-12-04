package com.hirshler.remindme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.hirshler.remindme.databinding.RemindersOverviewRowItemBinding
import com.hirshler.remindme.databinding.RemindersOverviewRowTitleBinding
import com.hirshler.remindme.model.Reminder
import java.text.SimpleDateFormat
import java.util.*


class RemindersOverviewAdapter(private val clickListener: ReminderClickListener, private val reminders: MutableList<Reminder>) :
    RecyclerView.Adapter<RemindersOverviewAdapter.ReminderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {

        val binding: ViewBinding = when (viewType) {
            ITEM -> RemindersOverviewRowItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            //TITLE
            else -> RemindersOverviewRowTitleBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        }
        return ReminderViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        if (holder.binding is RemindersOverviewRowItemBinding) {
            holder.binding.text.text = reminder.text
            holder.binding.dateAndTime.text = generateDateText(reminder.nextAlarmTime)

            holder.binding.imgText.visibility = if (reminder.text.isNotEmpty()) View.VISIBLE else View.INVISIBLE
            holder.binding.imgVoiceNote.visibility = if (reminder.voiceNotePath.isNotEmpty()) View.VISIBLE else View.INVISIBLE

            holder.binding.imgEdit.setOnClickListener { clickListener.onEditClick(reminder) }
            holder.binding.imgDelete.setOnClickListener { clickListener.onDeleteClick(reminder) }

        } else if (holder.binding is RemindersOverviewRowTitleBinding) {
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