package com.kacper.mushrooming.view.main.dialog.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import com.kacper.mushrooming.BR
import com.kacper.mushrooming.R
import com.kacper.mushrooming.utils.Validate.checkField

class VisitBindable: BaseObservable() {
    var state = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.valid)
        }
    var count = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.valid)
        }
    
    var stateError = ObservableField<Int>()
    var countError = ObservableField<Int>()

    @Bindable
    fun isValid(): Boolean{
        return checkField(isStateValid(),stateError, R.string.no_state)
                && checkField(isCountValid(),countError,R.string.no_count)
    }

    private fun isCountValid(): Boolean{
        return count.isNotEmpty() && count.toInt() > 0
    }

    private fun isStateValid(): Boolean {
        return state.isNotEmpty()
    }
}