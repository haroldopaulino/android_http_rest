package com.http_s.rest.alerts

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.google.gson.GsonBuilder
import com.http_s.rest.R
import com.http_s.rest.Utilities
import com.http_s.rest.data.SqliteCore
import org.json.JSONException
import org.json.JSONObject

abstract class HttpResponseAlert(
    activityInput: Activity?,
    alertInput: AlertDialog.Builder,
    viewInput: View,
    responseDataInput: JSONObject?
) {
    private val activity = activityInput!!
    private val sqliteCore = SqliteCore(activityInput)
    private val view = viewInput
    private val utilities = Utilities(activity)
    val alert: AlertDialog.Builder = alertInput
    private lateinit var optionDialog: AlertDialog
    private lateinit var contentsTextView: TextView
    private val responseData = responseDataInput
    private lateinit var bodyButton: Button
    private lateinit var headersButton: Button
    private lateinit var summaryButton: Button

    internal inner class ClickHandler(private var action: String) : View.OnClickListener {
        override fun onClick(view: View) {
            when (action) {
                "BODY" -> loadBody()
                "HEADERS" -> loadHeaders()
                "SUMMARY" -> loadSummary()
                else -> loadBody()
            }
            selectButton(action)
        }
    }

    private fun selectButton(selection: String) {
        bodyButton.setBackgroundResource(R.color.inactiveTabButton)
        headersButton.setBackgroundResource(R.color.inactiveTabButton)
        summaryButton.setBackgroundResource(R.color.inactiveTabButton)
        when (selection) {
            "BODY" -> bodyButton.setBackgroundResource(R.color.activeTabButton)
            "HEADERS" -> headersButton.setBackgroundResource(R.color.activeTabButton)
            "SUMMARY" -> summaryButton.setBackgroundResource(R.color.activeTabButton)
            else -> bodyButton.setBackgroundResource(R.color.activeTabButton)
        }
    }

    init {
        buildLayout()
    }

    private fun buildLayout() {
        try {
            val alertTitle = view.findViewById<TextView>(R.id.alertTitle)
            alertTitle.text =
                responseData!!.getString("response_code_description") + " (" + responseData.getString(
                    "response_code"
                ) + ")"

            bodyButton = view.findViewById(R.id.bodyButton)
            headersButton = view.findViewById(R.id.headersButton)
            summaryButton = view.findViewById(R.id.summaryButton)

            bodyButton.setOnClickListener(ClickHandler("BODY"))
            headersButton.setOnClickListener(ClickHandler("HEADERS"))
            summaryButton.setOnClickListener(ClickHandler("SUMMARY"))

            contentsTextView = view.findViewById(R.id.contentsTextView)
            selectButton("BODY")
            loadBody()

            alert.setView(view)
            optionDialog = alert.create()
            //optionDialog.setOnDismissListener(DialogInterface.OnDismissListener { dialogInterface: DialogInterface? -> })

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
                .setOnClickListener { optionDialog.dismiss() }

            view.findViewById<View>(R.id.backImage)
                .setOnClickListener { optionDialog.dismiss() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadBody() {
        val parsedContents: JSONObject
        var contents = ""
        try {
            contents = responseData!!.getString("response_message")
            parsedContents = JSONObject(responseData.getString("response_message"))
            val gson = GsonBuilder().setPrettyPrinting().create()
            contentsTextView.text = gson.toJson(parsedContents)
        } catch (e: Exception) {
            contentsTextView.text = contents.replace("\\\\".toRegex(), "")
            e.printStackTrace()
        }
    }

    private fun loadHeaders() {
        var headers: JSONObject? = null
        try {
            headers = responseData!!.getJSONObject("headers")
            val contents = StringBuilder()
            val iterator = headers.keys()
            var counter = 0
            while (iterator.hasNext()) {
                val key = iterator.next()
                try {
                    val value = headers.getString(key)
                    counter++
                    contents.append("HEADER #").append(counter).append("\n").append(key)
                        .append("  =  ").append(value).append("\n\n")
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
            contentsTextView.text = contents.toString()
        } catch (e: Exception) {
            if (headers != null) {
                contentsTextView.text = headers.toString().replace("\\\\".toRegex(), "")
            }
            e.printStackTrace()
        }
    }

    private fun loadSummary() {
        try {
            contentsTextView.text =
                (StringBuilder().append("URL:\n")
                    .append(sqliteCore.getSetting("HTTP_URL"))
                    .append("\n\n")
                    .append("Connection Opened:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_CONNECTION_OPENED))
                    .append("\n\n")
                    .append("Outgoing - Header Sent:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_HEADER_SENT))
                    .append("\n\n")
                    .append("Outgoing - Header Count Sent:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_HEADER_COUNT_SENT))
                    .append("\n\n")
                    .append("Outgoing - Content Length:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_CONTENT_LENGTH))
                    .append("\n\n")
                    .append("Outgoing - Data Open Stream:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_DATA_OPEN_STREAM))
                    .append("\n\n")
                    .append("Outgoing - Data Stream Sent:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_DATA_STREAM_SENT))
                    .append("\n\n")
                    .append("Outgoing - Data Stream Closed:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_DATA_CLOSE_STREAM))
                    .append("\n\n")
                    .append("Outgoing - Variables Sent:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_VARIABLES_SENT))
                    .append("\n\n")
                    .append("Incoming - Begin Receiving:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_BEGIN))
                    .append("\n\n")
                    .append("Incoming - Code:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_CODE))
                    .append("\n\n")
                    .append("Incoming - Description:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_DESCRIPTION))
                    .append("\n\n")
                    .append("Incoming - Begin Reading Contents:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_BEGIN_READING_CONTENTS))
                    .append("\n\n")
                    .append("Incoming - Finished Reading Contents:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_END_READING_CONTENTS))
                    .append("\n\n")
                    .append("Incoming - Contents Length:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_CONTENTS_LENGTH))
                    .append("\n\n")
                    .append("Incoming - Header Count:\n")
                    .append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_HEADER_COUNT))
                    .append("\n\n")
                        ).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
