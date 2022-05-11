package com.hirshler.remindme.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.hirshler.remindme.activities.MainActivity
import com.hirshler.remindme.activities.MainActivity.Companion.REMINDER_TO_EDIT
import com.hirshler.remindme.model.Reminder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class MainActivityFragment : Fragment() {

    val mainActivity get() = requireActivity() as MainActivity

    fun refreshActivity(
        goToScreen: String? = null, reminderToEdit: Reminder? = null, options: Bundle? = null
    ) {

        requireActivity().let {


            val intent = Intent(it, MainActivity::class.java)
                .apply {

                    if (goToScreen != null) putExtra(goToScreen, true)

                    if (reminderToEdit != null) putExtra(REMINDER_TO_EDIT, reminderToEdit.toString())

                }

            if (options != null) {
                startActivity(intent, options)
                GlobalScope.launch {
                    delay(1000)
                    it.finish()
                }
            } else {
                startActivity(intent)
                it.finish()
            }


        }

    }
}