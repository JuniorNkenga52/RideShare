package com.app.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.adapter.HistoryAdapter;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.HistoryResponse;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.view.CustomProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    CustomProgressDialog mProgressDialog;
    User mUserBean;
    private ListView mHistoryLv;
    private HistoryAdapter mHistoryAdapter;
    private TextView mNoHistoryTv;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_history);

        context=this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("History");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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

        mHistoryLv = (ListView) findViewById(R.id.history_lv);

        mProgressDialog = new CustomProgressDialog(this);

        PrefUtils.initPreference(this);
        mUserBean = PrefUtils.getUserInfo();

        mNoHistoryTv = (TextView) findViewById(R.id.no_history);
        mNoHistoryTv.setVisibility(View.GONE);

        getHistory(mUserBean.getmUserId());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        RideShareApp.mHomeTabPos = 4;

        Intent i = new Intent(HistoryActivity.this, HomeNewActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();

    }

    private void getHistory(final String mId) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class,context).getHistory(mId).enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    if (response.body().getResult().size() == 0) {
                        mNoHistoryTv.setVisibility(View.VISIBLE);
                    } else {
                        mHistoryAdapter = new HistoryAdapter(HistoryActivity.this, response.body().getResult(), mId);
                        mHistoryLv.setAdapter(mHistoryAdapter);
                    }
                } else {

                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<HistoryResponse> call, Throwable t) {
                t.printStackTrace();
                mProgressDialog.cancel();
            }
        });
    }
}
