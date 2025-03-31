package com.http_s.rest.alerts

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.SystemClock
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.webkit.WebView
import android.widget.TextView
import com.http_s.rest.R
import org.json.JSONException
import org.json.JSONObject

internal abstract class ProcessingAlert(
    private val activity: Activity,
    private val alert: AlertDialog.Builder,
    private val view: View
) {
    private lateinit var optionDialog: AlertDialog
    private var startTime: Long = 0
    private val handler = Handler()
    private lateinit var alertTimer: TextView

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            val millisecondTime = SystemClock.uptimeMillis() - startTime
            var seconds = (millisecondTime / 1000).toInt()
            val minutes = seconds / 60
            seconds %= 60
            val milliSeconds = (millisecondTime % 1000).toInt()
            if (minutes == 0) {
                alertTimer.text =
                    seconds.toString() + "s, " + String.format("%03d", milliSeconds) + "ms"
            } else {
                alertTimer.text = minutes.toString() + "m, " +
                        seconds + "s, " + String.format("%03d", milliSeconds) + "ms"
            }

            handler.postDelayed(this, 0)
        }
    }

    init {
        buildLayout()
    }

    private fun buildLayout() {
        try {
            alertTimer = view.findViewById(R.id.alertTimer)
            view.findViewById<View>(R.id.cancelRequestButton).setOnClickListener {
                try {
                    callback(JSONObject().put("response", "CANCEL_REQUEST"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                optionDialog.dismiss()
            }

            val loadingWebView = view.findViewById<WebView>(R.id.loadingWebView)
            loadingWebView.settings
            loadingWebView.setBackgroundColor(Color.TRANSPARENT)
            loadingWebView.loadUrl("file:///android_asset/loading.html")

            alert.setView(view)
            optionDialog = alert.create()
            optionDialog.setOnDismissListener { }

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

            startStopWatch()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getAlert(): AlertDialog.Builder {
        return alert
    }

    open fun callback(inputResponse: JSONObject) {}

    fun dismiss() {
        optionDialog.dismiss()
    }

    private fun startStopWatch() {
        startTime = SystemClock.uptimeMillis()
        handler.postDelayed(runnable, 0)
    }
}
