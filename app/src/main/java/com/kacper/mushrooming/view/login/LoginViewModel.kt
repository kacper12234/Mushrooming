package com.kacper.mushrooming.view.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kacper.mushrooming.view.login.model.LoginBindable
import com.kacper.mushrooming.utils.SingleLiveEvent


class LoginViewModel : ViewModel() {
    var loginModel = LoginBindable()

    var buttonClick = MutableLiveData<LoginBindable>()
    var registerClick = SingleLiveEvent<Boolean>()


    fun onRegisterClick(){
        registerClick.value = true
    }

    fun onLoginClick(){
        if (loginModel.isValid()) buttonClick.value = loginModel
    }
    
}