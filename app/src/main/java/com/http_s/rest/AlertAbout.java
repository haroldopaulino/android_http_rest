package com.http_s.rest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONObject;

abstract class AlertAbout {
    private final Activity activity;
    private final View view;
    private final Utilities utilities;
    private final AlertDialog.Builder alert;
    private AlertDialog OptionDialog;
    private TextView alertTitle;

    AlertAbout(
            Activity activityInput,
            AlertDialog.Builder alertInput,
            View viewInput) {
        activity = activityInput;
        alert = alertInput;
        view = viewInput;
        utilities = new Utilities(activity);
        buildLayout();
    }

    private void buildLayout() {
        alertTitle = view.findViewById(R.id.alertTitle);

        view.findViewById(R.id.backImageContainer).setOnClickListener(arg0 -> OptionDialog.dismiss());

        view.findViewById(R.id.backImage).setOnClickListener(arg0 -> OptionDialog.dismiss());

        WebView webView = view.findViewById(R.id.webView);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setBackgroundColor(0x00000000);
        String fileContents = utilities.getFileContentFromAssets(utilities.getLanguageText("PORTFOLIO_FILENAME"));
        webView.loadDataWithBaseURL("file:///android_asset/", fileContents, "text/html", "utf-8", null);

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

        loadTitles();
    }

    public AlertDialog.Builder getAlert() {
        return alert;
    }

    private void loadTitles() {
        alertTitle.setText(utilities.getLanguageText("ABOUT"));
    }

}
