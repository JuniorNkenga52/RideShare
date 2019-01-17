package com.app.rideWhiz.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.adapter.HistoryAdapter;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.response.HistoryResponse;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {

    CustomProgressDialog mProgressDialog;
    User mUserBean;
    private ListView mHistoryLv;
    private HistoryAdapter mHistoryAdapter;
    private TextView mNoHistoryTv;
    //private Typeface mRobotoMeduim;
    Context context;
    public static HistoryFragment newInstance() {
        Bundle bundle = new Bundle();
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_history, null);

        context=getContext();
        mHistoryLv = (ListView) rootview.findViewById(R.id.history_lv);

        mProgressDialog = new CustomProgressDialog(getActivity());

        PrefUtils.initPreference(getActivity());
        mUserBean = PrefUtils.getUserInfo();

        mNoHistoryTv = (TextView) rootview.findViewById(R.id.no_history);
        //mRobotoMeduim = TypefaceUtils.getTypefaceRobotoMediam(getActivity());
        //mNoHistoryTv.setTypeface(mRobotoMeduim);
        mNoHistoryTv.setVisibility(View.GONE);

        getHistory(mUserBean.getmUserId());

        return rootview;
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
                        mHistoryAdapter = new HistoryAdapter(getActivity(), response.body().getResult(), mId);
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
