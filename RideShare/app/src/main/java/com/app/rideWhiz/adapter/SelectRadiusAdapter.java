package com.app.rideWhiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.model.Rider;

import java.util.List;

/**
 * Created by rlogical-dev-48 on 12/19/2017.
 */

public class SelectRadiusAdapter extends BaseAdapter {
    Context context;
    LayoutInflater mInflater;
    private List<String> list_radius;
    private List<Rider> list_drivers;

    public SelectRadiusAdapter(List<Rider> list_drivers, List<String> list_radius, Context context) {
        this.list_drivers = list_drivers;
        this.list_radius = list_radius;
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        if (list_radius != null) {
            return list_radius.size();
        } else {
            return list_drivers.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_select_radius, null);
            holder.item_txt_select_radius = convertView.findViewById(R.id.item_txt_select_radius);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String distance = "";
        if (list_drivers != null) {
            distance = String.format("%.02f", Float.valueOf(list_drivers.get(position).getmDistance()));
        }
        String text = list_radius != null ? list_radius.get(position) : list_drivers.get(position).getmFirstName() + " - " + distance + " Miles";
        holder.item_txt_select_radius.setText(text);
        return convertView;
    }

    private class ViewHolder {
        TextView item_txt_select_radius;
        ImageView item_txt_selected;

    }
}
