package com.http_s.rest

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.http_s.rest.alerts.AlertMessage
import com.http_s.rest.alerts.HttpResponseAlert
import com.http_s.rest.alerts.ProcessingAlert
import com.http_s.rest.alerts.SettingsAlert
import com.http_s.rest.api_client.HttpClient
import com.http_s.rest.data.SqliteCore
import com.http_s.rest.location.LocationHelper
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class MainActivity : AppCompatActivity() {

    private lateinit var activity: Activity
    private lateinit var methodSpinner: Spinner
    private lateinit var urlEditText: EditText
    private lateinit var headersCheckBox: CheckBox
    private lateinit var variablesCheckBox: CheckBox
    private lateinit var addHeaderButton: Button
    private lateinit var addVariableButton: Button
    private lateinit var sendButton: Button
    private lateinit var headersLinearLayout: LinearLayout
    private lateinit var variablesLinearLayout: LinearLayout
    private var utilities: Utilities? = null
    private lateinit var sqliteCore: SqliteCore
    private var httpClient: LocalHttpClientAsyncTask? = null
    private var localProcessingAlert: LocalProcessingAlert? = null
    private lateinit var locationHelper: LocationHelper
    private lateinit var deviceLocationTextView: TextView

    companion object {
        private const val TAG = "MainActivity"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    class LocalHttpClientAsyncTask internal constructor(
        private val mainActivity: MainActivity,
        requestMethodInput: String?,
        urlInput: String?,
        headerInput: JSONArray,
        variableInput: JSONArray
    ) : HttpClient(
        mainActivity,
        requestMethodInput.toString(),
        urlInput.toString(),
        headerInput,
        variableInput
    ) {
        override fun callback(responseData: JSONObject?, message: String?) {
            Log.d("MainActivity", "HttpClient -> Callback called -> Response data: $responseData")
            mainActivity.closeProcessingDialog()
            if (responseData != null) {
                mainActivity.openHttpResponseDialog(responseData)
            } else {
                Log.d("MainActivity", "HttpClient -> Callback called -> responseData is null!")
            }

            if (message != null) {
                mainActivity.openErrorDialog(message)
            } else {
                Log.d("MainActivity", "HttpClient -> Callback called -> Message is null!")
            }
        }
    }

    private class LocalProcessingAlert(
        private val mainActivity: MainActivity,
        alertInput: AlertDialog.Builder,
        viewInput: View
    ) :
        ProcessingAlert(
            mainActivity,
            alertInput,
            viewInput
        ) {
        override fun callback(inputResponse: JSONObject) {
            try {
                if (inputResponse.getString("response")
                        .equals("CANCEL_REQUEST", ignoreCase = true)
                ) {
                    mainActivity.httpClient?.cancel()
                }
            } catch (e: Exception) {
                e.message?.let { Log.e("ERROR", it) }
            }
        }
    }

    class LocalHttpResponseAlert internal constructor(
        activityInput: Activity,
        alertInput: AlertDialog.Builder,
        viewInput: View,
        responseDataInput: JSONObject?
    ) :
        HttpResponseAlert(
            activityInput,
            alertInput,
            viewInput,
            responseDataInput
        )

    private class LocalSettingsAlert(
        private val mainActivity: MainActivity,
        alertInput: AlertDialog.Builder,
        viewInput: View
    ) :
        SettingsAlert(
            mainActivity,
            alertInput,
            viewInput) {
        override fun callback(inputResponse: JSONObject?) {
            mainActivity.loadGpsLocation()
        }
    }

    private class LocalErrorAlert(
        mainActivity: Activity?,
        alertInput: AlertDialog.Builder,
        viewInput: View,
        titleInput: String,
        messageInput: String
    ) :
        AlertMessage(
            mainActivity,
            alertInput,
            viewInput,
            titleInput,
            messageInput
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity = this
        utilities = Utilities(this)
        sqliteCore = SqliteCore(this)

        deviceLocationTextView = findViewById(R.id.deviceLocationTextView)
        locationHelper = LocationHelper(this)

        processInit()
    }

    private fun processInit() {
        // Check for permissions and get location
        loadGpsLocation()
        setViews()
        setCheckBoxes()
        setButtons()
        loadHeadersAndVariables()
        loadCheckBoxes()
    }

    fun loadGpsLocation() {
        if (locationHelper.hasLocationPermission()) {
            updateGpsView()
        } else {
            locationHelper.requestLocationPermissions(this)
        }
    }

    private fun updateGpsView() {
        deviceLocationTextView.text = getString(R.string.fetching_location)
        lifecycleScope.launch {
            val currentLocation = locationHelper.getGpsLocation()
            if (currentLocation == null) {
                deviceLocationTextView.text = getString(R.string.location_not_available)
                deviceLocationTextView.setOnClickListener { loadGpsLocation() }
            } else {
                deviceLocationTextView.text = String.format(
                    "%s%s\n%s%s",
                    getString(R.string.my_latitude),
                    currentLocation.latitude,
                    getString(R.string.my_longitude),
                    currentLocation.longitude
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Location permission granted")
                updateGpsView()
            } else {
                Log.e(TAG, "Location permission denied")
                deviceLocationTextView.text = getString(R.string.location_permission_denied)
                deviceLocationTextView.setOnClickListener { loadGpsLocation() }
            }
        }
    }

    private fun loadCheckBoxes() {
        val headersState: Boolean = sqliteCore.getSetting("CHECKED_HEADERS_CHECKBOX").equals("YES")
        headersCheckBox.isChecked = headersState
        manageHeadersVariblesVisibility("HEADERS", headersState)

        val variablesState: Boolean =
            sqliteCore.getSetting("CHECKED_VARIABLES_CHECKBOX").equals("YES")
        variablesCheckBox.isChecked = variablesState
        manageHeadersVariblesVisibility("VARIABLES", variablesState)
    }

    private fun setViews() {
        urlEditText = findViewById(R.id.urlEditText)
        methodSpinner = findViewById(R.id.methodSpinner)
    }

    private fun setCheckBoxes() {
        headersCheckBox = findViewById(R.id.headersCheckBox)
        headersCheckBox.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            sqliteCore.createSetting("CHECKED_HEADERS_CHECKBOX", if (checked) "YES" else "NO")
            manageHeadersVariblesVisibility("HEADERS", checked)
        }

        variablesCheckBox = findViewById(R.id.variablesCheckBox)
        variablesCheckBox.setOnCheckedChangeListener { _: CompoundButton?, checked: Boolean ->
            sqliteCore.createSetting("CHECKED_VARIABLES_CHECKBOX", if (checked) "YES" else "NO")
            manageHeadersVariblesVisibility("VARIABLES", checked)
        }
    }

    private fun setButtons() {
        findViewById<View>(R.id.menuImageView).setOnClickListener { openSettingsDialog() }

        addHeaderButton = findViewById(R.id.addHeaderButton )
        addHeaderButton.setOnClickListener {
            addNameValueField(
                headersLinearLayout,
                "header",
                "",
                ""
            )
        }

        addVariableButton = findViewById(R.id.addVariableButton)
        addVariableButton.setOnClickListener {
            addNameValueField(
                variablesLinearLayout,
                "variable",
                "",
                ""
            )
        }

        sendButton = findViewById(R.id.sendButton)
        sendButton.setOnClickListener {
            openProcessingDialog()
            sendHttpRequest()
        }
    }

    private fun manageHeadersVariblesVisibility(target: String, state: Boolean) {
        when (target) {
            "HEADERS" -> if (state) {
                headersLinearLayout.visibility = View.VISIBLE
                addHeaderButton.visibility = View.VISIBLE
            } else {
                headersLinearLayout.visibility = View.GONE
                addHeaderButton.visibility = View.GONE
            }

            "VARIABLES" -> if (state) {
                variablesLinearLayout.visibility = View.VISIBLE
                addVariableButton.visibility = View.VISIBLE
            } else {
                variablesLinearLayout.visibility = View.GONE
                addVariableButton.visibility = View.GONE
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun addNameValueField(
        inputLayout: LinearLayout,
        fieldType: String,
        fieldName: String,
        fieldValue: String
    ) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = inflater.inflate(R.layout.name_value_item, null)
        (rowView.findViewById<View>(R.id.nameEditText) as EditText).setText(fieldName)
        (rowView.findViewById<View>(R.id.valueEditText) as EditText).setText(fieldValue)

        val nameEditText = rowView.findViewById<EditText>(R.id.nameEditText)
        val valueEditText = rowView.findViewById<EditText>(R.id.valueEditText)

        (rowView.findViewById<View>(R.id.nameEditText) as EditText).addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                sqliteCore.deleteHeaderVariable(fieldType, nameEditText.text.toString())
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                sqliteCore.createHeaderVariable(
                    fieldType,
                    nameEditText.text.toString(),
                    valueEditText.text.toString()
                )
            }
        })

        (rowView.findViewById<View>(R.id.valueEditText) as EditText).addTextChangedListener(object :
            TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                sqliteCore.deleteHeaderVariable(fieldType, nameEditText.text.toString())
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                sqliteCore.createHeaderVariable(
                    fieldType,
                    nameEditText.text.toString(),
                    valueEditText.text.toString()
                )
            }
        })

        rowView.findViewById<View>(R.id.deleteImageView).setOnClickListener { view: View? ->
            inputLayout.removeView(rowView)
            sqliteCore.deleteHeaderVariable(fieldType, nameEditText.text.toString())
        }

        rowView.findViewById<View>(R.id.deleteFrameLayout).setOnClickListener { view: View? ->
            inputLayout.removeView(rowView)
            sqliteCore.deleteHeaderVariable(fieldType, nameEditText.text.toString())
        }

        inputLayout.addView(rowView, inputLayout.childCount)
    }

    private fun loadHeadersAndVariables() {
        headersLinearLayout = findViewById(R.id.headersLinearLayout)
        variablesLinearLayout = findViewById(R.id.variablesLinearLayout)
        loadStoredFields("header", headersLinearLayout)
        loadStoredFields("variable", variablesLinearLayout)
    }

    private fun loadStoredFields(fieldType: String, inputLayout: LinearLayout) {
        try {
            val storedRecords: JSONArray = sqliteCore.getHeaderVariable(fieldType, null)
            val storedRecordsCount = storedRecords.length()
            var record: JSONObject
            for (i in 0 until storedRecordsCount) {
                record = storedRecords.getJSONObject(i)
                addNameValueField(
                    inputLayout,
                    fieldType,
                    record.getString("name").trim { it <= ' ' },
                    record.getString("value").trim { it <= ' ' })
            }
        } catch (e: java.lang.Exception) {
            e.message?.let { Log.e("ERROR", it) }
        }
    }

    private fun sendHttpRequest() {
        val outputData = JSONObject()
        httpClient = LocalHttpClientAsyncTask(
            this,
            getRequestMethodValue(),
            urlEditText.text.toString(),
            sqliteCore.getHeaderVariable("header", null),
            sqliteCore.getHeaderVariable("variable", null)
        )
        try {
            outputData.put(
                "web_service_mode",
                Base64.encodeToString("TRUE".toByteArray(StandardCharsets.UTF_8), Base64.DEFAULT)
            )
        } catch (e: java.lang.Exception) {
            e.message?.let {
                Log.d("MainActivity", "sendHttpRequest -> error -> $it")
            }
        }
        Log.d("MainActivity", "runHttpClient")
        httpClient?.runHttpClient()
    }

    private fun getRequestMethodValue(): String {
        var output = "GET"
        when (methodSpinner.selectedItemPosition) {
            0 -> output = "GET"
            1 -> output = "POST"
            2 -> output = "OPTIONS"
            3 -> output = "HEAD"
            4 -> output = "PUT"
            5 -> output = "DELETE"
            6 -> output = "TRACE"
        }
        return output
    }

    @SuppressLint("InflateParams")
    private fun openSettingsDialog() {
        val alertSettings = AlertDialog.Builder(this)
        val inflater = layoutInflater
        LocalSettingsAlert(
            this,
            alertSettings,
            inflater.inflate(R.layout.alert_settings, null)
        )
    }

    @SuppressLint("InflateParams")
    private fun openHttpResponseDialog(responseData: JSONObject) {
        runOnUiThread {
            val alertHttpResponse = AlertDialog.Builder(activity)
            val inflater = layoutInflater
            activity.let {
                LocalHttpResponseAlert(
                    it,
                    alertHttpResponse,
                    inflater.inflate(R.layout.http_response, null),
                    responseData
                )
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun openProcessingDialog() {
        val alertProcessing = AlertDialog.Builder(this)
        val inflater = layoutInflater
        localProcessingAlert = LocalProcessingAlert(
            this,
            alertProcessing,
            inflater.inflate(R.layout.processing_alert, null)
        )
        localProcessingAlert?.getAlert()
    }

    fun closeProcessingDialog() {
        try {
            localProcessingAlert?.dismiss()
        } catch (e: java.lang.Exception) {
            e.message?.let { Log.e("ERROR", it) }
        }
    }

    @SuppressLint("InflateParams")
    fun openErrorDialog(message: String) {
        activity.runOnUiThread {
            val alertError = AlertDialog.Builder(activity)
            val inflater: LayoutInflater = activity.layoutInflater
            LocalErrorAlert(
                activity,
                alertError,
                inflater.inflate(R.layout.alert_message, null),
                getString(R.string.response),
                message
            )
        }
    }
}