package com.app.rideWhiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.rideWhiz.R;

import java.util.ArrayList;

public class ChatAdapterAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context context;
    private ArrayList<String> mlist;

    public ChatAdapterAdapter(Context context, ArrayList<String> mlist) {

        this.context = context;
        this.mlist = mlist;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public String getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        private TextView mNameTv;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_chat, null);
            holder = new ViewHolder();

            holder.mNameTv=(TextView)convertView.findViewById(R.id.chat_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

            holder.mNameTv.setText(mlist.get(position));

        return convertView;
    }
    public void updatelist(String message)
    {
        mlist.add(message);
        notifyDataSetChanged();
    }

}