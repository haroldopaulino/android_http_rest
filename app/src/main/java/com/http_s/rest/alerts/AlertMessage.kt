package com.http_s.rest.alerts

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.http_s.rest.R
import com.http_s.rest.data.SqliteCore

internal abstract class AlertMessage(
    private val activity: Activity?,
    val alert: AlertDialog.Builder,
    private val view: View,
    private val title: String,
    private val message: String
) {
    private val sqliteCore = SqliteCore(activity)
    private lateinit var optionDialog: AlertDialog

    init {
        buildLayout()
    }

    private fun buildLayout() {
        val alertTitle = view.findViewById<TextView>(R.id.alertTitle)
        alertTitle.text = title

        val messageTextView = view.findViewById<TextView>(R.id.message)
        messageTextView.text = message

        alert.setView(view)
        optionDialog = alert.create()
        optionDialog.setOnDismissListener {
            sqliteCore.createSetting(
                "ERROR_DIALOG_CURRENTLY_SHOWING",
                "NO"
            )
        }

        //OptionDialog.setCancelable(false);//ALLOW THE DEVICE'S BACK BUTTON TO CLOSE THE ALERT OR NOT
        optionDialog.window!!
            .setBackgroundDrawable(ColorDrawable(Color.parseColor("#00ffffff")))
        optionDialog.show()

        //val displayMetrics: WindowMetrics = activity!!.getSystemService(WindowManager::class.java).currentWindowMetrics

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(optionDialog.window!!.attributes)

        optionDialog.window!!.attributes = layoutParams

        view.findViewById<View>(R.id.backImageContainer)
            .setOnClickListener { optionDialog.dismiss() }

        view.findViewById<View>(R.id.backImage)
            .setOnClickListener { optionDialog.dismiss() }

        view.findViewById<View>(R.id.okButton)
            .setOnClickListener { optionDialog.dismiss() }
    }
}
