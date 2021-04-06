package com.example.login_screen.login.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.ViewModelProvider
import com.example.login_screen.R
import com.niemietz.everis.beca.core.BackendClient
import com.niemietz.everis.beca.core.com.niemietz.everis.beca.modularizacao.login.constants.LoginConstants.EXTRA_ERROR_KEY
import com.niemietz.everis.beca.core.com.niemietz.everis.beca.modularizacao.login.constants.LoginConstants.EXTRA_RESULT_KEY
import com.niemietz.everis.beca.core.com.niemietz.everis.beca.modularizacao.login.constants.LoginConstants.LOGIN_RESULT_CODE
import com.niemietz.everis.beca.core.com.niemietz.everis.beca.modularizacao.login.textwatcher.LoginTextWatcher
import com.example.login_screen.login.events.LoginEvents
import com.example.login_screen.login.events.LoginInteractor
import com.example.login_screen.login.model.KeyboardItem
import com.example.login_screen.login.states.LoginStates
import com.example.login_screen.login.interfaces.LoginAPI
import com.example.login_screen.login.repository.LoginRepository
import com.example.login_screen.login.ui.viewmodel.LoginViewModel
import com.example.login_screen.login.ui.viewmodel.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {
    private val etPassword: AppCompatEditText by lazy { findViewById(R.id.et_password) }
    private val btLogin: AppCompatButton by lazy { findViewById(R.id.bt_login) }
    private val btn01: AppCompatButton by lazy { findViewById(R.id.btn_0_1) }
    private val btn23: AppCompatButton by lazy { findViewById(R.id.btn_2_3) }
    private val btn45: AppCompatButton by lazy { findViewById(R.id.btn_4_5) }
    private val btn67: AppCompatButton by lazy { findViewById(R.id.btn_6_7) }
    private val btn89: AppCompatButton by lazy { findViewById(R.id.btn_8_9) }
    private val clKeyboard: ConstraintLayout by lazy { findViewById(R.id.cl_keyboard) }
    private val clLoadingBackground: ConstraintLayout by lazy { findViewById(R.id.cl_loading_background) }
    private val gpLoading: Group by lazy { findViewById(R.id.gp_loading) }

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViewModel()
        initEventsObservers()
        initStatesObservers()

        setListeners()

        gpLoading.visibility = View.GONE
        clKeyboard.visibility = View.GONE
    }

    private fun initViewModel() {
        val api = BackendClient.api(LoginAPI::class.java)
        val repository = LoginRepository(api)

        viewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(
                this,
                repository
            )
        ).get(LoginViewModel::class.java)
    }

    private fun initEventsObservers() {
        viewModel.events.observe(this, {
            when(it) {
                is LoginEvents.StartLoading -> showLoading(true)
                is LoginEvents.NoInternet -> Toast.makeText(
                    this,
                    CHECK_YOUR_CONNECTION_WARNING,
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun initStatesObservers() {
        viewModel.states.observe(this, {
            when(it) {
                is LoginStates.GetSessionResult -> setKeyboard(it.keyboard)
                is LoginStates.GetSessionError -> onError(it.exception)

                is LoginStates.AuthenticateResult -> afterLogin(it.result)
                is LoginStates.AuthenticateError -> onError(it.exception)
            }
        })
    }

    private fun setListeners() {
        this.btLogin.setOnClickListener { button ->
            getKeyboard()
            button.setOnClickListener {
                val password = this.etPassword.text.toString()
                realizeLogin(password)
            }
        }

        this.etPassword.addTextChangedListener(LoginTextWatcher(this.etPassword))

        this.clLoadingBackground.setOnClickListener {
            // Keep it to make everything unclickable while loading
        }
    }

    private fun showLoading(show: Boolean) {
        when (show) {
            true -> gpLoading.visibility = View.VISIBLE
            else -> gpLoading.visibility = View.GONE
        }
    }

    private fun getKeyboard() {
        viewModel.interact(LoginInteractor.GetSession)
    }

    private fun realizeLogin(password: String) {
        viewModel.interact(LoginInteractor.Authenticate(password))
    }

    private fun setKeyboard(keyboard: ArrayList<KeyboardItem>) {
        keyboard.forEach { item ->
            item.numbers.forEach { number ->
                val btn = when (number) {
                    0, 1 -> btn01
                    2, 3 -> btn23
                    4, 5 -> btn45
                    6, 7 -> btn67
                    8, 9 -> btn89
                    else -> null
                }

                btn?.setOnClickListener {
                    etPassword.append(item.letter)
                }
            }
        }

        showLoading(false)
        clKeyboard.visibility = View.VISIBLE
    }

    private fun afterLogin(loginOk: Boolean) {
        if (loginOk) {
            this.intent.putExtra(EXTRA_RESULT_KEY, true)
            this.intent.putExtra(EXTRA_ERROR_KEY, "")

            setResult(LOGIN_RESULT_CODE, this.intent)

            finish()
        } else {
            onError(Exception())
        }
    }

    private fun onError(exception: Exception) {
        showLoading(false)

        this.intent.putExtra(EXTRA_RESULT_KEY, false)
        this.intent.putExtra(EXTRA_ERROR_KEY, exception.message)

        finish()
    }

    companion object {
        private const val CHECK_YOUR_CONNECTION_WARNING = "Check your internet connection!"
    }
}