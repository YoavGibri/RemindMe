package com.hirshler.remindme

import android.util.TypedValue
import android.view.LayoutInflater
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

        val binding: ViewBinding =
            if (viewType == TYPE.TITLE.ordinal) {
                RemindersRemindersListRowTitleBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )

            } else {

                RemindersRemindersListRowItemBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                    .apply {
                        when (viewType) {
                            TYPE.ITEM_REPEAT.ordinal -> {
                                backgroundLayout.setBackgroundResource(R.drawable.list_item_text_background_colored)
                                imgEdit.imageTintList = null
                                imgDelete.imageTintList = null

                                val outValue = TypedValue()
                                parent.context.theme.resolveAttribute(R.attr.button_background, outValue, true)
                                imgDelete.setBackgroundResource(outValue.resourceId)
                                val res = parent.context.resources
                                dateAndTime.setTextColor(res.getColor(R.color.white))
                                text.setTextColor(res.getColor(R.color.white))
                            }

                            TYPE.ITEM_DISMISSED.ordinal->{
                                root.alpha = 0.5f
                            }

                        }
                    }

            }


        return ReminderViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        if (holder.binding is RemindersRemindersListRowItemBinding) {
            holder.binding.text.text = reminder.text
            holder.binding.dateAndTime.text = generateDateText(reminder.nextAlarmWithSnooze())

            holder.binding.imgTextAndVoice.setImageResource(
                if (reminder.text.isNotEmpty() && reminder.voiceNotePath.isNotEmpty())
                    R.drawable.icon_text_and_voice
                else if (reminder.text.isNotEmpty())
                    R.drawable.icon_text
                else R.drawable.icon_voice
            )

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
        return "$time $date"
    }

    override fun getItemCount(): Int {
        return reminders.count()
    }


    override fun getItemViewType(position: Int): Int {
        return reminders[position].let {
            when {
                it.id == null -> TYPE.TITLE
                it.nextAlarmWithSnooze() < Calendar.getInstance().timeInMillis -> TYPE.ITEM_DISMISSED
                it.repeat -> TYPE.ITEM_REPEAT
                else -> TYPE.ITEM_ACTIVE
            }
        }.ordinal


    }

    class ReminderViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)


    interface ReminderClickListener {
        fun onEditClick(reminder: Reminder)
        fun onDeleteClick(reminder: Reminder)
    }

    enum class TYPE {
        TITLE, ITEM_ACTIVE, ITEM_REPEAT, ITEM_DISMISSED
    }
}