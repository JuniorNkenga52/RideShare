package com.app.rideshare.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.model.HistoryBean;
import com.app.rideshare.utils.AppUtils;
import com.app.rideshare.utils.TypefaceUtils;

import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context;
    private Typeface mRobotoMedium;
    private ArrayList<HistoryBean> mlist;

    public HistoryAdapter(Context context, ArrayList<HistoryBean> mlist) {

        this.context = context;
        this.mlist = mlist;
        mRobotoMedium = TypefaceUtils.getTypefaceRobotoMediam(context);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public String getItem(int position) {
        return mlist.get(position).getName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        private TextView mNameTv;
        private TextView mRideTypeTv;
        private TextView mStatusTv;
        private TextView mStatusResultTv;
        private TextView mPickupAddressTv;
        private TextView mDropoffAddressTv;
        private TextView mTimeTv;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_history, null);
            holder = new ViewHolder();

            holder.mNameTv=(TextView)convertView.findViewById(R.id.username_tv);
            holder.mRideTypeTv=(TextView)convertView.findViewById(R.id.ride_type_tv);
            holder.mStatusTv=(TextView)convertView.findViewById(R.id.status_tv);
            holder.mStatusResultTv=(TextView)convertView.findViewById(R.id.status_result_tv);
            holder.mPickupAddressTv=(TextView)convertView.findViewById(R.id.pickup_address_tv);
            holder.mDropoffAddressTv=(TextView)convertView.findViewById(R.id.drop_off_address);
            holder.mTimeTv=(TextView)convertView.findViewById(R.id.time_tv);

            holder.mNameTv.setTypeface(mRobotoMedium);
            holder.mRideTypeTv.setTypeface(mRobotoMedium);
            holder.mStatusTv.setTypeface(mRobotoMedium);
            holder.mStatusResultTv.setTypeface(mRobotoMedium);
            holder.mPickupAddressTv.setTypeface(mRobotoMedium);
            holder.mDropoffAddressTv.setTypeface(mRobotoMedium);
            holder.mTimeTv.setTypeface(mRobotoMedium);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        HistoryBean bean = mlist.get(position);

        holder.mNameTv.setText(bean.getName());
        holder.mPickupAddressTv.setText(bean.getStarting_address());
        holder.mDropoffAddressTv.setText(bean.getEnding_address());
        holder.mTimeTv.setText(AppUtils.dateformat(bean.getTime()));

        return convertView;
    }
}