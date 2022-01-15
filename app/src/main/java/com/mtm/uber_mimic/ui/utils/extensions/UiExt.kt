package com.mtm.uber_mimic.ui

import android.app.Activity
import android.view.View
import android.content.Context.INPUT_METHOD_SERVICE
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.core.content.ContextCompat

import androidx.core.content.ContextCompat.getSystemService
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.common.internal.Preconditions.checkMainThread
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.onStart
import java.lang.Exception


fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun Activity.closeKeyboard() {
    try {
        val imm: InputMethodManager? = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
    } catch (e: Exception) {
    }
}

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        val listener = doOnTextChanged { text, _, _, _ ->
            trySend(text)
        }
        awaitClose { removeTextChangedListener(listener) }
    }.cancellable()
}