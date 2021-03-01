package com.kacper.mushrooming.utils

import androidx.annotation.StringRes
import com.kacper.mushrooming.App

object Strings {
    fun get(@StringRes stringRes: Int): String = App.instance.getString(stringRes)
}