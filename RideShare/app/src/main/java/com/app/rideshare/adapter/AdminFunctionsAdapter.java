package com.app.rideshare.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.activity.AdminUserinfoActivity;
import com.app.rideshare.model.GroupusersModel;

import java.util.ArrayList;

public class AdminFunctionsAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<GroupusersModel> listdata;
    private LayoutInflater mInflater;

    public AdminFunctionsAdapter(Context context, ArrayList<GroupusersModel> listdata) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_admin_functions, null);
            holder = new ViewHolder();

            holder.item_user_name_tv = (TextView) convertView.findViewById(R.id.item_user_name_tv);
            holder.item_phno_tv = (TextView) convertView.findViewById(R.id.item_phno_tv);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.item_user_name_tv.setText(listdata.get(position).getU_firstname());
        holder.item_phno_tv.setText(listdata.get(position).getU_mo_number());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AdminUserinfoActivity.class);
                intent.putExtra("pos",position);
                context.startActivity(intent);
            }
        });


        return convertView;
    }

    private class ViewHolder {
        TextView item_user_name_tv, item_phno_tv;
    }
}
