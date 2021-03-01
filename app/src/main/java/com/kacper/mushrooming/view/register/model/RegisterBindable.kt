package com.kacper.mushrooming.view.register.model

import android.util.Patterns
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import com.kacper.mushrooming.BR
import com.kacper.mushrooming.R
import com.kacper.mushrooming.utils.Validate
import com.kacper.mushrooming.utils.Validate.checkField

class RegisterBindable : BaseObservable() {
    var name = ""
    set(name) {
        field = name
        notifyPropertyChanged(BR.valid)
    }
    var surname = ""
        set(name) {
            field = name
            notifyPropertyChanged(BR.valid)
        }
    var login = ""
        set(surname) {
            field = surname
            notifyPropertyChanged(BR.valid)
        }
    var password = ""
        set(password) {
            field = password
            notifyPropertyChanged(BR.valid)
        }
    var email = ""
        set(email) {
            field = email
            notifyPropertyChanged(BR.valid)
        }
    var passwordRepeat = ""
        set(password) {
            field = password
            notifyPropertyChanged(BR.valid)
        }

    @Bindable
    var progressBarVisible = View.GONE
    set(value) {
        field = value
        notifyPropertyChanged(BR.progressBarVisible)
    }

    var nameError =  ObservableField<Int>()
    var surnameError =  ObservableField<Int>()
    var loginError =  ObservableField<Int>()
    var passError =  ObservableField<Int>()
    var emailError = ObservableField<Int>()
    var passRepeatError = ObservableField<Int>()

    @Bindable
    fun isValid(): Boolean {
        return checkField(isNameValid(name), nameError, R.string.invalid_name)
                && checkField(isNameValid(surname), surnameError, R.string.invalid_surname)
                && checkField(isNameValid(login), loginError, R.string.invalid_username)
                && checkField(isPasswordValid(), passError, R.string.invalid_password)
                && checkField(isEmailValid(), emailError, R.string.invalid_email)
                && checkField(doPasswordsMatch(), passRepeatError, R.string.passwords_match_fail)
    }

    private fun isNameValid(name : String): Boolean {
        return name.trim { it <= ' ' }.isNotEmpty()
    }

    private fun isPasswordValid(): Boolean {
        return password.trim { it <= ' ' }.length > 5
    }

    private fun isEmailValid(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun doPasswordsMatch(): Boolean{
        return password == passwordRepeat
    }

}