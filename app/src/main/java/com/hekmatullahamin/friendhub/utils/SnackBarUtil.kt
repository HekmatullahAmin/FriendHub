package com.hekmatullahamin.friendhub.utils

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.hekmatullahamin.friendhub.R
import kotlinx.coroutines.flow.combineTransform

object SnackBarUtil {
    fun showSnackBar(
        context: Context,
        view: View,
        message: String,
        isErrorMessage: Boolean,
        duration: Int = Snackbar.LENGTH_LONG
    ) {
        val snackBar = Snackbar.make(view, message, duration)
        val snackBarView = snackBar.view
        if (isErrorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(context, R.color.red)
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(context, R.color.green)
            )
        }
        snackBar.show()
    }
}