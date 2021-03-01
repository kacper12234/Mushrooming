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
import com.google.android.gms.maps.model.LatLng
import com.kacper.mushrooming.databinding.DialogFindBinding

class FindDialog(private val dialogInterface: DialogInterface, private val loc: LatLng) : DialogFragment() {
    private lateinit var dialogViewModel : FindDialogViewModel
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = setupBindings()
        setupButtonClick()
        return AlertDialog.Builder(activity)
                .setTitle(R.string.find_dialog)
                .setView(view)
                .setPositiveButton(R.string.add) { _: android.content.DialogInterface?, _: Int -> dialogViewModel.onButtonClick() }
                .setNegativeButton(R.string.cancel) { _: android.content.DialogInterface?, _: Int -> dismiss() }
                .create()
    }

    private fun setupBindings(): View {
        val binding : DialogFindBinding = DataBindingUtil.inflate(LayoutInflater.from(context),R.layout.dialog_find,null,false)
        dialogViewModel = ViewModelProvider(this).get(FindDialogViewModel::class.java)
        binding.model = dialogViewModel
        return binding.root
    }

    private fun setupButtonClick(){
        dialogViewModel.buttonClick.observe(this,{
            dialogInterface.addFind(it.species,loc.latitude, loc.longitude)
        })
    }

}