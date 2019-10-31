package com.app.rideWhiz.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.activity.SignUpActivity;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;

public class EmailFragment extends Fragment {

    private ImageView imgBack;
    private TextView txtNext;

    public  TextView txtEmailId;

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
                    PrefUtils.putString("UemailID",  SignUpActivity.EmailId);
                    SignUpActivity.mViewPager.setCurrentItem(5);
                }


            }
        });

        return rootView;
    }
}