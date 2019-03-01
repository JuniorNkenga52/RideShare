package com.app.rideWhiz.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rideWhiz.R;
import com.app.rideWhiz.activity.SignUpActivity;
import com.app.rideWhiz.api.RideShareApi;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.json.JSONObject;

import java.util.Locale;

public class MobileNumberFragment extends Fragment {

    private ImageView imgBack;
    private TextView txtTermsOfService;
    private TextView txtNext;

    public MaskedEditText txtPhoneNumber;

    private CheckBox chkIAgree;
    String wantPermission = Manifest.permission.READ_PHONE_STATE;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_phonenumber, container,
                false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imgBack = (ImageView) rootView.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                getActivity().finish();
            }
        });

        txtTermsOfService = (TextView) rootView.findViewById(R.id.txtTermsOfService);

        String udata = getActivity().getString(R.string.term_of_service);
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        txtTermsOfService.setText(content);

        txtPhoneNumber = (MaskedEditText) rootView.findViewById(R.id.txtPhoneNumber);

        if (!checkPermission(wantPermission)) {
            requestPermission(wantPermission);
        } else {
            txtPhoneNumber.setTextColor(getActivity().getResources().getColor(R.color.white));
            String phno = getPhone();
            if (!phno.equals("")) {
                txtPhoneNumber.setVisibility(View.VISIBLE);
                txtPhoneNumber.setMaskedText(phno);
            }

        }
        chkIAgree = rootView.findViewById(R.id.chkIAgree);
        txtTermsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        txtNext = rootView.findViewById(R.id.txtNext);
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String result = txtPhoneNumber.getUnmaskedText().toString();
                    if (result.length() == 0) {
                        MessageUtils.showFailureMessage(getActivity(), "Please enter Mobile Number.");
                    } else if (!chkIAgree.isChecked()) {
                        MessageUtils.showFailureMessage(getActivity(), "You must agree with the Terms and Conditions");
                    } else {

                        String numberMo = "+1" + result;
                        if (AppUtils.isInternetAvailable(getActivity())) {
                            new AsyncSendTextMessage(numberMo.trim().replaceAll(" ", "")).execute();
                        } else {
                            MessageUtils.showNoInternetAvailable(getActivity());
                        }
                    }
                } catch (Exception e) {
                    MessageUtils.showFailureMessage(getActivity(), "Please enter Mobile Number.");
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }

    public class AsyncSendTextMessage extends AsyncTask<Object, Integer, Object> {

        String mobileNumber;
        CustomProgressDialog mProgressDialog;

        public AsyncSendTextMessage(String mobileNumber) {

            mProgressDialog = new CustomProgressDialog(getActivity());
            mProgressDialog.show();

            this.mobileNumber = mobileNumber;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        public Object doInBackground(Object... params) {
            try {
                //return RideShareApi.sendTextMessageNew(mobileNumber);
                return RideShareApi.sendTextMessageNew(mobileNumber, getContext());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void onPostExecute(Object result) {
            super.onPostExecute(result);

            mProgressDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(result.toString());

                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    JSONObject jsonObjectResult = new JSONObject(jsonObject.getString("result"));
                    SignUpActivity.PhoneNumber = txtPhoneNumber.getUnmaskedText().toString();
                    SignUpActivity.mUserId = jsonObjectResult.getString("user_id");
                    SignUpActivity.mViewPager.setCurrentItem(1);

                    OTPFragment.updateTest();
                    OTPFragment otpFragment = new OTPFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    assert fragmentManager != null;
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame_layout_mobile, otpFragment);
                    fragmentTransaction.commit();


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getPhone() {
        String phno = "";
        TelephonyManager phoneMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), wantPermission) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        if (phoneMgr.getLine1Number() != null) {
            phno = phoneMgr.getLine1Number();
            if (phno.length() == 11) {
                phno = phoneMgr.getLine1Number().substring(1, 11);
            } else if (phno.length() == 12) {
                phno = phoneMgr.getLine1Number().substring(2
                        , 12);
            } else if (phno.length() == 13) {
                phno = phoneMgr.getLine1Number().substring(3, 13);
            } else {
                phno = phoneMgr.getLine1Number();
            }
        } else {
            phno = "";
        }
        return phno;
    }

    private void requestPermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
            Toast.makeText(getActivity(), "Phone state permission allows us to get phone number. Please allow it for additional functionality.", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(getActivity(), "Permission Denied. We can't get phone number.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}