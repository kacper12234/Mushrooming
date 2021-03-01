package com.kacper.mushrooming.view.main.dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kacper.mushrooming.view.main.dialog.model.FindBindable

class FindDialogViewModel : ViewModel() {
    var findModel = FindBindable()

    var buttonClick = MutableLiveData<FindBindable>()

    fun onButtonClick(){
        if (findModel.isValid()) buttonClick.value = findModel
    }
}