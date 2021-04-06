package com.niemietz.everis.beca.modularizacao.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.niemietz.everis.beca.core.com.niemietz.everis.beca.modularizacao.login.constants.LoginConstants
import com.niemietz.everis.beca.modularizacao.BuildConfig
import com.niemietz.everis.beca.modularizacao.R
import com.example.login_screen.login.ui.LoginActivity

class MainActivity : AppCompatActivity() {
    private val btLogin: AppCompatButton by lazy { findViewById(R.id.bt_login) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setListener()
    }

    private fun setListener() {
        btLogin.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivityForResult(loginIntent, LOGIN_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOGIN_REQUEST_CODE && resultCode == LoginConstants.LOGIN_RESULT_CODE) {
            val loginOk = data?.extras?.getBoolean(LoginConstants.EXTRA_RESULT_KEY)
            if (loginOk == true) {
                val loginOkIntent = Intent(this, LoginOkActivity::class.java)
                startActivity(loginOkIntent)
            } else {
                onError(
                    data?.extras?.getString(LoginConstants.EXTRA_ERROR_KEY) ?: UNKNOWN_LOGIN_ERROR
                )
            }
        }
    }

    private fun onError(error: String, genericError: Boolean = true) {
        Log.e(BuildConfig.LOG_TAG, error)

        Toast.makeText(
            this,
            when (genericError) {
                true -> LOGIN_FAILED_WARNING
                else -> error
            },
            Toast.LENGTH_LONG
        ).show()
    }

    companion object {
        private const val LOGIN_REQUEST_CODE = 1
        private const val UNKNOWN_LOGIN_ERROR = "Unknown login error!"
        private const val LOGIN_FAILED_WARNING = "Login failed!"
    }
}