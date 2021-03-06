package com.hirshler.remindme

import android.util.Log
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.hirshler.remindme.model.Reminder
import java.text.SimpleDateFormat
import java.util.*

class FlowLog {
    companion object {
        const val tag = "FlowLog"

        //every log is (id, text, time)
        //new reminder added to DB
        fun newReminder(reminder: Reminder) {
            logDebug(reminder, "New reminder added to DB")
        }

        //new reminder alert in AM
        fun setAlert(reminder: Reminder, alertTime: Long) {
            logDebug(reminder, "Alert set to AlertManager to ${Utils.fullDateByMilliseconds(alertTime)}")
        }

        //reminder updated in DB
        fun reminderUpdate(reminder: Reminder) {
            logDebug(reminder, "Reminder updated in DB")
        }

        //alert updated in AM
        fun alertUpdate(reminder: Reminder) {
            logDebug(reminder, "Alert updated in AlertManager")
        }

        //reminder deleted
        fun reminderDelete(reminder: Reminder) {
            logDebug(reminder, "Reminder deleted from DB")
        }

        //alert canceled in AM
        fun alertCancel(reminder: Reminder) {
            logDebug(reminder, "Alert canceled in AlertManager")
        }

        //reminder edit start
        fun reminderEditStart(reminder: Reminder) {
            logDebug(reminder, "Reminder is in update screen")
        }

        //alert broadcast
        fun alertBroadcastIsCalled() {
            logDebug(null, "Alert broadcast is called")
        }

        fun alertBroadcastGotReminder(id: Long, reminder: Reminder?) {
            logDebug(reminder, "Alert broadcast got reminder by id: $id")
        }

        //alert is showing
        fun alertIsAlerting(reminder: Reminder, fromAlertManager: Boolean) {
            logDebug(reminder, "Alert is alerting ${if (fromAlertManager) "from alert manager" else "from notification click"}")
        }

        //user click
        fun userClickDismissButton() {
            logDebug( null, "User click - Dismiss Button")
        }

        //alert dismissed
        fun alertDismissed(reminder: Reminder?) {
            logDebug(reminder, "Alert dismissed")
        }

        //alert snoozed
        fun alertSnoozed(reminder: Reminder?) {
            logDebug(reminder, "Alert snoozed")
        }

        fun notificationIsShowing(reminder: Reminder?, missedText: String) {
            logDebug(reminder, "Notification is showing: \n$missedText")
        }

        fun notificationDismissed(reminder: Reminder) {
            logDebug(reminder, "Notification canceled by AlertActivity")
        }

        fun bootReceiverCalled() {
            logDebug(null, "BootReceiver called")
        }

        fun reminderSetToNewDateTime(reminder: Reminder?, newDateTime: Calendar?) {
            logDebug(reminder, "Reminder set to new date time by DateTimeDialog: ${newDateTime?.let { Utils.fullDateByMilliseconds(it.timeInMillis) }}")
        }

        fun alertBroadcastReminderOutOfRange(reminder: Reminder) {
            logDebug(reminder, "Reminder is late more than an hour")
        }


        private fun logDebug(reminder: Reminder?, message: String) {
            if (AppSettings.getUserName() == "")
                return

            var id = ""
            var time = ""
            var text = ""
            var reminderDetails = ""
            reminder?.let {
                id = "id: ${it.id},"
                time = "time: ${Utils.fullDateByMilliseconds(it.nextAlarmWithSnooze())},"
                text = "text: ${it.text}"
                reminderDetails = Gson().toJson(it)
            }

            val logText = "$id $message \n$text $time \n$reminderDetails"
            Log.d(tag, logText)

            val database = Firebase.database("https://remind-me-2021-default-rtdb.europe-west1.firebasedatabase.app/")
            val myRef = database.getReference(AppSettings.getUserName())
            myRef.child(getDatabaseChildRef()).setValue(logText)

        }

        private fun getDatabaseChildRef(): String {
            val formatter = SimpleDateFormat("yyyy/MM/dd/ HH:mm:ss:SSS", Locale.getDefault())
            val cal = Calendar.getInstance().time
            return formatter.format(cal)
        }




    }
}