package com.http_s.rest;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


abstract class ProcessingAlert {
    private final Activity activity;
    private final View view;
    private final AlertDialog.Builder alert;
    private AlertDialog OptionDialog;
    private long startTime;
    private final Handler handler;
    private TextView alertTimer;

    private final Runnable runnable = new Runnable() {

        public void run() {
            long millisecondTime = SystemClock.uptimeMillis() - startTime;
            int seconds = (int) (millisecondTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliSeconds = (int) (millisecondTime % 1000);
            if (minutes == 0) {
                alertTimer.setText(seconds + "s, " +
                        String.format("%03d", milliSeconds) + "ms");
            } else {
                alertTimer.setText(minutes + "m, " +
                        seconds + "s, " +
                        String.format("%03d", milliSeconds) + "ms");
            }

            handler.postDelayed(this, 0);
        }

    };

    ProcessingAlert(
            Activity activityInput,
            AlertDialog.Builder alertInput,
            View viewInput) {
        activity = activityInput;
        alert = alertInput;
        view = viewInput;
        handler = new Handler();
        buildLayout();
    }

    private void buildLayout() {
        try {
            alertTimer = view.findViewById(R.id.alertTimer);
            view.findViewById(R.id.cancelRequestButton).setOnClickListener(arg0 -> {
                try {
                    callback(new JSONObject().put("response", "CANCEL_REQUEST"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                OptionDialog.dismiss();
            });

            WebView loadingWebView =  view.findViewById(R.id.loadingWebView);
            loadingWebView.getSettings();
            loadingWebView.setBackgroundColor(Color.TRANSPARENT);
            loadingWebView.loadUrl("file:///android_asset/loading.html");

            alert.setView(view);
            OptionDialog = alert.create();
            OptionDialog.setOnDismissListener(dialogInterface -> {
                //TO DO
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

            startStopWatch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AlertDialog.Builder getAlert() {
        return alert;
    }

    public void callback(JSONObject inputResponse) { }

    public void dismiss() {
        OptionDialog.dismiss();
    }

    private void startStopWatch() {
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
    }
}
