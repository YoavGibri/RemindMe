package com.hirshler.remindme

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hirshler.remindme.databinding.RemindersOverviewRowItemBinding
import com.hirshler.remindme.model.Reminder
import java.text.SimpleDateFormat
import java.util.*

class RemindersOverviewAdapter(private val context: Context, private val reminders: MutableList<Reminder>) :
    RecyclerView.Adapter<RemindersOverviewAdapter.ReminderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = RemindersOverviewRowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.binding.text.text = reminder.text
        holder.binding.dateAndTime.text = generateDateText(reminder.nextAlarmTime)
        holder.binding.imgText.visibility = if (reminder.text != null) View.VISIBLE else View.INVISIBLE
        holder.binding.imgVoiceNote.visibility =  View.VISIBLE
//        holder.binding.imgVoiceNote.visibility = if (reminder.voiceNotePath != null) View.VISIBLE else View.INVISIBLE
    }



    private fun generateDateText(nextAlarmTime: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = nextAlarmTime }
        val date = SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.time)
        val time = SimpleDateFormat("kk:mm", Locale.getDefault()).format(cal.time)
        return "$date\n$time"
    }

    override fun getItemCount(): Int {
        return reminders.count()
    }


    class ReminderViewHolder : RecyclerView.ViewHolder {
        val binding: RemindersOverviewRowItemBinding

        constructor(binding: RemindersOverviewRowItemBinding) : super(binding.root) {
            this.binding = binding
        }
    }
}