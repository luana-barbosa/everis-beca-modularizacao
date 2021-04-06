package com.example.login_screen.login.ui.viewmodel

import android.content.Context
import android.provider.Settings.Secure
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.niemietz.everis.beca.core.InternetChecker.isConnected2Internet
import com.niemietz.everis.beca.core.Session
import com.example.login_screen.login.events.LoginEvents
import com.example.login_screen.login.events.LoginInteractor
import com.example.login_screen.login.states.LoginStates
import com.example.login_screen.login.repository.LoginRepository
import com.example.login_screen.login.model.AuthenticateRequest
import com.example.login_screen.login.model.AuthenticateResponseContent
import com.example.login_screen.login.model.GETSessionRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

class LoginViewModel(
    private val context: Context,
    private var repository: LoginRepository,
    private val checkInternetConnection: Boolean = true
): ViewModel() {
    private val job: Job = Job()
    private val currentJob: CoroutineContext =
        Dispatchers.Main + job
    val events = MutableLiveData<LoginEvents>()
    val states = MutableLiveData<LoginStates>()

    fun interact(interaction: LoginInteractor) {
        when (interaction) {
            is LoginInteractor.GetSession -> getSession()
            is LoginInteractor.Authenticate -> authenticate(interaction.password)
        }
    }

    private fun getSession() {
        if (isConnected2Internet(context)) {
            events.value = LoginEvents.StartLoading

            CoroutineScope(currentJob).launch {
                try {
                    val deviceId = Secure.getString(
                        context.contentResolver,
                        Secure.ANDROID_ID
                    )

                    val response = repository.getSession(
                        GETSessionRequest(
                            deviceId
                        )
                    )

                    setCoreSession(response.sessionId)

                    states.value = LoginStates.GetSessionResult(
                        response.keyboard
                    )
                } catch (ex: Exception) {
                    states.value = LoginStates.GetSessionError(ex)
                }
            }
        } else {
            events.value = LoginEvents.NoInternet
        }
    }

    private fun authenticate(password: String) {
        if (isConnected2Internet(context)) {
            events.value = LoginEvents.StartLoading

            CoroutineScope(currentJob).launch {
                try {
                    Session.id?.let {
                        val sessionId = it

                        val response = repository.authenticate(
                            AuthenticateRequest(
                                sessionId,
                                password
                            )
                        )

                        setCoreUserContent(response.content)

                        states.value = LoginStates.AuthenticateResult(
                            response.result
                        )
                    } ?: run {
                        events.value = LoginEvents.NoSession
                    }
                } catch (ex: Exception) {
                    states.value = LoginStates.GetSessionError(ex)
                }
            }
        } else {
            events.value = LoginEvents.NoInternet
        }
    }

    private fun setCoreSession(sessionId: String) {
        Session.id = sessionId
    }

    private fun setCoreUserContent(userContent: AuthenticateResponseContent) {
        Session.content = JSONObject(Gson().toJson(userContent))
    }
}