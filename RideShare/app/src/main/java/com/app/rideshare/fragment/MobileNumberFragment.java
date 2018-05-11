package com.app.rideshare.fragment;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.activity.SignUpActivity;
import com.app.rideshare.api.RideShareApi;
import com.app.rideshare.utils.AppUtils;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.github.pinball83.maskededittext.MaskedEditText;

import org.json.JSONObject;

public class MobileNumberFragment extends Fragment {

    private ImageView imgBack;
    private TextView txtTermsOfService;
    private TextView txtNext;

    public MaskedEditText txtPhoneNumber;

    private CheckBox chkIAgree;

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

        //txtPhoneNumber.setRawInputType(InputType.TYPE_CLASS_NUMBER);

        /*try {
            InputFilter filter = new InputFilter() {
                public CharSequence filter(CharSequence source, int start, int end,
                                           Spanned dest, int dstart, int dend) {
                    for (int i = start; i < end; i++) {
                        if (Character.isWhitespace(source.charAt(i))) {
                            return "";
                        }
                    }
                    return null;
                }
            };
            txtPhoneNumber.setFilters(new InputFilter[]{filter});
        } catch (Exception e) {
            e.printStackTrace();
        }*/


        chkIAgree = (CheckBox) rootView.findViewById(R.id.chkIAgree);

        txtTermsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        txtNext = (TextView) rootView.findViewById(R.id.txtNext);
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String result = txtPhoneNumber.getUnmaskedText().toString();
                    if (result.length() == 0) {
                        ToastUtils.showShort(getActivity(), "Please enter Mobile Number.");
                    } else if (result.replaceAll(" ","").length() != 10) {
                        ToastUtils.showShort(getActivity(), "Please enter valid Mobile Number.");
                    } else if (!chkIAgree.isChecked()) {
                        ToastUtils.showShort(getActivity(), "You must agree with the Terms and Conditions");
                    } else {
                        //String numberMo = "+1"+ result;
                        String numberMo = "+91" + result;

                        //result = "+919265094032";-nikunj
                        //result="+917359371716";
                        //result="+917435068611";-Ajay
                        //result="+919725672270";
                        //result="+919265762630";-Darshan
                        //result="+917600902008";
                        if (AppUtils.isInternetAvailable(getActivity())) {
                            new AsyncSendTextMessage(numberMo).execute();
                        } else {
                            AppUtils.showNoInternetAvailable(getActivity());
                        }
                    }
                }catch (Exception e){
                    ToastUtils.showShort(getActivity(), "Please enter Mobile Number.");
                    e.printStackTrace();
                }
                //SignUpActivity.mViewPager.setCurrentItem(3);
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
                return RideShareApi.sendTextMessageNew(mobileNumber);
                //return RideShareApi.sendTextMessageNew("+1"+mobileNumber);
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
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

}