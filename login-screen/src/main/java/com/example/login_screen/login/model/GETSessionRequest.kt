package com.example.login_screen.login.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GETSessionRequest (
    @SerializedName("deviceId")
    val deviceId: String
): Parcelable