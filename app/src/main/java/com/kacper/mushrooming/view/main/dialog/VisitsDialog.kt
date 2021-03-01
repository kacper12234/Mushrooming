package com.kacper.mushrooming.view.main.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.DialogFragment
import com.kacper.mushrooming.R


class VisitsDialog(private val strings: List<String>) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_visits, null)
        val listView = view.findViewById<ListView>(R.id.visits)
        val adapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, strings)
        listView.adapter = adapter
        return AlertDialog.Builder(activity)
                .setView(view)
                .setNeutralButton(R.string.close) { _: DialogInterface?, _: Int -> dismiss() }
                .create()
    }
}