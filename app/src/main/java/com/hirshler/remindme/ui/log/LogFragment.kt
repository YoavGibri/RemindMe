package com.hirshler.remindme.ui.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hirshler.remindme.R
import com.hirshler.remindme.Utils

class LogFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        readLog(view)

        view.findViewById<Button>(R.id.refreshLog).setOnClickListener {
            readLog(view)
        }

        view.findViewById<Button>(R.id.deleteLog).setOnClickListener {
            Utils.writeToFile("")
            readLog(view)
        }

    }

    private fun readLog(view: View) {
        view.findViewById<TextView>(R.id.textView).text = Utils.readFromFile()
    }
}