package com.kacper.mushrooming.utils

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField

object Validate {
    fun checkField(condition: Boolean, error: ObservableField<Int>, errorId: Int): Boolean {
        return if (condition) {
            error.set(null)
            true
        } else {
            error.set(errorId)
            false
        }
    }

    @JvmStatic
    @BindingAdapter("error")
    fun setError(editText: EditText, strOrResId: Any?) {
        if (strOrResId is Int) {
            editText.error = editText.context.getString((strOrResId as Int?)!!)
        } else {
            editText.error = strOrResId as String?
        }
    }
}