package com.http_s.rest.alerts

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.TextView
import com.http_s.rest.R
import com.http_s.rest.Utilities

internal abstract class AlertAbout(
    private val activity: Activity,
    val alert: AlertDialog.Builder,
    private val view: View
) {
    private val utilities = Utilities(activity)
    private lateinit var optionDialog: AlertDialog
    private var alertTitle: TextView? = null

    init {
        buildLayout()
    }

    private fun buildLayout() {
        alertTitle = view.findViewById(R.id.alertTitle)

        view.findViewById<View>(R.id.backImageContainer)
            .setOnClickListener { arg0: View? -> optionDialog.dismiss() }

        view.findViewById<View>(R.id.backImage)
            .setOnClickListener { optionDialog.dismiss() }

        val webView = view.findViewById<WebView>(R.id.webView)
        webView.settings.builtInZoomControls = false
        webView.setBackgroundColor(0x00000000)
        val fileContents =
            utilities.getFileContentFromAssets("PORTFOLIO_FILENAME")
        webView.loadDataWithBaseURL(
            "file:///android_asset/",
            fileContents!!, "text/html", "utf-8", null
        )

        alert.setView(view)
        optionDialog = alert.create()
        optionDialog.setOnDismissListener { }

        //OptionDialog.setCancelable(false);//ALLOW THE DEVICE'S BACK BUTTON TO CLOSE THE ALERT OR NOT
        optionDialog.window!!
            .setBackgroundDrawable(ColorDrawable(Color.parseColor("#00ffffff")))
        optionDialog.show()

        val displayMetrics = DisplayMetrics()
        activity.windowManager. defaultDisplay.getMetrics(displayMetrics)

        val displayWidth = displayMetrics.widthPixels
        val displayHeight = displayMetrics.heightPixels
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.copyFrom(optionDialog.window!!.attributes)

        val dialogWindowWidth = (displayWidth * 0.8f).toInt()
        val dialogWindowHeight = (displayHeight * 0.8f).toInt()

        layoutParams.width = dialogWindowWidth
        layoutParams.height = dialogWindowHeight

        optionDialog.window!!.attributes = layoutParams

        loadTitles()
    }

    private fun loadTitles() {
        alertTitle!!.text = "PORTFOLIO_FILENAME"
    }
}
