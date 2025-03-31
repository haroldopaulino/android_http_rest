package com.http_s.rest.alerts

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import com.http_s.rest.R
import com.http_s.rest.Utilities
import com.http_s.rest.data.SqliteCore
import org.json.JSONException
import org.json.JSONObject

internal abstract class SettingsAlert(
    private val activity: Activity,
    val alert: AlertDialog.Builder,
    private val view: View
) {
    private val sqliteCore = SqliteCore(activity)
    private lateinit var optionDialog: AlertDialog
    private lateinit var locationCheckBox: CheckBox
    private lateinit var base64GetCheckBox: CheckBox
    private lateinit var encodeGetValuesCheckBox: CheckBox
    private lateinit var encodePostValuesCheckBox: CheckBox

    private class LocalAlertAbout(
        activityInput: Activity,
        alertInput: AlertDialog.Builder,
        viewInput: View
    ) :
        AlertAbout(activityInput, alertInput, viewInput)

    private class LocalAlertPrivacyPolicy(
        activityInput: Activity,
        alertInput: AlertDialog.Builder,
        viewInput: View
    ) :
        AlertPrivacyPolicy(activityInput, alertInput, viewInput)

    private inner class ClickHandler(var action: String) : View.OnClickListener {
        override fun onClick(view: View) {
            when (action) {
                "ABOUT" -> openAboutDialog()
                "PRIVACY_POLICY" -> openPrivacyPolicyDialog()
            }
        }
    }

    private inner class ChangeHandler(var action: String) : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(compoundButton: CompoundButton, b: Boolean) {
            val newValue = if (b) "YES" else "NO"
            sqliteCore.createSetting(action, newValue)
        }
    }

    init {
        buildLayout()
    }

    private fun buildLayout() {
        try {
            view.findViewById<View>(R.id.backImageContainer)
                .setOnClickListener { dismiss() }

            view.findViewById<View>(R.id.backImage).setOnClickListener { dismiss() }

            locationCheckBox = view.findViewById(R.id.locationCheckBox)
            base64GetCheckBox = view.findViewById(R.id.base64GetCheckBox)
            encodeGetValuesCheckBox = view.findViewById(R.id.encodeGetValuesCheckBox)
            encodePostValuesCheckBox = view.findViewById(R.id.encodePostValuesCheckBox)

            locationCheckBox.setOnCheckedChangeListener(ChangeHandler(Utilities.LOCATION_SETTING))
            base64GetCheckBox.setOnCheckedChangeListener(ChangeHandler(Utilities.ENCODE_GET_QUERY_STRING))
            encodeGetValuesCheckBox.setOnCheckedChangeListener(ChangeHandler(Utilities.ENCODE_GET_VALUES_SETTING))
            encodePostValuesCheckBox.setOnCheckedChangeListener(ChangeHandler(Utilities.ENCODE_POST_VALUES_SETTING))

            val privacyPolicyButton = view.findViewById<Button>(R.id.privacyPolicyButton)
            privacyPolicyButton.setOnClickListener(ClickHandler("PRIVACY_POLICY"))

            val aboutButton = view.findViewById<Button>(R.id.aboutButton)
            aboutButton.setOnClickListener(ClickHandler("ABOUT"))

            loadObjectsValues()

            alert.setView(view)
            optionDialog = alert.create()
            optionDialog.setOnDismissListener {
                try {
                    callback(JSONObject().put("result", "dismissed"))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

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

            val dialogWindowWidth = (displayWidth * 0.9f).toInt()
            val dialogWindowHeight = (displayHeight * 0.9f).toInt()

            layoutParams.width = dialogWindowWidth
            layoutParams.height = dialogWindowHeight

            optionDialog.window!!.attributes = layoutParams
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadObjectsValues() {
        locationCheckBox.isChecked =
            sqliteCore.getSetting(Utilities.LOCATION_SETTING)
                .equals("YES", ignoreCase = true)
        base64GetCheckBox.isChecked =
            sqliteCore.getSetting(Utilities.ENCODE_GET_QUERY_STRING)
                .equals("YES", ignoreCase = true)
        encodeGetValuesCheckBox.isChecked =
            sqliteCore.getSetting(Utilities.ENCODE_GET_VALUES_SETTING)
                .equals("YES", ignoreCase = true)
        encodePostValuesCheckBox.isChecked =
            sqliteCore.getSetting(Utilities.ENCODE_POST_VALUES_SETTING)
                .equals("YES", ignoreCase = true)
    }

    private fun openPrivacyPolicyDialog() {
        val alertPrivacyPolicy = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val localPrivacyPolicyAlert = LocalAlertPrivacyPolicy(
            activity,
            alertPrivacyPolicy,
            inflater.inflate(R.layout.alert_privacy_policy, null)
        )
        localPrivacyPolicyAlert.alert
    }

    private fun openAboutDialog() {
        val alertAbout = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val localAboutAlert =
            LocalAlertAbout(activity, alertAbout, inflater.inflate(R.layout.alert_about, null))
        localAboutAlert.alert
    }

    open fun callback(inputResponse: JSONObject?) {}

    private fun dismiss() {
        optionDialog.dismiss()
    }
}
