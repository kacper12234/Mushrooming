package com.kacper.mushrooming.view.main.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.kacper.mushrooming.R
import com.kacper.mushrooming.databinding.DialogVisitBinding

class VisitDialog(private val dialogInterface: DialogInterface, private val findId: Long) :
    DialogFragment() {
    private lateinit var dialogViewModel: VisitDialogViewModel
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = setupBindings()
        setupButtonClick()
        return AlertDialog.Builder(activity)
            .setTitle(R.string.visit_dialog)
            .setView(view)
            .setPositiveButton(R.string.add) { _: android.content.DialogInterface?, _: Int -> dialogViewModel.onButtonClick() }
            .create()
    }

    private fun setupBindings(): View {
        val binding: DialogVisitBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_visit,
            null,
            false
        )
        dialogViewModel = ViewModelProvider(this).get(VisitDialogViewModel::class.java)
        binding.model = dialogViewModel
        return binding.root
    }

    private fun setupButtonClick() {
        dialogViewModel.buttonClick.observe(this, {
            dialogInterface.addVisit(it.state, it.count.toInt(), findId)
        })
    }

}