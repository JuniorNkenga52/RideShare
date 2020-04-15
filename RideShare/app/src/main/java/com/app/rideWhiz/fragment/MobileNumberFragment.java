package com.app.rideWhiz.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.app.rideWhiz.R;
import com.app.rideWhiz.activity.SignUpActivity;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.RideShareApi;
import com.app.rideWhiz.api.response.SignupResponse;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.json.JSONObject;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileNumberFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 1;
    public MaskedEditText txtPhoneNumber;
    //private BroadcastReceiver mRegistrationBroadcastReceiver;
    String wantPermission = Manifest.permission.READ_PHONE_STATE;
    String token;
    private ImageView imgBack;
    private TextView txtTermsOfService;
    private TextView txtNext;
    private CheckBox chkIAgree;

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_phonenumber, container,
                false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        imgBack = rootView.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                getActivity().finish();
            }
        });

        txtTermsOfService = rootView.findViewById(R.id.txtTermsOfService);

        String udata = getActivity().getString(R.string.term_of_service);
        SpannableString content = new SpannableString(udata);
        content.setSpan(new UnderlineSpan(), 0, udata.length(), 0);
        txtTermsOfService.setText(content);

        txtPhoneNumber = rootView.findViewById(R.id.txtPhoneNumber);

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


        txtTermsOfService.setOnClickListener(v -> {

        });

        txtNext = rootView.findViewById(R.id.txtNext);
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String result = txtPhoneNumber.getUnmaskedText();
                    if (result.length() == 0) {
                        MessageUtils.showFailureMessage(getActivity(), "Please enter Mobile Number.");
                    } else if (!chkIAgree.isChecked()) {
                        MessageUtils.showFailureMessage(getActivity(), "You must agree with the Terms and Conditions");
                    } else {
                        String numberMo = "+1" + result;
                        //String numberMo = "+91" + result;
                        //String numberMo = "+" + AppUtils.getCountryTelephoneCode(getApplicationContext()) + result;
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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
            return result == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    public String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = getContext().getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
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
                if (result != null) {
                    JSONObject jsonObject = new JSONObject(result.toString());

                    if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                        JSONObject jsonObjectResult = new JSONObject(jsonObject.getString("result"));
                        SignUpActivity.PhoneNumber = txtPhoneNumber.getUnmaskedText();
                        SignUpActivity.mUserId = jsonObjectResult.getString("user_id");
                        //SignUpActivity.mViewPager.setCurrentItem(1);
                        //OTPFragment.updateTest();
                        getUserDetails(jsonObjectResult.getString("user_id"));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    private void getUserDetails(final String userId) {
        ApiServiceModule.createService(RestApiInterface.class, getActivity()).getUserDetails(userId).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getmStatus().equals("error")) {
                        PrefUtils.addMyGroupInfo(response.body().getGroups());
                        SignUpActivity.mViewPager.setCurrentItem(1);
                        OTPFragment.updateTest();
                    } else {
                        MessageUtils.showFailureMessage(getActivity(), response.body().getmMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
            }
        });
    }
}