package com.http_s.rest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {

    private Activity activity;
    private Spinner methodSpinner;
    private EditText urlEditText;
    private CheckBox
            headersCheckBox,
            variablesCheckBox;
    private Button
            addHeaderButton,
            addVariableButton,
            sendButton;
    private LinearLayout
            headersLinearLayout,
            variablesLinearLayout;
    private TextView deviceLocationTextView;
    private Utilities utilities;
    private SqliteCore sqliteCore;
    private LocalHttpClientAsyncTask httpClient;
    private LocalProcessingAlert localProcessingAlert;

    @Override
    protected void onResume() {
        super.onResume();
    }

    class LocalHttpClientAsyncTask extends HttpClient {
        LocalHttpClientAsyncTask(
                Activity inputActivity,
                String requestMethodInput,
                String urlInput,
                JSONArray headerInput,
                JSONArray variableInput) {
            super(inputActivity,
                  requestMethodInput,
                  urlInput,
                  headerInput,
                  variableInput);
        }

        public void callback(JSONObject responseData) {
            closeProcessingDialog();
            openHttpResponseDialog(responseData);
        }
    }

    class LocalProcessingAlert extends ProcessingAlert {
        LocalProcessingAlert(
                Activity activityInput,
                AlertDialog.Builder alertInput,
                View viewInput) {
            super(activityInput, alertInput, viewInput);
        }

        @Override
        public void callback(JSONObject inputResponse) {
            try {
                if (inputResponse.getString("response").equalsIgnoreCase("CANCEL_REQUEST")) {
                    httpClient.cancel();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class LocalHttpResponseAlert extends HttpResponseAlert {
        LocalHttpResponseAlert(
                Activity activityInput,
                AlertDialog.Builder alertInput,
                View viewInput,
                JSONObject responseDataInput) {
            super(activityInput, alertInput, viewInput, responseDataInput);
        }

    }

    class LocalSettingsAlert extends SettingsAlert {
        LocalSettingsAlert(
                Activity activityInput,
                AlertDialog.Builder alertInput,
                View viewInput) {
            super(activityInput, alertInput, viewInput);
        }

        @Override
        public void callback(JSONObject inputResponse) {
            loadGpsLocation();
        }
    }

    private static class LocalErrorAlert extends AlertMessage {
        LocalErrorAlert(
                Activity activityInput,
                AlertDialog.Builder alertInput,
                View viewInput,
                String titleInput,
                String messageInput) {
            super(activityInput, alertInput, viewInput, titleInput, messageInput);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activity = this;
        utilities = new Utilities(this);
        sqliteCore = new SqliteCore(this);

        methodSpinner = findViewById(R.id.methodSpinner);
        methodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sqliteCore.createSetting("METHOD_CHOSEN_ITEM", i + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if (!sqliteCore.getSetting("METHOD_CHOSEN_ITEM").trim().equalsIgnoreCase("")) {
            try {
                methodSpinner.setSelection(Integer.parseInt(sqliteCore.getSetting("METHOD_CHOSEN_ITEM")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        urlEditText = findViewById(R.id.urlEditText);
        String url = sqliteCore.getSetting("URL_TEXT");
        urlEditText.setText(!url.equals("") ? url : "https://haroldopaulino.com/web/http_rest/gateway.php");
        urlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sqliteCore.createSetting("URL_TEXT", charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        headersCheckBox = findViewById(R.id.headersCheckBox);
        variablesCheckBox = findViewById(R.id.variablesCheckBox);

        sendButton = findViewById(R.id.sendButton);
        addHeaderButton = findViewById(R.id.addHeaderButton);
        addVariableButton = findViewById(R.id.addVariableButton);

        headersLinearLayout = findViewById(R.id.headersLinearLayout);
        variablesLinearLayout = findViewById(R.id.variablesLinearLayout);

        deviceLocationTextView = findViewById(R.id.deviceLocationTextView);

        loadCheckBoxes();
        setCheckBoxes();
        setButtons();
        loadHeadersAndVariables();
        loadGpsLocation();
    }

    private void loadGpsLocation() {

        if (sqliteCore.getSetting(Utilities.LOCATION_SETTING).equalsIgnoreCase("YES")) {
            if (utilities.requestGpsPermission()) {
                updateGpsView();
            }
        } else {
            deviceLocationTextView.setVisibility(View.GONE);
        }
    }

    private void updateGpsView() {
        deviceLocationTextView.setVisibility(View.VISIBLE);
        deviceLocationTextView.setText("Fetching Location");
        Location currentLocation = utilities.getGpsLocation();
        deviceLocationTextView.setText(
                "My Latitude: " + currentLocation.getLatitude() + "\n" +
                        "My Longitude: " + currentLocation.getLongitude());
        sqliteCore.createSetting("LATITUDE", currentLocation.getLatitude() + "");
        sqliteCore.createSetting("LONGITUDE", currentLocation.getLongitude() + "");
    }

    private void hideGpsView() {
        deviceLocationTextView.setVisibility(View.GONE);
    }

    private void loadCheckBoxes() {
        boolean
                headersState,
                variablesState;
        headersState = sqliteCore.getSetting("CHECKED_HEADERS_CHECKBOX").equals("YES");
        headersCheckBox.setChecked(headersState);
        manageHeadersVariblesVisibility("HEADERS", headersState);

        variablesState = sqliteCore.getSetting("CHECKED_VARIABLES_CHECKBOX").equals("YES");
        variablesCheckBox.setChecked(variablesState);
        manageHeadersVariblesVisibility("VARIABLES", variablesState);
    }

    private void setCheckBoxes() {
        headersCheckBox.setOnCheckedChangeListener((compoundButton, checked) -> {
            sqliteCore.createSetting("CHECKED_HEADERS_CHECKBOX", checked ? "YES" : "NO");
            manageHeadersVariblesVisibility("HEADERS", checked);
        });

        variablesCheckBox.setOnCheckedChangeListener((compoundButton, checked) -> {
            sqliteCore.createSetting("CHECKED_VARIABLES_CHECKBOX", checked ? "YES" : "NO");
            manageHeadersVariblesVisibility("VARIABLES", checked);
        });
    }

    private void setButtons() {
        findViewById(R.id.menuImageView).setOnClickListener(view -> openSettingsDialog());

        addHeaderButton.setOnClickListener(view -> addNameValueField(headersLinearLayout, "header", "", ""));

        addVariableButton.setOnClickListener(view -> addNameValueField(variablesLinearLayout, "variable", "", ""));

        sendButton.setOnClickListener(view -> {
            sendHttpRequest();
            openProcessingDialog();
        });
    }

    private void manageHeadersVariblesVisibility(String target, boolean state) {
        switch(target ) {
            case "HEADERS" :
                if (state) {
                    headersLinearLayout.setVisibility(View.VISIBLE);
                    addHeaderButton.setVisibility(View.VISIBLE);
                } else {
                    headersLinearLayout.setVisibility(View.GONE);
                    addHeaderButton.setVisibility(View.GONE);
                }
                break;
            case "VARIABLES" :
                if (state) {
                    variablesLinearLayout.setVisibility(View.VISIBLE);
                    addVariableButton.setVisibility(View.VISIBLE);
                } else {
                    variablesLinearLayout.setVisibility(View.GONE);
                    addVariableButton.setVisibility(View.GONE);
                }
                break;
        }
    }

    public void addNameValueField(
            final LinearLayout inputLayout,
            final String fieldType,
            final String fieldName,
            final String fieldValue) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.name_value_item, null);
        ((EditText) rowView.findViewById(R.id.nameEditText)).setText(fieldName);
        ((EditText) rowView.findViewById(R.id.valueEditText)).setText(fieldValue);

        final EditText nameEditText = rowView.findViewById(R.id.nameEditText);
        final EditText valueEditText = rowView.findViewById(R.id.valueEditText);

        ((EditText) rowView.findViewById(R.id.nameEditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sqliteCore.deleteHeaderVariable(fieldType, nameEditText.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sqliteCore.createHeaderVariable(fieldType, nameEditText.getText().toString(), valueEditText.getText().toString());
            }
        });

        ((EditText) rowView.findViewById(R.id.valueEditText)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sqliteCore.deleteHeaderVariable(fieldType, nameEditText.getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                sqliteCore.createHeaderVariable(fieldType, nameEditText.getText().toString(), valueEditText.getText().toString());
            }
        });

        rowView.findViewById(R.id.deleteImageView).setOnClickListener(view -> {
            inputLayout.removeView(rowView);
            sqliteCore.deleteHeaderVariable(fieldType, nameEditText.getText().toString());
        });

        rowView.findViewById(R.id.deleteFrameLayout).setOnClickListener(view -> {
            inputLayout.removeView(rowView);
            sqliteCore.deleteHeaderVariable(fieldType, nameEditText.getText().toString());
        });

        inputLayout.addView(rowView, inputLayout.getChildCount());
    }

    private void loadHeadersAndVariables() {
        loadStoredFields("header", headersLinearLayout);
        loadStoredFields("variable", variablesLinearLayout);
    }

    private void loadStoredFields(String fieldType, LinearLayout inputLayout) {
        try {
            JSONArray storedRecords = sqliteCore.getHeaderVariable(fieldType, null);
            int storedRecordsCount = storedRecords.length();
            JSONObject record;
            for (int i = 0; i < storedRecordsCount; i++) {
                record = storedRecords.getJSONObject(i);
                addNameValueField(inputLayout, fieldType, record.getString("name").trim(), record.getString("value").trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*for (int x = 0; x < headersLinearLayout.getChildCount(); x++)
        {
            View viewChild1 = headersLinearLayout.getChildAt(x);
            Class classChild1 = viewChild1.getClass();
            if (classChild1 == LinearLayout.class)
            {
                LinearLayout layoutChild1 = (LinearLayout) viewChild1;
                for (int y = 0; y < layoutChild1.getChildCount(); y++)
                {
                    //continue
                }
            }
        }*/
    }

    private void sendHttpRequest() {
        JSONObject outputData = new JSONObject();
        httpClient = new LocalHttpClientAsyncTask(
                this,
                getRequestMethodValue(),
                urlEditText.getText().toString(),
                sqliteCore.getHeaderVariable("header", null),
                sqliteCore.getHeaderVariable("variable", null));
        try {
            outputData.put("web_service_mode", Base64.encodeToString("TRUE".getBytes(StandardCharsets.UTF_8), Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }
        httpClient.runHttpClient();
        //openProcessingDialog();
    }

    private String getRequestMethodValue() {
        String output = "GET";
        switch(methodSpinner.getSelectedItemPosition()) {
            case 0 :
                output = "GET";
                break;
            case 1 :
                output = "POST";
                break;
            case 2 :
                output = "OPTIONS";
                break;
            case 3 :
                output = "HEAD";
                break;
            case 4 :
                output = "PUT";
                break;
            case 5 :
                output = "DELETE";
                break;
            case 6 :
                output = "TRACE";
                break;
        }
        return output;
    }

    private void openSettingsDialog() {
        AlertDialog.Builder alertSettings = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        LocalSettingsAlert localSettingsAlert = new LocalSettingsAlert(
                this,
                alertSettings,
                inflater.inflate(R.layout.settings_alert, null));
        localSettingsAlert.getAlert();
    }

    private void openHttpResponseDialog(final JSONObject responseData) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertHttpResponse = new AlertDialog.Builder(activity);
                LayoutInflater inflater = getLayoutInflater();
                LocalHttpResponseAlert localAboutAlert = new LocalHttpResponseAlert(
                        activity,
                        alertHttpResponse,
                        inflater.inflate(R.layout.http_response, null),
                        responseData);
                localAboutAlert.getAlert();
            }
        });
    }

    private void openProcessingDialog() {
        AlertDialog.Builder alertProcessing = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        localProcessingAlert = new LocalProcessingAlert(
                this,
                alertProcessing,
                inflater.inflate(R.layout.processing_alert, null));
        localProcessingAlert.getAlert();
    }

    private void closeProcessingDialog() {
        try {
            localProcessingAlert.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openErrorDialog(final String title, final String message) {
        activity.runOnUiThread(() -> {
            AlertDialog.Builder alertError = new AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            LocalErrorAlert localErrorAlert = new LocalErrorAlert(activity, alertError, inflater.inflate(R.layout.alert_message, null), title, message);
            localErrorAlert.getAlert();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateGpsView();
            } else {
                hideGpsView();
                openErrorDialog("ERROR", "The app was not allowed to access your location.");
            }
        }
    }
}
