package com.app.rideshare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.model.AdminFunctions;

import java.util.ArrayList;

/**
 * Created by rlogical-dev-48 on 11/7/2017.
 */

public class AdminFuncitonsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<AdminFunctions> listdata;
    private LayoutInflater mInflater;

    public AdminFuncitonsAdapter(Context context, ArrayList<AdminFunctions> listdata) {
        this.context = context;
        this.listdata = listdata;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listdata.size();
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
            convertView = mInflater.inflate(R.layout.item_admin_functions, null);
            holder = new ViewHolder();

            holder.item_fname = (TextView) convertView.findViewById(R.id.item_fname);
            holder.item_lname = (TextView) convertView.findViewById(R.id.item_lname);
            holder.item_urole = (TextView) convertView.findViewById(R.id.item_urole);
            holder.item_aprove_req = (CheckBox) convertView.findViewById(R.id.item_aprove_req);
            holder.item_disable = (CheckBox) convertView.findViewById(R.id.item_disable);
            holder.item_aprove_priviledge = (CheckBox) convertView.findViewById(R.id.item_aprove_priviledge);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.item_fname.setText(listdata.get(position).getFname());
        holder.item_lname.setText(listdata.get(position).getLname());
        holder.item_urole.setText(listdata.get(position).getUrole());
        if (listdata.get(position).getAp_dr_req() == 0 || listdata.get(position).getDisble() == 0 || listdata.get(position).getAp_admin_priv() == 0) {
            holder.item_aprove_req.setChecked(true);
            holder.item_disable.setChecked(true);
            holder.item_aprove_priviledge.setChecked(true);
        } else {
            holder.item_aprove_req.setChecked(false);
            holder.item_disable.setChecked(false);
            holder.item_aprove_priviledge.setChecked(false);
        }


        return convertView;
    }

    private class ViewHolder {
        TextView item_fname, item_lname, item_urole;
        CheckBox item_aprove_req, item_disable, item_aprove_priviledge;
    }
}
