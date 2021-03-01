package com.kacper.mushrooming.view.login.model

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import com.kacper.mushrooming.R
import com.kacper.mushrooming.BR
import com.kacper.mushrooming.utils.Validate

class LoginBindable : BaseObservable() {
    var login  = ""
    set(login){
        field = login
        notifyPropertyChanged(BR.valid)
    }
    var password = ""
    set(password){
        field = password
        notifyPropertyChanged(BR.valid)
    }

    @Bindable
    var formVisible = View.VISIBLE
    set(value) {
        field = value
        notifyPropertyChanged(BR.formVisible)
    }

    var loginError = ObservableField<Int>()
    var passError = ObservableField<Int>()

    @Bindable
    fun isValid(): Boolean {
        return Validate.checkField(isLoginValid(), loginError, R.string.invalid_username)
                && Validate.checkField(isPasswordValid(), passError, R.string.invalid_password)
    }

    private fun isLoginValid() : Boolean {
        return login.trim { it <= ' ' }.isNotEmpty()
    }

    private fun isPasswordValid() : Boolean{
        return password.trim { it <= ' ' }.length > 5
    }
}