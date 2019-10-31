package com.app.rideWhiz.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.activity.SignUpActivity;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.hariofspades.incdeclibrary.IncDecCircular;
import com.hariofspades.incdeclibrary.IncDecImageButton;

public class CarInfoFragment extends Fragment {

    private EditText edt_car_model, edt_car_type;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_car_info, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        final IncDecImageButton incdec = rootView.findViewById(R.id.number_picker);
        incdec.setConfiguration(LinearLayout.HORIZONTAL, IncDecCircular.TYPE_INTEGER,
                IncDecCircular.DECREMENT, IncDecCircular.INCREMENT);
        incdec.setupValues(1, 4, 1, 1);
        incdec.enableLongPress(true, true, 500);

        ImageView imgBack = rootView.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.mViewPager.setCurrentItem(4);
            }
        });
        TextView txtNext = rootView.findViewById(R.id.txt_Next);
        edt_car_model=rootView.findViewById(R.id.edt_car_model);
        edt_car_type=rootView.findViewById(R.id.edt_car_type);

        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_car_model.getText().toString().isEmpty()) {
                    MessageUtils.showFailureMessage(getActivity(), "Please Enter Car Model.");
                } else if (edt_car_type.getText().toString().isEmpty()) {
                    MessageUtils.showFailureMessage(getActivity(), "Please Enter Car Type.");
                } else if (incdec.getValue().isEmpty()) {
                    MessageUtils.showFailureMessage(getActivity(), "Please select Max Seats.");
                } else {
                    SignUpActivity.CarModel = edt_car_model.getText().toString().trim();
                    SignUpActivity.CarType = edt_car_type.getText().toString().trim();
                    SignUpActivity.CarSeats = incdec.getValue().trim();
                    PrefUtils.putString("MaxSeats", SignUpActivity.CarSeats);
                    SignUpActivity.mViewPager.setCurrentItem(6);
                }
            }
        });
        return rootView;
    }
}
