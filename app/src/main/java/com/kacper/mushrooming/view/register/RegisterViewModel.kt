package com.kacper.mushrooming.view.register

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kacper.mushrooming.view.register.model.RegisterBindable

class RegisterViewModel : ViewModel() {
    var registerModel = RegisterBindable()

    var buttonClick = MutableLiveData<RegisterBindable>()

    fun onRegisterClick(){
        if (registerModel.isValid()) buttonClick.value = registerModel
    }

}