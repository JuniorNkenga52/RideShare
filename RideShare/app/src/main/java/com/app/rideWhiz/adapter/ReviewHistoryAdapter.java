package com.app.rideWhiz.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.model.DriverReviewResponse;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;

public class ReviewHistoryAdapter extends RecyclerView.Adapter<ReviewHistoryAdapter.ItemRowHolder> {

    private ArrayList<DriverReviewResponse> driver_reviews_list;
    private Context context;

    public ReviewHistoryAdapter(Context context, ArrayList<DriverReviewResponse> driver_reviews_list) {
        this.driver_reviews_list = driver_reviews_list;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemLayoutView = LayoutInflater.from(context).inflate(R.layout.item_review_history, viewGroup, false);
        return new ItemRowHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder itemRowHolder, final int position) {

        final DriverReviewResponse bean = driver_reviews_list.get(position);
        itemRowHolder.ratings_star.setFocusable(false);
        itemRowHolder.ratings_star.setIndicator(true);
        itemRowHolder.ratings_star.setRating(Float.parseFloat(bean.getRate()));
        itemRowHolder.txt_review_comments.setText(bean.getReview());
        itemRowHolder.txt_review_date.setText(bean.getUpdated_date());
    }

    @Override
    public int getItemCount() {
        return driver_reviews_list.size();
        //return 10;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {

        SimpleRatingBar ratings_star;
        TextView txt_review_comments;
        TextView txt_review_date;

        ItemRowHolder(View view) {
            super(view);
            ratings_star = view.findViewById(R.id.ratings_star);
            txt_review_comments = view.findViewById(R.id.txt_review_comments);
            txt_review_date = view.findViewById(R.id.txt_review_date);
        }
    }
}