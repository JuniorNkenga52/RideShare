package com.app.rideWhiz.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.model.CarYear;

import java.util.List;
import java.util.Locale;

/**
 * Created by hiteshsheth on 30/08/17.
 */
public class CarYearAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    List<CarYear> CarTypesArray;
    String LanguageCode;
    private int itemsCount = 0;


    private static CheckBox lastChecked = null;
    private static int lastCheckedPos = 0;

    public CarYearAdapter(Activity act, List<CarYear> carTypesArray) {
        activity = act;
        CarTypesArray = carTypesArray;
        itemsCount = carTypesArray.size();
        LanguageCode = Locale.getDefault().getLanguage();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_caryear, viewGroup, false);
        CartypeViewHolder viewHolder = new CartypeViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        CartypeViewHolder holder = (CartypeViewHolder) viewHolder;

        CarYear carYearitem = CarTypesArray.get(position);

        holder.text_caryear.setText("" + carYearitem.getYear());


        holder.checkBox_caryear.setChecked(carYearitem.isSelected());
        holder.checkBox_caryear.setId(position);


        if (position == 0 && CarTypesArray.get(0).isSelected() && holder.checkBox_caryear.isChecked()) {
            lastChecked = holder.checkBox_caryear;
            lastCheckedPos = 0;
        }


        if (holder.checkBox_caryear.isChecked()) {
            lastChecked = holder.checkBox_caryear;
            lastCheckedPos = position;
        }


        holder.checkBox_caryear.setTag(holder);
        holder.checkBox_caryear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartypeViewHolder holder = (CartypeViewHolder) v.getTag();

                CheckBox cb = (CheckBox) v;
                int clickedPos = cb.getId();

                if (cb.isChecked()) {
                    if (lastChecked != null) {
                        lastChecked.setChecked(false);
                        CarTypesArray.get(lastCheckedPos).setSelected(false);
                    }

                    lastChecked = cb;
                    lastCheckedPos = clickedPos;
                } else {
                    lastChecked = null;
                }

                CarTypesArray.get(clickedPos).setSelected(cb.isChecked());

                // onpackageClickListener.clickDetailTrip(holder.getAdapterPosition());
            }
        });


        holder.layout_caryear.setId(position);
        holder.layout_caryear.setTag(holder);
        holder.layout_caryear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartypeViewHolder holder = (CartypeViewHolder) v.getTag();

                holder.checkBox_caryear.performClick();

            }
        });

    }

    @Override
    public int getItemCount() {
        return CarTypesArray.size();

    }

    public class CartypeViewHolder extends RecyclerView.ViewHolder {
        TextView text_caryear;
        LinearLayout layout_caryear;
        CheckBox checkBox_caryear;


        public CartypeViewHolder(View view) {
            super(view);
            layout_caryear = (LinearLayout) view.findViewById(R.id.layout_caryear);
            text_caryear = (TextView) view.findViewById(R.id.text_caryear);
            checkBox_caryear = (CheckBox) view.findViewById(R.id.checkBox_caryear);
        }
    }


}
