package com.http_s.rest.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.http_s.rest.R
import com.http_s.rest.adapter.data.SpinnerItemData

class SpinnerAdapter(
    context: Activity,
    private var groupid: Int,
    id: Int,
    private var list: ArrayList<SpinnerItemData>
) :
    ArrayAdapter<SpinnerItemData?>(context, id, list as List<SpinnerItemData?>) {
    private var inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var selectedItem: Int = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var localConvertView = convertView
        if (convertView == null) {
            localConvertView = inflater.inflate(groupid, parent, false)
        }
        val textView = localConvertView!!.findViewById<TextView>(R.id.spinnerTextview)
        textView.text = list[position].text

        return localConvertView
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    fun setSelectedItem(inputSelectedItem: Int) {
        selectedItem = inputSelectedItem
    }
}