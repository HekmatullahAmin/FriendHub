package com.hekmatullahamin.friendhub.utils

import android.app.Dialog
import android.content.Context
import android.widget.TextView
import com.hekmatullahamin.friendhub.R

object ProgressBarUtil {
    private var progressBar: Dialog? = null

    fun showProgressBar(
        context: Context,
        message: String = context.resources.getString(R.string.progress_bar_text)
    ) {
        progressBar = Dialog(context)
        progressBar?.setContentView(R.layout.custom_progress_bar)
        val progressbarMessage = progressBar?.findViewById<TextView>(R.id.progressBarMessage)
        progressbarMessage?.text = message
        progressBar?.setCancelable(false)
        progressBar?.setCanceledOnTouchOutside(false)
        progressBar?.show()
    }

    fun hideProgressBar() {
        progressBar?.dismiss()
    }
}