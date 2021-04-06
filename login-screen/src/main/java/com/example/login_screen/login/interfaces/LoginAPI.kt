package com.example.login_screen.login.interfaces

import com.example.login_screen.login.constants.APIConstants.AUTHENTICATE
import com.example.login_screen.login.constants.APIConstants.GET_SESSION
import com.example.login_screen.login.model.GETSessionResponse
import com.example.login_screen.login.model.GETSessionRequest
import com.example.login_screen.login.model.AuthenticateResponse
import com.example.login_screen.login.model.AuthenticateRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginAPI {
    @POST(GET_SESSION)
    suspend fun getSession(
        @Body request: GETSessionRequest
    ): GETSessionResponse

    @POST(AUTHENTICATE)
    suspend fun authenticate(
        @Body request: AuthenticateRequest
    ): AuthenticateResponse
}