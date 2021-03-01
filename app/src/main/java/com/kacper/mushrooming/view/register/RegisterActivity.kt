package com.kacper.mushrooming.view.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.kacper.mushrooming.R
import com.kacper.mushrooming.databinding.ActivityRegisterBinding
import com.kacper.mushrooming.view.login.LoginActivity
import com.kacper.mushrooming.view.register.api.RegisterController

class RegisterActivity : AppCompatActivity(), RegisterInterface {
    private lateinit var regViewModel: RegisterViewModel
    private lateinit var controller: RegisterController
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        controller = RegisterController()

setupBindings()
setupButtonClick()
    }

    private fun setupBindings() {
        val activityBinding: ActivityRegisterBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_register
        )
        regViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        activityBinding.model = regViewModel
    }

    private fun setupButtonClick() {
        regViewModel.buttonClick.observe(this,
            {
                regViewModel.registerModel.progressBarVisible = View.VISIBLE
                controller.register(it.login, it.password, it.email, it.name, it.surname, this)
            })
    }

    override fun register() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finishActivity(0)
        Toast.makeText(this@RegisterActivity, R.string.reg_success, Toast.LENGTH_LONG)
    }

    override fun dataAlreadyUsed(message: String) {
        regViewModel.registerModel.progressBarVisible = View.GONE
        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG)
    }

    override fun registerFail() {
        regViewModel.registerModel.progressBarVisible = View.GONE
        Toast.makeText(this@RegisterActivity, R.string.reg_fail, Toast.LENGTH_LONG)
    }
}