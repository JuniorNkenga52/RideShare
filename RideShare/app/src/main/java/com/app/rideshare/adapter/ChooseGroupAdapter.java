package com.app.rideshare.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.model.ChooseGroupModel;
import com.app.rideshare.utils.TypefaceUtils;

import java.util.ArrayList;

/**
 * Created by rlogical-dev-48 on 11/7/2017.
 */

public class ChooseGroupAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<ChooseGroupModel> listdata;
        private LayoutInflater mInflater;
        //private Typeface mRobotoMediam;

    public ChooseGroupAdapter(Context context, ArrayList<ChooseGroupModel> listdata) {
        this.context = context;
        this.listdata = listdata;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //mRobotoMediam = TypefaceUtils.getTypefaceRobotoMediam(context);
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
            convertView = mInflater.inflate(R.layout.item_choose_group, null);
            holder = new ViewHolder();

            holder.txt_choose_group = (TextView) convertView.findViewById(R.id.txt_choose_group);
            //holder.txt_choose_group.setTypeface(mRobotoMediam);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txt_choose_group.setTextColor(context.getResources().getColor(R.color.loginbtn));
        holder.txt_choose_group.setText(listdata.get(position).getGroup_name());


        return convertView;
    }

    private class ViewHolder {
        TextView txt_choose_group;
    }


}
