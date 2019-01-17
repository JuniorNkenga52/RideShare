package com.app.rideWhiz.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.response.SignupResponse;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.nguyenhoanglam.imagepicker.activity.ImagePicker;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileActivity extends AppCompatActivity {
    private ImageView mBackIv;
    private TextView mSaveTv;
    private TextView mUserNameTv;

    //private Typeface mRobotoMedium;

    User mUserBean;


    private EditText mFirstNameEt;
    private EditText mLastNameEt;
    private EditText mMobileEt;
    private EditText mEmailEt;
    private EditText mVhmodel_Et;
    private EditText mVhtype_Et;
    private EditText mMaxpassenger_Et;
    private CheckBox mReqdriver_Ch;
    private LinearLayout layout_req_driver;
    private LinearLayout layout_other_op;

    private CircularImageView mProfileIv;
    ArrayList<Image> images;

    private static final int REQUEST_CODE_CHOOSE = 101;
    CustomProgressDialog mProgressDialog;
    private int ch_val = 0;
    Context context;
    //Button mprivileges;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        PrefUtils.initPreference(this);
        context=this;
        mUserBean = PrefUtils.getUserInfo();
        mProgressDialog = new CustomProgressDialog(this);

        //mRobotoMedium= TypefaceUtils.getTypefaceRobotoMediam(this);
        mBackIv = (ImageView) findViewById(R.id.back_iv);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSaveTv = (TextView) findViewById(R.id.save_tv);
        //mSaveTv.setTypeface(mRobotoMedium);
        mSaveTv.setVisibility(View.INVISIBLE);

        mSaveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
        mUserNameTv = (TextView) findViewById(R.id.username_tv);
        // mUserNameTv.setTypeface(mRobotoMedium);
        mUserNameTv.setText(mUserBean.getmFirstName());

        mFirstNameEt = (EditText) findViewById(R.id.first_name_et);
        mLastNameEt = (EditText) findViewById(R.id.last_name_et);
        mMobileEt = (EditText) findViewById(R.id.mobile_et);
        mEmailEt = (EditText) findViewById(R.id.email_et);

        mVhmodel_Et = (EditText) findViewById(R.id.vhmodel_et);
        mVhtype_Et = (EditText) findViewById(R.id.vhtype_et);
        mMaxpassenger_Et = (EditText) findViewById(R.id.maxpassenger_et);
        mReqdriver_Ch = (CheckBox) findViewById(R.id.reqdriver_ch);
        layout_req_driver = (LinearLayout) findViewById(R.id.layout_req_driver);
        layout_other_op = (LinearLayout) findViewById(R.id.layout_other_op);

        /*mFirstNameEt.setTypeface(mRobotoMedium);
        mLastNameEt.setTypeface(mRobotoMedium);
        mMobileEt.setTypeface(mRobotoMedium);
        mEmailEt.setTypeface(mRobotoMedium);

        mVhmodel_Et.setTypeface(mRobotoMedium);
        mVhtype_Et.setTypeface(mRobotoMedium);
        mMaxpassenger_Et.setTypeface(mRobotoMedium);*/

        mFirstNameEt.setText(mUserBean.getmFirstName());
        mLastNameEt.setText(mUserBean.getmLastName());
        mMobileEt.setText(mUserBean.getmMobileNo());
        mEmailEt.setText(mUserBean.getmEmail());
        mVhmodel_Et.setText(mUserBean.getMvehicle_model());
        mVhtype_Et.setText(mUserBean.getMvehicle_type());
        mMaxpassenger_Et.setText(mUserBean.getmMax_passengers());

        if (mUserBean.getMrequested_as_driver() != null) {
            if (mUserBean.getMrequested_as_driver().equals("1")) {
                mReqdriver_Ch.setChecked(true);
                layout_other_op.setVisibility(View.VISIBLE);
            }

        }
        /*mprivileges= (Button) findViewById(R.id.btn_privileges);
        mprivileges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, DriverPrivileges.class);

                startActivity(intent);
            }
        });*/
        mReqdriver_Ch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ch_val = 1;
                    layout_other_op.setVisibility(View.VISIBLE);
                } else {
                    ch_val = 0;
                    layout_other_op.setVisibility(View.GONE);
                }
                mSaveTv.setVisibility(View.VISIBLE);
            }
        });


        mFirstNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSaveTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mLastNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSaveTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mMobileEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSaveTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEmailEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSaveTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mVhmodel_Et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSaveTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mVhtype_Et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSaveTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mMaxpassenger_Et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSaveTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mProfileIv = (CircularImageView) findViewById(R.id.circularImageView);
        if (!PrefUtils.getUserInfo().getProfile_image().equals("")) {
            Picasso.with(this).load(mUserBean.getProfile_image()).into(mProfileIv);
        }

        mProfileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new TedPermission(ProfileActivity.this)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\n" +
                                "Please turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();

            }
        });
    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            ImagePicker.create(ProfileActivity.this)
                    .folderMode(true)
                    .folderTitle("Folder")
                    .imageTitle("Tap to select")
                    .single()
                    .showCamera(true)
                    .imageDirectory("Camera")
                    .start(REQUEST_CODE_CHOOSE);
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            try {
                images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
                mProfileIv.setImageURI(Uri.parse(images.get(0).getPath()));
                mSaveTv.setVisibility(View.VISIBLE);
            } catch (Exception e) {

            }
        }
    }

    private void updateProfile() {
        mProgressDialog.show();
        RequestBody mfirstname = RequestBody.create(okhttp3.MultipartBody.FORM, mFirstNameEt.getText().toString());
        RequestBody mlatname = RequestBody.create(okhttp3.MultipartBody.FORM, mLastNameEt.getText().toString());
        RequestBody mMobile = RequestBody.create(okhttp3.MultipartBody.FORM, mMobileEt.getText().toString());
        RequestBody mUserId = RequestBody.create(okhttp3.MultipartBody.FORM, mUserBean.getmUserId());
        RequestBody mEmail = RequestBody.create(okhttp3.MultipartBody.FORM, mEmailEt.getText().toString());
        RequestBody mVh_Model = RequestBody.create(okhttp3.MultipartBody.FORM, mVhmodel_Et.getText().toString());
        RequestBody mVh_Type = RequestBody.create(okhttp3.MultipartBody.FORM, mVhtype_Et.getText().toString());
        RequestBody mMax_Passengers = RequestBody.create(okhttp3.MultipartBody.FORM, mMaxpassenger_Et.getText().toString());
        RequestBody mGroupid = RequestBody.create(okhttp3.MultipartBody.FORM, mUserBean.getmGroup_id());
        RequestBody mReq_driver = RequestBody.create(okhttp3.MultipartBody.FORM, String.valueOf(ch_val));

        RequestBody requestFile = null;
        MultipartBody.Part body = null;

        if (images != null) {
            requestFile = RequestBody.create(RestApiInterface.MULTIPART, new File(images.get(0).getPath()));
            body = MultipartBody.Part.createFormData("profile_image", images.get(0).getName(), requestFile);
        }

        ApiServiceModule.createService(RestApiInterface.class,context).updateProfile(mUserId, mfirstname, mlatname, mMobile, body, mEmail, mVh_Model, mVh_Type, mMax_Passengers, mReq_driver, mGroupid).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                mProgressDialog.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    PrefUtils.addUserInfo(response.body().getMlist().get(0));
                    MessageUtils.showSuccessMessage(ProfileActivity.this, response.body().getmMessage());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }
}
