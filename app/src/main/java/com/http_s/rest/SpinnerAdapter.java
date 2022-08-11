package com.http_s.rest;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<SpinnerItemData> {
    int groupid;
    Activity context;
    ArrayList<SpinnerItemData> list;
    LayoutInflater inflater;
    int selectedItem = 0;
    public SpinnerAdapter(Activity context, int groupid, int id, ArrayList<SpinnerItemData>
            list){
        super(context,id,list);
        this.context = context;
        this.list=list;
        inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid=groupid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent ) {
        if (convertView == null) {
            convertView = inflater.inflate(groupid, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.spinnerTextview);
        textView.setText(list.get(position).getText());

        return convertView;
    }

    public View getDropDownView(int position,View convertView,ViewGroup parent) {
        return getView(position,convertView,parent);
    }

    public void setSelectedItem(int inputSelectedItem) {
        selectedItem = inputSelectedItem;
    }
}