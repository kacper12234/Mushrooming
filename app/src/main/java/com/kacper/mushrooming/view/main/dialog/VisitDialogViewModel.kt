package com.kacper.mushrooming.view.main.dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kacper.mushrooming.view.main.dialog.model.VisitBindable

class VisitDialogViewModel: ViewModel() {
    var visitModel = VisitBindable()

    var buttonClick = MutableLiveData<VisitBindable>()

    fun onButtonClick(){
        if (visitModel.isValid()) buttonClick.value = visitModel
    }
}