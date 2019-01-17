package com.app.rideWhiz.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.activity.SignUpActivity;
import com.app.rideWhiz.utils.MessageUtils;

public class NameFragment extends Fragment {

    private ImageView imgBack;
    private TextView txtNext;

    public TextView txtFirstName, txtLastName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_name, container,
                false);

        imgBack = (ImageView) rootView.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.mViewPager.setCurrentItem(1);
            }
        });

        txtFirstName = (TextView) rootView.findViewById(R.id.txtFirstName);
        txtLastName = (TextView) rootView.findViewById(R.id.txtLastName);

        txtFirstName.setText(SignUpActivity.FirstName);
        txtLastName.setText(SignUpActivity.LastName);

        txtNext = (TextView) rootView.findViewById(R.id.txtNext);
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtFirstName.getText().toString().isEmpty()) {
                    MessageUtils.showFailureMessage(getActivity(), "Please enter First Name.");
                } else if (txtLastName.getText().toString().isEmpty()) {
                    MessageUtils.showFailureMessage(getActivity(), "Please enter Last Name.");
                } else {

                    SignUpActivity.FirstName = txtFirstName.getText().toString().trim();
                    SignUpActivity.LastName = txtLastName.getText().toString().trim();

                    SignUpActivity.mViewPager.setCurrentItem(3);
                }

            }
        });

        return rootView;
    }
}