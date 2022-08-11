package com.http_s.rest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.json.JSONObject;

abstract class AlertMessage {
    private final Activity activity;
    private final SqliteCore sqliteCore;
    private final View view;
    private final AlertDialog.Builder alert;
    private AlertDialog OptionDialog;
    private final String title;
    private final String message;

    AlertMessage(
            Activity activityInput,
            AlertDialog.Builder alertInput,
            View viewInput,
            String titleInput,
            String messageInput) {
        activity = activityInput;
        alert = alertInput;
        view = viewInput;
        title = titleInput;
        message = messageInput;
        sqliteCore = new SqliteCore(activity);
        buildLayout();
    }

    private void buildLayout() {
        TextView alertTitle = view.findViewById(R.id.alertTitle);
        alertTitle.setText(title);

        view.findViewById(R.id.backImageContainer).setOnClickListener(arg0 -> OptionDialog.dismiss());

        view.findViewById(R.id.backImage).setOnClickListener(arg0 -> OptionDialog.dismiss());

        view.findViewById(R.id.okButton).setOnClickListener(arg0 -> OptionDialog.dismiss());

        TextView messageTextView = view.findViewById(R.id.message);
        messageTextView.setText(message);

        alert.setView(view);
        OptionDialog = alert.create();
        OptionDialog.setOnDismissListener(dialogInterface -> sqliteCore.createSetting("ERROR_DIALOG_CURRENTLY_SHOWING", "NO"));
        //OptionDialog.setCancelable(false);//ALLOW THE DEVICE'S BACK BUTTON TO CLOSE THE ALERT OR NOT

        OptionDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00ffffff")));
        OptionDialog.show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(OptionDialog.getWindow().getAttributes());

        OptionDialog.getWindow().setAttributes(layoutParams);
    }

    public AlertDialog.Builder getAlert() {
        return alert;
    }

}
