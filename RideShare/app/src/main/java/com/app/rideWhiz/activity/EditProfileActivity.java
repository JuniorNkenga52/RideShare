package com.app.rideWhiz.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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
import com.app.rideWhiz.api.RideShareApi;
import com.app.rideWhiz.api.response.SignupResponse;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfileActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnClickListener {
    private ImageView mBackIv;
    private TextView mSaveTv;
    private TextView mUserNameTv;

    //private Typeface mRobotoMedium;

    User mUserBean;


    private EditText mFirstNameEt;
    private EditText mLastNameEt;
    private EditText mMobileEt;
    private EditText mEmailEt;
    private EditText mAddressEt;
    private EditText mVhmodel_Et;
    private EditText mVhtype_Et;
    private EditText mMaxpassenger_Et;
    private CheckBox mReqdriver_Ch;
    private LinearLayout layout_req_driver;
    private LinearLayout layout_other_op;

    private CircularImageView mProfileIv;

    CustomProgressDialog mProgressDialog;
    private int ch_val = 0;
    Activity activity;
    Context context;
    Uri imageUri = null;

    private int PICK_CAMERA = 1;
    private int PICK_GALLERY = 2;
    String imagePath = "";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_new);

        PrefUtils.initPreference(this);

        activity = this;
        context = this;
        mUserBean = PrefUtils.getUserInfo();
        mProgressDialog = new CustomProgressDialog(this);

        mBackIv = (ImageView) findViewById(R.id.back_iv);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSaveTv = (TextView) findViewById(R.id.save_tv);
        mSaveTv.setVisibility(View.INVISIBLE);
        mUserNameTv = (TextView) findViewById(R.id.username_tv);
        mFirstNameEt = (EditText) findViewById(R.id.first_name_et);
        mLastNameEt = (EditText) findViewById(R.id.last_name_et);
        mMobileEt = (EditText) findViewById(R.id.mobile_et);
        mEmailEt = (EditText) findViewById(R.id.email_et);
        mAddressEt = (EditText) findViewById(R.id.address_name_et);
        mVhmodel_Et = (EditText) findViewById(R.id.vhmodel_et);
        mVhtype_Et = (EditText) findViewById(R.id.vhtype_et);
        mMaxpassenger_Et = (EditText) findViewById(R.id.maxpassenger_et);
        mReqdriver_Ch = (CheckBox) findViewById(R.id.reqdriver_ch);
        layout_req_driver = (LinearLayout) findViewById(R.id.layout_req_driver);
        layout_other_op = (LinearLayout) findViewById(R.id.layout_other_op);

        if (mUserBean.getMrequested_as_driver() != null) {
            if (mUserBean.getMrequested_as_driver().equals("1") ) {
                mReqdriver_Ch.setChecked(true);
                ch_val = 1;
                layout_other_op.setVisibility(View.VISIBLE);
            }
        }
        mSaveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidations()) {
                    new AsyncUpdateUserProfile().execute();
                }
            }
        });

        mProfileIv = findViewById(R.id.circularImageView);

        if (!PrefUtils.getUserInfo().getThumb_image().equals("")) {
            //mProgressDialog.show();
            Glide.with(activity)
                    .load(PrefUtils.getUserInfo().getThumb_image())
                    .crossFade()
                    .error(R.drawable.user_icon)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            //mProgressDialog.dismiss();
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            //mProgressDialog.dismiss();
                            return false;
                        }
                    })
                    .into(mProfileIv);
        }

        SetValues();

        mProfileIv.setOnClickListener(this);
        mReqdriver_Ch.setOnCheckedChangeListener(this);
        mFirstNameEt.addTextChangedListener(this);
        mLastNameEt.addTextChangedListener(this);
        mAddressEt.addTextChangedListener(this);
        mMobileEt.addTextChangedListener(this);
        mEmailEt.addTextChangedListener(this);
        mVhmodel_Et.addTextChangedListener(this);
        mVhtype_Et.addTextChangedListener(this);
        mMaxpassenger_Et.addTextChangedListener(this);
    }

    private void SetValues() {
        mUserNameTv.setText(mUserBean.getmFirstName() + " " + mUserBean.getmLastName());
        mFirstNameEt.setText(mUserBean.getmFirstName());
        mLastNameEt.setText(mUserBean.getmLastName());
        mMobileEt.setText(mUserBean.getmMobileNo());
        mEmailEt.setText(mUserBean.getmEmail());
        mAddressEt.setText(mUserBean.getmAddress());

        mVhmodel_Et.setText(mUserBean.getMvehicle_model());
        mVhtype_Et.setText(mUserBean.getMvehicle_type());
        mMaxpassenger_Et.setText(mUserBean.getmMax_passengers());
        /*if (ch_val != 0) {
            mVhmodel_Et.setText(mUserBean.getMvehicle_model());
            mVhtype_Et.setText(mUserBean.getMvehicle_type());
            mMaxpassenger_Et.setText(mUserBean.getmMax_passengers());
        }*/
    }

    private boolean checkValidations() {
        if (mFirstNameEt.getText().toString().isEmpty()) {
            MessageUtils.showFailureMessage(EditProfileActivity.this, "Please enter First Name");
            return false;
        } else if (mLastNameEt.getText().toString().isEmpty()) {
            MessageUtils.showFailureMessage(EditProfileActivity.this, "Please enter Last Name");
            return false;
        } else if (mAddressEt.getText().toString().isEmpty()) {
            MessageUtils.showFailureMessage(EditProfileActivity.this, "Please enter Home Address");
            return false;
        } else if (mEmailEt.getText().toString().isEmpty()) {
            MessageUtils.showFailureMessage(EditProfileActivity.this, "Please enter Email Address");
            return false;
        } else if (!AppUtils.isEmail(mEmailEt.getText().toString())) {
            MessageUtils.showFailureMessage(EditProfileActivity.this, "Please enter valid Email Address");
            return false;
        } else if (ch_val != 0) {
           if (mVhmodel_Et.getText().toString().isEmpty()) {
                MessageUtils.showFailureMessage(EditProfileActivity.this, "Please enter Vehicle Model");
                return false;
            } else if (mVhtype_Et.getText().toString().isEmpty()) {
                MessageUtils.showFailureMessage(EditProfileActivity.this, "Please enter Vehicle Type");
                return false;
            } else if (mMaxpassenger_Et.getText().toString().isEmpty()) {
                MessageUtils.showFailureMessage(EditProfileActivity.this, "Please enter Vehicle Max Passenger");
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        RideShareApp.mHomeTabPos = 4;

        Intent i = new Intent(EditProfileActivity.this, HomeNewActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();

    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            selectImage();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {

                    String fileName = "Camera_Example.jpg";

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");

                    imageUri = activity.getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    startActivityForResult(intent, PICK_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    GalleryConfig config = new GalleryConfig.Build()
                            .limitPickPhoto(1)
                            .singlePhoto(false)
                            .filterMimeTypes(new String[]{"image/gif"})
                            .hintOfPick("You can select max 1 photos")
                            .build();
                    GalleryActivity.openActivity(activity, PICK_GALLERY, config);
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mSaveTv.setVisibility(View.VISIBLE);
        if (PICK_GALLERY == requestCode && resultCode == Activity.RESULT_OK) {
            ArrayList<String> photos = (ArrayList<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);

            imagePath = photos.get(0);
            Uri uri = Uri.fromFile(new File(photos.get(0)));
            Glide.with(this).load(uri)
                    .error(R.drawable.user_icon)
                    .into(mProfileIv);

        } else if (PICK_CAMERA == requestCode && resultCode == Activity.RESULT_OK) {
            imagePath = convertImageUriToFile(imageUri, activity);
            Glide.with(this)
                    .load("file://" + imagePath)
                    .error(R.drawable.user_icon)
                    .into(mProfileIv);

        }
    }

    public String convertImageUriToFile(Uri imageUri, Activity activity) {

        Cursor cursor = null;
        String Path = "";
        try {
            String[] proj = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };

            cursor = activity.getContentResolver().query(

                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)

            );

            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            int size = cursor.getCount();

            if (size == 0) {
            } else {
                if (cursor.moveToFirst()) {
                    Path = cursor.getString(file_ColumnIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return Path;
    }

    private void updateProfile() {
        mProgressDialog.show();
        RequestBody mfirstname = RequestBody.create(MultipartBody.FORM, mFirstNameEt.getText().toString());
        RequestBody mlatname = RequestBody.create(MultipartBody.FORM, mLastNameEt.getText().toString());
        RequestBody mMobile = RequestBody.create(MultipartBody.FORM, mMobileEt.getText().toString());
        RequestBody mUserId = RequestBody.create(MultipartBody.FORM, mUserBean.getmUserId());
        RequestBody mEmail = RequestBody.create(MultipartBody.FORM, mEmailEt.getText().toString());
        RequestBody mVh_Model = RequestBody.create(MultipartBody.FORM, mVhmodel_Et.getText().toString());
        RequestBody mVh_Type = RequestBody.create(MultipartBody.FORM, mVhtype_Et.getText().toString());
        RequestBody mMax_Passengers = RequestBody.create(MultipartBody.FORM, mMaxpassenger_Et.getText().toString());
        RequestBody mGroupid = RequestBody.create(MultipartBody.FORM, mUserBean.getmGroup_id());
        RequestBody mReq_driver = RequestBody.create(MultipartBody.FORM, String.valueOf(ch_val));

        RequestBody requestFile = null;
        MultipartBody.Part body = null;

        /*if (images != null) {
            requestFile = RequestBody.create(RestApiInterface.MULTIPART, new File(images.get(0).getPath()));
            body = MultipartBody.Part.createFormData("profile_image", images.get(0).getName(), requestFile);
        }*/

        ApiServiceModule.createService(RestApiInterface.class, context).updateProfile(mUserId, mfirstname, mlatname, mMobile, body, mEmail, mVh_Model, mVh_Type, mMax_Passengers, mReq_driver, mGroupid).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                mProgressDialog.cancel();
                if (response.isSuccessful() && response.body() != null) {
                    PrefUtils.addUserInfo(response.body().getMlist().get(0));
                    MessageUtils.showSuccessMessage(EditProfileActivity.this, response.body().getmMessage());
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            ch_val = 1;
            layout_other_op.setVisibility(View.VISIBLE);
        } else {
            ch_val = 0;
            layout_other_op.setVisibility(View.GONE);
        }
        mSaveTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        mSaveTv.setVisibility(View.VISIBLE);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        new TedPermission(EditProfileActivity.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service" +
                        "\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }


    private class AsyncUpdateUserProfile extends AsyncTask<Objects, Void, String> {

        CustomProgressDialog mProgressDialog;
        String mVh_Model = "";
        String mVh_Type = "";
        String mMax_Passengers = "";

        public AsyncUpdateUserProfile() {

            mProgressDialog = new CustomProgressDialog(EditProfileActivity.this);
            mProgressDialog.show();

            /*if (ch_val == 1) {
                mVh_Model = mVhmodel_Et.getText().toString();
                mVh_Type = mVhtype_Et.getText().toString();
                mMax_Passengers = mMaxpassenger_Et.getText().toString();
                mReq_driver = String.valueOf(ch_val);
            }*/

            mVh_Model = mVhmodel_Et.getText().toString();
            mVh_Type = mVhtype_Et.getText().toString();
            mMax_Passengers = mMaxpassenger_Et.getText().toString();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(Objects... param) {
            try {
                return RideShareApi.updateProfileNew(mUserBean.getmUserId(), mFirstNameEt.getText().toString(),
                        mLastNameEt.getText().toString(), mAddressEt.getText().toString(),
                        mEmailEt.getText().toString(), imagePath,
                        mVh_Model, mVh_Type, mMax_Passengers, String.valueOf(ch_val));
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            mProgressDialog.dismiss();

            try {

                if (result != null) {

                    JSONObject jObj = new JSONObject(result);

                    if (jObj.getString("status").equals("success")) {

                        JSONArray jArrayResult = new JSONArray(jObj.getString("result"));

                        JSONObject jObjResult = jArrayResult.getJSONObject(0);

                        User beanUser = new User();

                        beanUser.setmUserId(jObjResult.getString("u_id"));
                        beanUser.setmGroup_id(jObjResult.getString("group_id"));
                        beanUser.setmFirstName(jObjResult.getString("u_firstname"));
                        beanUser.setmLastName(jObjResult.getString("u_lastname"));
                        beanUser.setmEmail(jObjResult.getString("u_email"));
                        beanUser.setmDescription(jObjResult.getString("description"));
                        beanUser.setmAddress(jObjResult.getString("address"));
                        beanUser.setProfile_image(jObjResult.getString("profile_image"));
                        beanUser.setThumb_image(jObjResult.getString("thumb_image"));
                        beanUser.setmMobileNo(jObjResult.getString("u_mo_number"));
                        beanUser.setmLatitude(jObjResult.getString("u_lat"));
                        beanUser.setmLongitude(jObjResult.getString("u_long"));
                        beanUser.setMu_type(jObjResult.getString("u_type"));
                        beanUser.setMtoken(jObjResult.getString("token"));
                        beanUser.setmMobileNumber(jObjResult.getString("mobile_verify_number"));
                        beanUser.setmIsVerify(jObjResult.getString("verify_mobile"));
                        beanUser.setmRideType(jObjResult.getString("u_ride_type"));
                        beanUser.setmStatus(jObjResult.getString("u_status"));
                        beanUser.setmRidestatus(jObjResult.getString("ride_status"));
                        beanUser.setContact_sync(jObjResult.getString("contact_sync"));
                        beanUser.setmIs_rider(jObjResult.getString("is_rider"));
                        beanUser.setmUpdatedDate(jObjResult.getString("update_date"));
                        beanUser.setmCreatedDate(jObjResult.getString("create_date"));

                        beanUser.setMrequested_as_driver(jObjResult.getString("requested_as_driver"));
                        beanUser.setMvehicle_model(jObjResult.optString("vehicle_model"));
                        beanUser.setMvehicle_type(jObjResult.optString("vehicle_type"));
                        beanUser.setmMax_passengers(jObjResult.optString("max_passengers"));

                        PrefUtils.addUserInfo(beanUser);

                        RideShareApp.mHomeTabPos = 4;

                        Intent i = new Intent(EditProfileActivity.this, HomeNewActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                finishAffinity();
                            }
                        } catch (Exception e) {
                        }

                    } else {
                        MessageUtils.showPleaseTryAgain(EditProfileActivity.this);
                    }
                } else {
                    MessageUtils.showPleaseTryAgain(EditProfileActivity.this);
                }
            } catch (Exception e) {
                MessageUtils.showPleaseTryAgain(EditProfileActivity.this);
            }
        }
    }
}