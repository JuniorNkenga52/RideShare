package com.app.rideshare.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

public class HistoryFragment extends Fragment {

    public static HistoryFragment newInstance() {
        Bundle bundle = new Bundle();
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private ListView mHistoryLv;
    CustomProgressDialog mProgressDialog;
    User mUserBean;
    private HistoryAdapter mHistoryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_history, null);

        mHistoryLv = (ListView) rootview.findViewById(R.id.history_lv);

        mProgressDialog = new CustomProgressDialog(getActivity());

        PrefUtils.initPreference(getActivity());
        mUserBean = PrefUtils.getUserInfo();

        getHistory(mUserBean.getmUserId());


        return rootview;
    }

    private void getHistory(String mId) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).getHistory(mId).enqueue(new Callback<HistoryResponse>() {
            @Override
            public void onResponse(Call<HistoryResponse> call, Response<HistoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mHistoryAdapter = new HistoryAdapter(getActivity(), response.body().getResult());
                    mHistoryLv.setAdapter(mHistoryAdapter);
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
