package com.example.login_screen.login.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GETSessionResponse (
    @SerializedName("sessionId")
    val sessionId: String,
    @SerializedName("keyboard")
    val keyboard: ArrayList<KeyboardItem>
): Parcelable