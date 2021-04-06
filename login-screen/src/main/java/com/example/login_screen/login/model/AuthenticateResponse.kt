package com.example.login_screen.login.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class AuthenticateResponse (
    @SerializedName("result")
    val result: Boolean,
    @SerializedName("content")
    val content: AuthenticateResponseContent
)

@Parcelize
data class AuthenticateResponseContent (
    @SerializedName("id")
    val id: Int,
    @SerializedName("user")
    val user: String
): Parcelable