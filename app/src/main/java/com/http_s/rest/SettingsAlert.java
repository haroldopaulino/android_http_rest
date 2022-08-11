package com.http_s.rest;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

abstract class SettingsAlert {
    private final Activity activity;
    private final SqliteCore sqliteCore;
    private final View view;
    private final Utilities utilities;
    private final AlertDialog.Builder alert;
    private AlertDialog OptionDialog;
    private Spinner languageSpinner;
    private CheckBox
            locationCheckBox,
            base64GetCheckBox,
            encodeGetValuesCheckBox,
            encodePostValuesCheckBox;

    private static class LocalAlertAbout extends AlertAbout {
        LocalAlertAbout(Activity activityInput, AlertDialog.Builder alertInput, View viewInput) {
            super(activityInput, alertInput, viewInput);
        }

    }

    private static class LocalAlertPrivacyPolicy extends AlertPrivacyPolicy {
        LocalAlertPrivacyPolicy(Activity activityInput, AlertDialog.Builder alertInput, View viewInput) {
            super(activityInput, alertInput, viewInput);
        }

    }

    private class ClickHandler implements View.OnClickListener {

        String action;

        ClickHandler(String actionInput) {
            action = actionInput;
        }

        @Override
        public void onClick(View view) {
            switch(action) {
                case "ABOUT" :
                    openAboutDialog();
                    break;
                case "PRIVACY_POLICY" :
                    openPrivacyPolicyDialog();
                    break;
            }
        }
    }

    private class ChangeHandler implements OnCheckedChangeListener {

        String action;

        ChangeHandler(String actionInput) {
            action = actionInput;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            String newValue = b ? "YES" : "NO";
            sqliteCore.createSetting(action, newValue);
        }
    }

    SettingsAlert(
            Activity activityInput,
            AlertDialog.Builder alertInput,
            View viewInput) {
        activity = activityInput;
        alert = alertInput;
        view = viewInput;
        utilities = new Utilities(activity);
        sqliteCore = new SqliteCore(activity);
        buildLayout();
    }

    private void buildLayout() {
        try {
            view.findViewById(R.id.backImageContainer).setOnClickListener(arg0 -> dismiss());

            view.findViewById(R.id.backImage).setOnClickListener(arg0 -> dismiss());

            languageSpinner = view.findViewById(R.id.languageSpinner);

            locationCheckBox = view.findViewById(R.id.locationCheckBox);
            base64GetCheckBox = view.findViewById(R.id.base64GetCheckBox);
            encodeGetValuesCheckBox = view.findViewById(R.id.encodeGetValuesCheckBox);
            encodePostValuesCheckBox = view.findViewById(R.id.encodePostValuesCheckBox);

            locationCheckBox.setOnCheckedChangeListener(new ChangeHandler(Utilities.LOCATION_SETTING));
            base64GetCheckBox.setOnCheckedChangeListener(new ChangeHandler(Utilities.ENCODE_GET_QUERY_STRING));
            encodeGetValuesCheckBox.setOnCheckedChangeListener(new ChangeHandler(Utilities.ENCODE_GET_VALUES_SETTING));
            encodePostValuesCheckBox.setOnCheckedChangeListener(new ChangeHandler(Utilities.ENCODE_POST_VALUES_SETTING));

            Button privacyPolicyButton = view.findViewById(R.id.privacyPolicyButton);
            privacyPolicyButton.setOnClickListener(new ClickHandler("PRIVACY_POLICY"));

            Button aboutButton = view.findViewById(R.id.aboutButton);
            aboutButton.setOnClickListener(new ClickHandler("ABOUT"));

            loadObjectsValues();

            alert.setView(view);
            OptionDialog = alert.create();
            OptionDialog.setOnDismissListener(dialogInterface -> {
                try {
                    callback(new JSONObject().put("result" , "dismissed"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
            //OptionDialog.setCancelable(false);//ALLOW THE DEVICE'S BACK BUTTON TO CLOSE THE ALERT OR NOT

            OptionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ffffff")));
            OptionDialog.show();

            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            int displayWidth = displayMetrics.widthPixels;
            int displayHeight = displayMetrics.heightPixels;
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(OptionDialog.getWindow().getAttributes());

            int dialogWindowWidth = (int) (displayWidth * 0.9f);
            int dialogWindowHeight = (int) (displayHeight * 0.9f);

            layoutParams.width = dialogWindowWidth;
            layoutParams.height = dialogWindowHeight;

            OptionDialog.getWindow().setAttributes(layoutParams);

            loadLanguageSpinner();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLanguageSpinner() {
        ArrayList<SpinnerItemData> contents = new ArrayList<>();
        contents.add(new SpinnerItemData("English", 1));
        contents.add(new SpinnerItemData("Español", 1));
        contents.add(new SpinnerItemData("Français", 1));
        contents.add(new SpinnerItemData("Português", 1));
        final SpinnerAdapter spinnerAdapter = new SpinnerAdapter(activity, R.layout.spinner_list_item, R.id.spinnerTextview, contents);
        int selectedPosition = 0;
        try {
            selectedPosition = Integer.parseInt(sqliteCore.getSetting("language"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        languageSpinner.setAdapter(spinnerAdapter);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sqliteCore.createSetting("language", i + "");
                Log.e("languageSpinner", languageSpinner.getSelectedItem().toString());
                spinnerAdapter.notifyDataSetChanged();
                activity.setTitle(utilities.getLanguageText("SETTINGS_FRAGMENT_TITLE"));
                //loadTitles();
                try {
                    callback((new JSONObject()).put("RETURN", "CHANGED_LANGUAGE"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        languageSpinner.setSelection(selectedPosition);
    }

    private void loadObjectsValues() {
        locationCheckBox.setChecked(sqliteCore.getSetting(Utilities.LOCATION_SETTING).equalsIgnoreCase("YES"));
        base64GetCheckBox.setChecked(sqliteCore.getSetting(Utilities.ENCODE_GET_QUERY_STRING).equalsIgnoreCase("YES"));
        encodeGetValuesCheckBox.setChecked(sqliteCore.getSetting(Utilities.ENCODE_GET_VALUES_SETTING).equalsIgnoreCase("YES"));
        encodePostValuesCheckBox.setChecked(sqliteCore.getSetting(Utilities.ENCODE_POST_VALUES_SETTING).equalsIgnoreCase("YES"));

    }

    private void openPrivacyPolicyDialog() {
        AlertDialog.Builder alertPrivacyPolicy = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        LocalAlertPrivacyPolicy localPrivacyPolicyAlert = new LocalAlertPrivacyPolicy(activity, alertPrivacyPolicy, inflater.inflate(R.layout.alert_privacy_policy, null));
        localPrivacyPolicyAlert.getAlert();
    }

    private void openAboutDialog() {
        AlertDialog.Builder alertAbout = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        LocalAlertAbout localAboutAlert = new LocalAlertAbout(activity, alertAbout, inflater.inflate(R.layout.alert_about, null));
        localAboutAlert.getAlert();
    }

    AlertDialog.Builder getAlert() {
        return alert;
    }

    public void callback(JSONObject inputResponse) { }

    private void dismiss() {
        OptionDialog.dismiss();
    }
}
