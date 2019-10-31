package com.app.rideWhiz.adapter;

/**
 * Created by techintegrity on 29/08/16.
 */

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.rideWhiz.R;

import java.util.ArrayList;
import java.util.HashMap;


public class FinishRideAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    Activity activity;
    ArrayList<HashMap<String, String>> selectNoofPersonArray;
    private int itemsCount = 0;
    private static final int VIEW_TYPE_DEFAULT = 1;
    private static final int VIEW_TYPE_LOADER = 2;
    private boolean showLoadingView = false;

    private OnCarFeaturesClickListener onNoOfPersonClickListener;

    public FinishRideAdapter(Activity activity, ArrayList<HashMap<String, String>> selectNoofPersonArray) {
        this.activity = activity;
        this.selectNoofPersonArray = selectNoofPersonArray;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_no_of_person, parent, false);
        NoOfPersonViewHolder carTypeViewHolder = new NoOfPersonViewHolder(view);
        carTypeViewHolder.rl_no_of_person.setOnClickListener(this);
        return carTypeViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        NoOfPersonViewHolder holder = (NoOfPersonViewHolder) viewHolder;
        if (getItemViewType(position) == VIEW_TYPE_DEFAULT) {
            bindCarTypeFeedItem(position, holder);
        } else if (getItemViewType(position) == VIEW_TYPE_LOADER) {
            bindLoadingFeedItem(holder);
        }
    }

    private void bindCarTypeFeedItem(int position, NoOfPersonViewHolder holder) {
        try {
            HashMap<String, String> hmNoPerson = selectNoofPersonArray.get(position);
            holder.tv_no_of_person.setText(hmNoPerson.get("finish_riders"));
            if (hmNoPerson.get("isSelected").equals("1")) {
                holder.iv_right_icon.setImageResource(R.drawable.ic_car_radio_selected);
            } else {
                holder.iv_right_icon.setImageResource(R.drawable.unselected_radio);
            }
            holder.rl_no_of_person.setTag(holder);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void bindLoadingFeedItem(NoOfPersonViewHolder holder) {
        System.out.println("BindLoadingFeedItem >>>>>");
    }

    @Override
    public int getItemCount() {
        return selectNoofPersonArray.size();
    }

    @Override
    public void onClick(View view) {

        int viewId = view.getId();
        NoOfPersonViewHolder holder = (NoOfPersonViewHolder) view.getTag();
        if (viewId == R.id.rl_no_of_person) {
            if (onNoOfPersonClickListener != null) {
//                resetSelection(holder.getPosition());
                if (holder.getPosition() == 0) {
                    for (int i = 1; i < selectNoofPersonArray.size(); i++) {
                        HashMap<String, String> hmCarFea = selectNoofPersonArray.get(i);
                        hmCarFea.put("isSelected", "0");
                    }
                    HashMap<String, String> hmCarFea = selectNoofPersonArray.get(holder.getPosition());
                    hmCarFea.put("isSelected", "1");

                } else {
                    HashMap<String, String> hmFirstOption = selectNoofPersonArray.get(0);
                    hmFirstOption.put("isSelected", "0");

                    HashMap<String, String> hmCarFea = selectNoofPersonArray.get(holder.getPosition());
                    if (hmCarFea.get("isSelected").equals("0"))
                        hmCarFea.put("isSelected", "1");
                    else
                        hmCarFea.put("isSelected", "0");
                }

                checkNotSelected();
                onNoOfPersonClickListener.selectCarFeatures(holder.getPosition());
            }
        }

    }

    private void checkNotSelected() {
        boolean isSelected = false;
        for (int j = 0; j < selectNoofPersonArray.size(); j++) {
            HashMap<String, String> hm = selectNoofPersonArray.get(j);
            if (hm.get("isSelected").equals("1")) {
                isSelected = true;
            }
        }
        if (!isSelected) {
            HashMap<String, String> hmFirstOption = selectNoofPersonArray.get(0);
            hmFirstOption.put("isSelected", "1");
        }
    }


    private void resetSelection(int position) {
        for (int j = 0; j < selectNoofPersonArray.size(); j++) {
            HashMap<String, String> hm = selectNoofPersonArray.get(j);
            if (j != position) {
                hm.put("isSelected", "0");
            } else {
                if (hm.get("isSelected").equals("1")) {
                    //Skip
                } else if (hm.get("isSelected").equals("0"))
                    hm.put("isSelected", "1");
                else
                    hm.put("isSelected", "0");
            }
        }
    }

    public void updatePersonCount() {
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (showLoadingView && position == 0) {
            return VIEW_TYPE_LOADER;
        } else {
            return VIEW_TYPE_DEFAULT;
        }
    }

    public class NoOfPersonViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rl_no_of_person;
        TextView tv_no_of_person;
        ImageView iv_right_icon;

        public NoOfPersonViewHolder(View view) {
            super(view);
            rl_no_of_person = (RelativeLayout) view.findViewById(R.id.rl_no_of_person);
            tv_no_of_person = (TextView) view.findViewById(R.id.tv_no_of_person);
            iv_right_icon = (ImageView) view.findViewById(R.id.iv_right_icon);
        }
    }

    public void setOnCarFeaturesItemClickListener(OnCarFeaturesClickListener onNoOfPersonClickListener) {
        this.onNoOfPersonClickListener = onNoOfPersonClickListener;
    }

    public interface OnCarFeaturesClickListener {
        public void selectCarFeatures(int position);
    }
}

