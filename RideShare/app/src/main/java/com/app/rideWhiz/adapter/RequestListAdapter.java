package com.app.rideWhiz.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.activity.RideShareApp;
import com.app.rideWhiz.listner.CallbackRequestType;
import com.app.rideWhiz.listner.ItemClickListener;
import com.app.rideWhiz.listner.OnFinishViewListener;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.utils.DialogUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Locale;

public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.MyViewHolder> {
    private ArrayList<User> UserInfoArr;
    private LayoutInflater layoutInflater;
    private Activity activity;
    private Dialog dialog;
    private OnFinishViewListener onFinishViewListener;

    public RequestListAdapter(Activity activity, ArrayList<User> userInfoArr, RideShareApp mapp) {
        this.activity = activity;
        UserInfoArr = userInfoArr;
        layoutInflater = LayoutInflater.from(activity);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_requests_list, parent, false);
        return new MyViewHolder(view);
    }

    public void updateData(User User, int type) {
        if (type == 0) {
            UserInfoArr.add(User);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyItemInserted(UserInfoArr.size());
                    //notifyDataSetChanged();
                }
            });
        } else {
            for (int i = 0; i < UserInfoArr.size(); i++) {
                if (UserInfoArr.get(i).getmUserId().equals(User.getmUserId())) {
                    UserInfoArr.remove(i);
                }
            }
            if (UserInfoArr.size() > 0) {
                notifyDataSetChanged();
            } else {
                if (onFinishViewListener != null) {
                    onFinishViewListener.onFinished();
                }
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(activity).load(UserInfoArr.get(position).getThumb_image())
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform()
                .error(R.drawable.user_icon)
                .into(holder.image_user);
        holder.text_user_name.setText(UserInfoArr.get(position).getmFirstName());

        if(UserInfoArr.get(position).getAdd_ride_time()!=null){
            String time = String.format(Locale.US, "%.0f", Float.parseFloat(UserInfoArr.get(position).getAdd_ride_time()));
            String min = time.equals("0") ? " min" : " mins";
            holder.txt_time.setText(time + min);
            holder.txt_distance.setText(String.format(Locale.US, "%.1f", Float.parseFloat(UserInfoArr.get(position).getAdd_ride_distance())));
        }
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                showDialog(activity, UserInfoArr.get(position), position);
                //calculateTime_Distance(UserInfoArr.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return UserInfoArr.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircularImageView image_user;
        TextView text_user_name;
        ItemClickListener clickListener;
        TextView txt_time, txt_distance;

        private MyViewHolder(View itemView) {
            super(itemView);
            image_user = itemView.findViewById(R.id.image_user);
            text_user_name = itemView.findViewById(R.id.text_user_name);
            txt_time = itemView.findViewById(R.id.txt_time);
            txt_distance = itemView.findViewById(R.id.txt_distance);
            itemView.setOnClickListener(this);
        }

        private void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
    }

    private void showDialog(final Activity activity, final User fromUser, final int pos) {
        dialog = new DialogUtils(activity).buildDialogRequestType(new CallbackRequestType() {
            @Override
            public void onAccept() {
                dialog.dismiss();
                if (onFinishViewListener != null) {
                    onFinishViewListener.onAccepted(pos);
                }
            }

            @Override
            public void onReject() {
                dialog.dismiss();
                if (onFinishViewListener != null) {
                    onFinishViewListener.onRejected(pos);
                }
                /*if (UserInfoArr.size() > 1) {
                    UserInfoArr.remove(pos);
                    notifyDataSetChanged();
                    activity.finish();
                }*/
            }

            @Override
            public void onCancel() {
                dialog.dismiss();
            }
        }, activity.getResources().getString(R.string.accept_reject) + " " + fromUser.getmFirstName(), fromUser.getStart_address(), fromUser.getEnd_address());

        dialog.show();
    }

    public void OnFinished(OnFinishViewListener onFinishViewListener) {
        this.onFinishViewListener = onFinishViewListener;
    }

}