package com.app.rideshare.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.activity.SignUpActivity;
import com.app.rideshare.utils.AppUtils;
import com.app.rideshare.utils.MessageUtils;

public class EmailFragment extends Fragment {

    private ImageView imgBack;
    private TextView txtNext;

    public static TextView txtEmailId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_email, container,
                false);

        imgBack = (ImageView) rootView.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.mViewPager.setCurrentItem(3);
            }
        });

        txtEmailId = (TextView) rootView.findViewById(R.id.txtEmailId);
        txtEmailId.setText(SignUpActivity.EmailId);

        txtNext = (TextView) rootView.findViewById(R.id.txtNext);
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEmailId.getText().toString().isEmpty()) {
                    MessageUtils.showFailureMessage(getActivity(), "Please enter email id.");
                } else if (!AppUtils.isEmail(txtEmailId.getText().toString())) {
                    MessageUtils.showFailureMessage(getActivity(), "Please enter valid email id.");
                } else {

                    SignUpActivity.EmailId = txtEmailId.getText().toString().trim();

                    SignUpActivity.mViewPager.setCurrentItem(5);
                }


            }
        });

        return rootView;
    }
}