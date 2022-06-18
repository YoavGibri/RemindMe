package com.hirshler.remindme.view

import android.app.Activity
import android.app.AlertDialog
import com.hirshler.remindme.AppSettings
import com.hirshler.remindme.BuildConfig
import com.hirshler.remindme.R
import com.hirshler.remindme.databinding.UsernameDialogLayoutBinding

class UserNameDialog {
    companion object {

        fun showUserNameDialog(activity: Activity) {
            val dialogBinding = UsernameDialogLayoutBinding.inflate(activity.layoutInflater)
            dialogBinding.textInput.setText(AppSettings.getUserName())

            val dialog = AlertDialog.Builder(activity)
                .setTitle(R.string.username_dialog_title)
                .setView(dialogBinding.root)
                .setCancelable(false)
                .show()

            dialogBinding.buttonOk.setOnClickListener {
                val userName = dialogBinding.textInput.text.toString().trim()
                if (BuildConfig.DEBUG && userName.isEmpty()) {
                    dialogBinding.infoText.text = activity.getString(R.string.username_dialog_username_empty_message)
                } else {
                    AppSettings.setUserName(userName)
                    dialog.dismiss()
                }
            }

            dialogBinding.textInput.setText(AppSettings.getUserName())

        }

    }


}