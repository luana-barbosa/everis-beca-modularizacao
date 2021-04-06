package com.niemietz.everis.beca.core.com.niemietz.everis.beca.modularizacao.login.textwatcher

import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import androidx.appcompat.widget.AppCompatEditText

class LoginTextWatcher(private val editText: AppCompatEditText): TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        editText.transformationMethod = PasswordTransformationMethod.getInstance()
    }

    override fun afterTextChanged(s: Editable?)  = Unit
}