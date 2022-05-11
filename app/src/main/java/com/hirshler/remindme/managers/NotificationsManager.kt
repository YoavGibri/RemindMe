package com.hirshler.remindme.managers

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hirshler.remindme.App
import com.hirshler.remindme.FlowLog
import com.hirshler.remindme.R
import com.hirshler.remindme.Utils
import com.hirshler.remindme.model.Reminder
import java.text.SimpleDateFormat
import java.util.*

class NotificationsManager {

    companion object {

        val FROM_NOTIFICATION: String = "fromNotification"
        private val CHANNEL_ID: String = "remind_me"


        //create notification channel
        fun createNotificationChannel() {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = App.applicationContext().getString(R.string.channel_name)
                val descriptionText = App.applicationContext().getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                // Register the channel with the system
                val notificationManager: NotificationManager = App.applicationContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }


        //set new notification with action to go to the alert activity
        fun showMissedAlertNotification(activity: Activity, reminder: Reminder) {
            val intent = Utils.getAlertIntent(reminder.id!!).apply { putExtra(FROM_NOTIFICATION, true) }
            val pendingIntent: PendingIntent = PendingIntent.getActivity(App.applicationContext(), reminder.id!!.toInt(), intent, FLAG_UPDATE_CURRENT)


            val title = "You missed a reminder"
            val contentText = reminder.text.ifEmpty { "Voice reminder" }
//            val missedTime = Calendar.getInstance().time
            val missedTime = Calendar.getInstance().apply { timeInMillis = reminder.nextAlarm() }.time
            val nextTime = Calendar.getInstance().apply { timeInMillis = reminder.nextAlarmWithSnooze() }.time

            val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
            var missedText = "Was set to ${formatter.format(missedTime)}"
            if (reminder.snoozeCount < 5) missedText += ", next alert on ${formatter.format(nextTime)}"

            val notification = NotificationCompat.Builder(activity, CHANNEL_ID)
                .setSmallIcon(R.drawable.app_icon_blue_cropped_v_cut)
                .setContentTitle(title)
                .setContentText(contentText)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setStyle(
                    NotificationCompat.InboxStyle()
                        .addLine(contentText)
                        .addLine(missedText)
                ).build()

            NotificationManagerCompat.from(activity).notify(reminder.id!!.toInt(), notification)
            FlowLog.notificationIsShowing(reminder, missedText)
        }

        //cancel the current notification(if exists) and create a new one


        fun cancelMissedAlertNotification(activity: Activity, reminder: Reminder) {
            NotificationManagerCompat.from(activity).cancel(reminder.id!!.toInt())
            FlowLog.notificationDismissed(reminder)
        }

    }
}
