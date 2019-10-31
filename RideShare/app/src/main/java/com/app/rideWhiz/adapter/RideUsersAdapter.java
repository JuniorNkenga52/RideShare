package com.app.rideWhiz.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.api.response.AcceptRider;
import com.app.rideWhiz.listner.ItemClickListener;
import com.app.rideWhiz.listner.OnStartRideListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class RideUsersAdapter extends RecyclerView.Adapter<RideUsersAdapter.MyViewHolder> {
    private ArrayList<AcceptRider> riders;
    private LayoutInflater layoutInflater;
    private Activity activity;
    private OnStartRideListener onStartRideListener;

    public RideUsersAdapter(Activity activity, ArrayList<AcceptRider> riders) {
        this.activity = activity;
        this.riders = riders;
        layoutInflater = LayoutInflater.from(activity);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_users_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(activity).load(riders.get(position).getFromRider().getThumb_image())
                .error(R.drawable.user_icon)
                .transition(new DrawableTransitionOptions()
                        .crossFade())
                .into(holder.image_user);
        holder.text_user_name.setText(riders.get(position).getFromRider().getmFirstName());

        if (riders.get(position).getFromRider().getIs_new_request()) {
            holder.layout_new_user_request.setVisibility(View.VISIBLE);
        } else {
            holder.layout_new_user_request.setVisibility(View.GONE);
        }

        if (riders.get(position).getFromRider().getRideCount() > 0) {
            holder.layout_unreadmsgs.setVisibility(View.VISIBLE);
            holder.item_txt_chat_counts.setText("" + riders.get(position).getFromRider().getRideCount());
        }else {
            holder.layout_unreadmsgs.setVisibility(View.GONE);
        }
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (onStartRideListener != null) {
                    onStartRideListener.OnChatClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return riders.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircularImageView image_user;
        TextView text_user_name;
        TextView item_txt_chat_counts;
        RelativeLayout layout_new_user_request;
        RelativeLayout layout_unreadmsgs;
        ItemClickListener itemClickListener;

        private MyViewHolder(View itemView) {
            super(itemView);
            image_user = itemView.findViewById(R.id.image_user);
            text_user_name = itemView.findViewById(R.id.text_user_name);
            layout_new_user_request = itemView.findViewById(R.id.layout_new_user_request);
            layout_unreadmsgs = itemView.findViewById(R.id.layout_unreadmsgs);
            item_txt_chat_counts = itemView.findViewById(R.id.item_txt_chat_counts);
            image_user.setOnClickListener(this);
        }

        private void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onClick(view, getPosition(), false);
        }
    }

    public void updateData(String userID) {
        for (int i = 0; i < riders.size(); i++) {
            if (riders.get(i).getFromRider().getnUserId().equals(userID)) {
                riders.remove(i);
            }
        }
        notifyDataSetChanged();
    }

    public void updateIcon(String userID) {
        for (int i = 0; i < riders.size(); i++) {
            if (riders.get(i).getFromRider().getnUserId().equals(userID)) {
                riders.get(i).getFromRider().setIs_new_request(false);
            }
        }
        notifyDataSetChanged();
    }

    public void OnStartRide(OnStartRideListener onStartRideListener) {
        this.onStartRideListener = onStartRideListener;
    }
}
