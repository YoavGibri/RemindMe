package com.hirshler.remindme

import android.content.res.ColorStateList
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.setPadding
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

                                parent.context.run {

                                    val outValue = TypedValue()
                                    theme.resolveAttribute(R.attr.main_color_natural, outValue, true)
                                    imgDelete.imageTintList = ColorStateList.valueOf(getColor(outValue.resourceId))

                                    imgDelete.setBackgroundResource(R.drawable.background_round_button_colored)


                                    theme.resolveAttribute(R.attr.main_color_natural_dark, outValue, true)

                                    date.setTextColor(getColor(outValue.resourceId))
                                    time.setTextColor(getColor(outValue.resourceId))
                                    text.setTextColor(getColor(outValue.resourceId))

                                }
                            }

                            TYPE.ITEM_DISMISSED.ordinal -> {
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

            holder.binding.date.text = generateDateText(reminder)
            holder.binding.time.text = generateTimeText(reminder)


            holder.binding.imgTextAndVoice.apply {

                when {
                    reminder.text.isNotEmpty() && reminder.voiceNotePath.isNotEmpty() -> {
                        setImageResource(R.drawable.icon_text_and_voice)
                        setPadding(context.resources.getDimensionPixelSize(R.dimen.reminderlist_rowitem_textandvoice_padding))
                    }

                    reminder.text.isNotEmpty() -> {
                        setImageResource(R.drawable.icon_text)
                        setPadding(context.resources.getDimensionPixelSize(R.dimen.reminderlist_rowitem_text_padding))
                    }

                    else -> {
                        setImageResource(R.drawable.icon_voice)
                        setPadding(context.resources.getDimensionPixelSize(R.dimen.reminderlist_rowitem_text_padding))
                    }
                }
            }



            holder.binding.imgDelete.setOnClickListener { clickListener.onDeleteClick(reminder) }

            holder.binding.root.setOnClickListener { clickListener.onEditClick(reminder) }

        } else if (holder.binding is RemindersRemindersListRowTitleBinding) {
            holder.binding.text.text = reminder.text
        }
    }

    private fun generateTimeText(reminder: Reminder): String {
        Calendar.getInstance().apply {

            if (reminder.repeat) {
                set(Calendar.MINUTE, reminder.alarmTimeOfDay)

            } else {

                if (reminder.nextAlarmWithSnooze() == 0L)
                    return ""
                timeInMillis = reminder.nextAlarmWithSnooze()
            }

            val time = SimpleDateFormat("kk:mm", Locale.getDefault()).format(time)
            return time
        }
    }


    private fun generateDateText(reminder: Reminder): String {
        if (reminder.repeat) {

            val days = reminder.weekDays
                .filter { it.value == true }
                .keys.joinToString(" ") { key ->
                    when (key) {
                        1 -> "Su"
                        2 -> "Mo"
                        3 -> "Tu"
                        4 -> "We"
                        5 -> "Th"
                        6 -> "Fr"
                        7 -> "Sa"
                        else -> ""
                    }
                }
            return days

        } else {

            if (reminder.nextAlarmWithSnooze() == 0L) return ""

            val cal = Calendar.getInstance().apply { timeInMillis = reminder.nextAlarmWithSnooze() }
            val date = SimpleDateFormat("dd/MM", Locale.getDefault()).format(cal.time)
            return date
        }
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