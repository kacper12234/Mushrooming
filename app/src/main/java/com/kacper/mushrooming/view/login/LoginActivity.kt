package com.kacper.mushrooming.view.login

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.kacper.mushrooming.R
import com.kacper.mushrooming.api.dto.AuthenticationResponse
import com.kacper.mushrooming.databinding.ActivityLoginBinding
import com.kacper.mushrooming.view.login.api.LoginController
import com.kacper.mushrooming.view.main.MainActivity
import com.kacper.mushrooming.view.register.RegisterActivity
import java.net.InetAddress
import java.net.UnknownHostException
import java.time.Instant
import java.util.*

class LoginActivity : AppCompatActivity(), LoginInterface {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var preferences: SharedPreferences
    private lateinit var controller: LoginController
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        controller = LoginController()
        try {
            val checkConnection = Thread {
                try {
                    InetAddress.getByName("mushrooming.azurewebsites.net")
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                    try {
                        InetAddress.getByName("google.com")
                        Toast.makeText(this, R.string.server_unavaiable, Toast.LENGTH_LONG).show()
                    } catch (e1: UnknownHostException) {
                        e1.printStackTrace()
                        Toast.makeText(this, R.string.no_network, Toast.LENGTH_LONG).show()
                    }
                    finishActivity(-1)
                }
            }
            checkConnection.start()
            checkConnection.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        setupBindings()
        setupRegisterClick()
        setupButtonClick()
        preferences = getSharedPreferences("Mushrooming", MODE_PRIVATE)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) requestPermissions()
        else checkCache()
    }


    private fun setupBindings(){
        val activityBinding : ActivityLoginBinding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_login
        )
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        activityBinding.model = loginViewModel
    }

    private fun setupRegisterClick(){
        loginViewModel.registerClick.observe(this,{
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)})
    }

    private fun setupButtonClick() {
        loginViewModel.buttonClick.observe(this,
            {
                loginViewModel.loginModel.formVisible = View.INVISIBLE
                controller.login(it.login,it.password, this)})
    }

    override fun login(login: String?) {
        intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        Toast.makeText(this@LoginActivity, getString(R.string.welcome) + login, Toast.LENGTH_LONG).show()
    }

    override fun storeCred(
        response: AuthenticationResponse?,
        login: String?,
        password: String?,
        sessionId: String?
    ) {
        val editor = preferences.edit()
        if (password != null) {
            editor.putString("login", login)
            editor.putString("password", password)
        }
        editor.putString("refreshToken", response!!.refreshToken)
        editor.putString("token", response.authenticationToken)
        editor.putString("expireAt", response.expiresAt)
        if (sessionId != null) editor.putString("JSESSIONID", sessionId)
        editor.apply()
    }

    override fun loginFail(string: Int) {
        Snackbar.make(
            findViewById<View>(android.R.id.content).rootView,
            string,
            Snackbar.LENGTH_INDEFINITE
        ).show()
        loginViewModel.loginModel.formVisible = View.VISIBLE
    }

    override fun setToken(response: AuthenticationResponse?) {
        response!!.authenticationToken = preferences.getString("token", null)
    }

    override fun showSnackbar() {
        Timer().scheduleAtFixedRate(object : TimerTask() {
            var time = 60
            var snackbar = Snackbar.make(
                findViewById<View>(android.R.id.content).rootView,
                "",
                Snackbar.LENGTH_INDEFINITE
            )

            override fun run() {
                runOnUiThread {
                    snackbar.setText(String.format(getString(R.string.login_failed_server),time))
                }
                if (!snackbar.isShown) snackbar.show()
                time -= 1
                if (time == 0) {
                    snackbar.dismiss()
                    cancel()
                }
            }
        }, 0, 1000)
    }

   private fun checkCache(){
       if (intent.getBooleanExtra("logout", false)) {
           preferences.edit().clear().apply()
           controller.resetTask()
       } else {
           val login = preferences.getString("login", null)
           if (login != null) {
               val expiresAt = Instant.parse(preferences.getString("expireAt", null))
               println(expiresAt.toString())
               loginViewModel.loginModel.formVisible = View.INVISIBLE
               if (expiresAt.isAfter(Instant.now())) {
                   val refreshToken = preferences.getString("refreshToken", null)
                   val sessionId = preferences.getString("JSESSIONID", null)
                   controller!!.reconnect(login, refreshToken!!, sessionId!!, this)
               } else {
                   val password = preferences.getString("password", null)
                   controller!!.login(login, password!!, this)
               }
           }
       }
    }

    private fun requestPermissions(){
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()
            requestPermissions()
        }
        else checkCache()
    }
}