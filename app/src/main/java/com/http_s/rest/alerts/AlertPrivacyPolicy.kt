package com.http_s.rest.alerts

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.http_s.rest.R
import com.http_s.rest.Utilities

internal abstract class AlertPrivacyPolicy(
    private val activity: Activity,
    val alert: AlertDialog.Builder,
    private val view: View
) {
    private val utilities = Utilities(activity)
    private lateinit var optionDialog: AlertDialog
    private var alertTitle: TextView? = null
    private var contentsTextview: TextView? = null

    init {
        buildLayout()
    }

    private fun buildLayout() {
        alertTitle = view.findViewById(R.id.alertTitle)

        contentsTextview = view.findViewById(R.id.contentsTextview)

        alert.setView(view)
        optionDialog = alert.create()
        optionDialog.setOnDismissListener(DialogInterface.OnDismissListener { dialogInterface: DialogInterface? -> })

        //OptionDialog.setCancelable(false);//ALLOW THE DEVICE'S BACK BUTTON TO CLOSE THE ALERT OR NOT
        optionDialog.window!!
            .setBackgroundDrawable(ColorDrawable(Color.parseColor("#00ffffff")))
        optionDialog.show()

        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(optionDialog.window!!.attributes)

        val dialogWindowWidth = (displayWidth * 0.8f).toInt()
        val dialogWindowHeight = (displayHeight * 0.8f).toInt()

        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight

        optionDialog.window!!.attributes = layoutParams

        view.findViewById<View>(R.id.backImageContainer)
            .setOnClickListener { arg0: View? -> optionDialog.dismiss() }
        view.findViewById<View>(R.id.backImage)
            .setOnClickListener { arg0: View? -> optionDialog.dismiss() }

        loadTitles()
    }

    private fun loadTitles() {
        contentsTextview!!.text = activity
            .assets
            .open("privacy_policy.txt")
            .bufferedReader().use {
                it.readText()
            }.toString()
        alertTitle!!.text = activity.resources.getString(R.string.privacy_policy)
    }
}

