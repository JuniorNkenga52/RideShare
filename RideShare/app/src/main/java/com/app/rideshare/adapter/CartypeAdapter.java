package com.app.rideshare.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.model.CarBrand;
import com.app.rideshare.model.CarType;

import java.util.List;
import java.util.Locale;

/**
 * Created by hiteshsheth on 30/08/17.
 */
public class CartypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    List<CarBrand> CarTypesArray;
    String LanguageCode;
    private int itemsCount = 0;


    private static CheckBox lastChecked = null;
    private static int lastCheckedPos = 0;

    public CartypeAdapter(Activity act, List<CarBrand> carTypesArray) {
        activity = act;
        CarTypesArray = carTypesArray;
        itemsCount = carTypesArray.size();
        LanguageCode = Locale.getDefault().getLanguage();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_cartype, viewGroup, false);
        CartypeViewHolder viewHolder = new CartypeViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        CartypeViewHolder holder = (CartypeViewHolder) viewHolder;

        CarBrand carTypeitem = CarTypesArray.get(position);

        holder.text_cartype.setText("" + carTypeitem.getBrand());

        //Picasso.with(activity).load(Url.cartypepath+carTypeitem.getCarsideicon()).into(holder.image_cartype);

        holder.checkBox_cartype.setChecked(carTypeitem.isSelected());
        holder.checkBox_cartype.setId(position);


        if (position == 0 && CarTypesArray.get(0).isSelected() && holder.checkBox_cartype.isChecked()) {
            lastChecked = holder.checkBox_cartype;
            lastCheckedPos = 0;
        }


        if (holder.checkBox_cartype.isChecked()) {
            lastChecked = holder.checkBox_cartype;
            lastCheckedPos = position;
        }


        holder.checkBox_cartype.setTag(holder);
        holder.checkBox_cartype.setOnClickListener(new View.OnClickListener() {
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


        holder.layout_cartype.setId(position);
        holder.layout_cartype.setTag(holder);
        holder.layout_cartype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartypeViewHolder holder = (CartypeViewHolder) v.getTag();

                holder.checkBox_cartype.performClick();

            }
        });

    }


    @Override
    public int getItemCount() {
        return CarTypesArray.size();

    }


    public class CartypeViewHolder extends RecyclerView.ViewHolder {
        TextView text_cartype;
        LinearLayout layout_cartype;
        ImageView image_cartype;
        CheckBox checkBox_cartype;


        public CartypeViewHolder(View view) {
            super(view);
            layout_cartype = (LinearLayout) view.findViewById(R.id.layout_cartype);
            text_cartype = (TextView) view.findViewById(R.id.text_cartype);
            checkBox_cartype = (CheckBox) view.findViewById(R.id.checkBox_cartype);
            image_cartype = (ImageView) view.findViewById(R.id.image_cartype);

        }
    }


}
