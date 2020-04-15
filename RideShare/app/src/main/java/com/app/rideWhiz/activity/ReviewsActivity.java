package com.app.rideWhiz.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.adapter.ReviewHistoryAdapter;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.model.DriverReviewResponse;
import com.app.rideWhiz.model.DriverReviews;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.view.CustomProgressDialog;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewsActivity extends AppCompatActivity {

    private RecyclerView recycler_review_list;
    private TextView txt_ratings;
    private TextView txt_total_ratings;
    private Context context;
    private ArrayList<DriverReviewResponse> driver_reviews_list;
    private CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        context = this;
        customProgressDialog = new CustomProgressDialog(context);
        initViews();
    }

    private void initViews() {
        String driver_id = getIntent().getStringExtra("Driver_id");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ratings");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(getAssets(), "OpenSans-Regular.ttf");
                if (tv.getText().equals(toolbar.getTitle())) {
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }

        recycler_review_list = findViewById(R.id.recycler_review_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recycler_review_list.setLayoutManager(layoutManager);
        recycler_review_list.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recycler_review_list.setHasFixedSize(false);
        recycler_review_list.setLayoutManager(layoutManager);

        txt_ratings = findViewById(R.id.txt_ratings);
        txt_total_ratings = findViewById(R.id.txt_total_ratings);

        driver_reviews_list = new ArrayList<>();
        getDriverReviews(driver_id);
    }


    private void getDriverReviews(final String userId) {
        customProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class, context).getDriverRates(userId).enqueue(new Callback<DriverReviews>() {
            @Override
            public void onResponse(Call<DriverReviews> call, Response<DriverReviews> response) {
                customProgressDialog.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getStatus().equals("error")) {
                        driver_reviews_list = response.body().getResult();
                        if (driver_reviews_list != null && driver_reviews_list.size() > 0) {
                            ReviewHistoryAdapter reviewBookingPrefAdapter = new ReviewHistoryAdapter(context, driver_reviews_list);
                            recycler_review_list.setAdapter(reviewBookingPrefAdapter);
                            txt_ratings.setText(String.format(Locale.getDefault(), "%.1f", Double.parseDouble(response.body().getAvg_rate())));
                            txt_total_ratings.setText("(" + response.body().getResult().size() + " ratings)");
                        } else {
                            MessageUtils.showFailureMessage(context, "No Driver Ratings Found!");
                        }
                    } else {
                        MessageUtils.showFailureMessage(context, response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<DriverReviews> call, Throwable t) {
                customProgressDialog.cancel();
                t.printStackTrace();
                Log.d("error", t.toString());
                Intent i = new Intent(getBaseContext(), SignUpActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }
}
