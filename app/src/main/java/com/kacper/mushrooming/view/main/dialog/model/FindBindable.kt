package com.kacper.mushrooming.view.main.dialog.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.ObservableField
import com.kacper.mushrooming.BR
import com.kacper.mushrooming.R
import com.kacper.mushrooming.utils.Validate

class FindBindable: BaseObservable() {
    var species = ""
    set(value) {
        field = value
        notifyPropertyChanged(BR.valid)
    }

    var speciesError = ObservableField<Int>()

    @Bindable
    fun isValid() : Boolean{
        return Validate.checkField(isSpeciesValid(),speciesError, R.string.no_species)
    }

    private fun isSpeciesValid() : Boolean{
        return species.isNotEmpty()
    }
}