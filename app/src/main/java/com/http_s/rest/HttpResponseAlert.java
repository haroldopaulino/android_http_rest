package com.http_s.rest;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


abstract class HttpResponseAlert {
    private final Activity activity;
    private final SqliteCore sqliteCore;
    private final View view;
    private final Utilities utilities;
    private final AlertDialog.Builder alert;
    private AlertDialog OptionDialog;
    private TextView contentsTextView;
    private final JSONObject responseData;
    private Button
            bodyButton,
            headersButton,
            summaryButton;

    class ClickHandler implements View.OnClickListener {
        String action;

        ClickHandler(String actionInput) {
            action = actionInput;
        }

        @Override
        public void onClick(View view) {
            switch(action) {
                case "BODY" :
                    loadBody();
                    break;
                case "HEADERS":
                    loadHeaders();
                    break;
                case "SUMMARY" :
                    loadSummary();
                    break;
                default :
                    loadBody();
                    break;
            }
            selectButton(action);
        }
    }

    private void selectButton(String selection) {
        bodyButton.setBackgroundColor(activity.getResources().getColor(R.color.inactiveTabButton));
        headersButton.setBackgroundColor(activity.getResources().getColor(R.color.inactiveTabButton));
        summaryButton.setBackgroundColor(activity.getResources().getColor(R.color.inactiveTabButton));
        switch(selection) {
            case "BODY" :
                bodyButton.setBackgroundColor(activity.getResources().getColor(R.color.activeTabButton));
                break;
            case "HEADERS":
                headersButton.setBackgroundColor(activity.getResources().getColor(R.color.activeTabButton));
                break;
            case "SUMMARY" :
                summaryButton.setBackgroundColor(activity.getResources().getColor(R.color.activeTabButton));
                break;
            default :
                bodyButton.setBackgroundColor(activity.getResources().getColor(R.color.activeTabButton));
                break;
        }
    }

    HttpResponseAlert(
            Activity activityInput,
            AlertDialog.Builder alertInput,
            View viewInput,
            JSONObject responseDataInput) {
        activity = activityInput;
        sqliteCore = new SqliteCore(activityInput);
        alert = alertInput;
        view = viewInput;
        responseData = responseDataInput;
        utilities = new Utilities(activity);
        buildLayout();
    }

    private void buildLayout() {
        try {
            TextView alertTitle = view.findViewById(R.id.alertTitle);
            alertTitle.setText(responseData.getString("response_code_description") + " (" + responseData.getString("response_code") + ")");

            view.findViewById(R.id.backImageContainer).setOnClickListener(arg0 -> OptionDialog.dismiss());

            view.findViewById(R.id.backImage).setOnClickListener(arg0 -> OptionDialog.dismiss());

            bodyButton = view.findViewById(R.id.bodyButton);
            headersButton = view.findViewById(R.id.headersButton);
            summaryButton = view.findViewById(R.id.summaryButton);

            bodyButton.setOnClickListener(new ClickHandler("BODY"));
            headersButton.setOnClickListener(new ClickHandler("HEADERS"));
            summaryButton.setOnClickListener(new ClickHandler("SUMMARY"));

            contentsTextView = view.findViewById(R.id.contentsTextView);
            selectButton("BODY");
            loadBody();

            alert.setView(view);
            OptionDialog = alert.create();
            OptionDialog.setOnDismissListener(dialogInterface -> {

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

            int dialogWindowWidth = (int) (displayWidth * 0.8f);
            int dialogWindowHeight = (int) (displayHeight * 0.8f);

            layoutParams.width = dialogWindowWidth;
            layoutParams.height = dialogWindowHeight;

            OptionDialog.getWindow().setAttributes(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AlertDialog.Builder getAlert() {
        return alert;
    }

    private void loadBody() {
        JSONObject parsedContents;
        String contents = "";
        try {
            contents = responseData.getString("response_message");
            parsedContents = new JSONObject(responseData.getString("response_message"));
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            contentsTextView.setText(gson.toJson(parsedContents));
        } catch (Exception e) {
            contentsTextView.setText(contents.replaceAll("\\\\", ""));
            e.printStackTrace();
        }

    }

    private void loadHeaders() {
        JSONObject headers = null;
        try {
            headers = responseData.getJSONObject("headers");
            StringBuilder contents = new StringBuilder();
            Iterator<String> iterator = headers.keys();
            int counter = 0;
            while (iterator.hasNext()) {
                String key = iterator.next();
                try {
                    String value = headers.getString(key);
                    counter++;
                    contents.append("HEADER #").append(counter).append("\n").append(key).append("  =  ").append(value).append("\n\n");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            contentsTextView.setText(contents.toString());
        } catch (Exception e) {
            if (headers != null) {
                contentsTextView.setText(headers.toString().replaceAll("\\\\", ""));
            }
            e.printStackTrace();
        }
    }

    private void loadSummary() {
        try {
            contentsTextView.setText((new StringBuilder().append("URL:\n").append(sqliteCore.getSetting("HTTP_URL")).append("\n\n")
                    .append("Connection Opened:\n").append(sqliteCore.getSetting(Utilities.HTTP_CONNECTION_OPENED)).append("\n\n")
                    .append("Outgoing - Header Sent:\n").append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_HEADER_SENT)).append("\n\n")
                    .append("Outgoing - Header Count Sent:\n").append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_HEADER_COUNT_SENT)).append("\n\n")
                    .append("Outgoing - Content Length:\n").append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_CONTENT_LENGTH)).append("\n\n")
                    .append("Outgoing - Data Open Stream:\n").append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_DATA_OPEN_STREAM)).append("\n\n")
                    .append("Outgoing - Data Stream Sent:\n").append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_DATA_STREAM_SENT)).append("\n\n")
                    .append("Outgoing - Data Stream Closed:\n").append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_DATA_CLOSE_STREAM)).append("\n\n")
                    .append("Outgoing - Variables Sent:\n").append(sqliteCore.getSetting(Utilities.HTTP_OUTGOING_VARIABLES_SENT)).append("\n\n")
                    .append("Incoming - Begin Receiving:\n").append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_BEGIN)).append("\n\n")
                    .append("Incoming - Code:\n").append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_CODE)).append("\n\n")
                    .append("Incoming - Description:\n").append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_DESCRIPTION)).append("\n\n")
                    .append("Incoming - Begin Reading Contents:\n").append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_BEGIN_READING_CONTENTS)).append("\n\n")
                    .append("Incoming - Finished Reading Contents:\n").append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_END_READING_CONTENTS)).append("\n\n")
                    .append("Incoming - Contents Length:\n").append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_CONTENTS_LENGTH)).append("\n\n")
                    .append("Incoming - Header Count:\n").append(sqliteCore.getSetting(Utilities.HTTP_INCOMING_HEADER_COUNT)).append("\n\n")
            ).toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
