package com.app.rideWhiz.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.api.RideShareApi;
import com.app.rideWhiz.model.CarInfo;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hariofspades.incdeclibrary.IncDecCircular;
import com.hariofspades.incdeclibrary.IncDecImageButton;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class EditProfileActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, TextWatcher, View.OnClickListener, IncDecImageButton.OnValueChangeListener {
    private TextView mSaveTv;
    private TextView mUserNameTv;
    private IncDecImageButton number_of_seats;
    //private Typeface mRobotoMedium;

    User mUserBean;


    private EditText mFirstNameEt;
    private EditText mLastNameEt;
    private EditText mMobileEt;
    private EditText mEmailEt;
    private EditText mAddressEt;
    private EditText mVhmodel_Et;
    private EditText mVhtype_Et;
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

        ImageView mBackIv = findViewById(R.id.back_iv);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSaveTv = findViewById(R.id.save_tv);
        mSaveTv.setVisibility(View.INVISIBLE);
        mUserNameTv = findViewById(R.id.username_tv);
        mFirstNameEt = findViewById(R.id.first_name_et);
        mLastNameEt = findViewById(R.id.last_name_et);
        mMobileEt = findViewById(R.id.mobile_et);
        mEmailEt = findViewById(R.id.email_et);
        mAddressEt = findViewById(R.id.address_name_et);
        mVhmodel_Et = findViewById(R.id.vhmodel_et);
        mVhtype_Et = findViewById(R.id.vhtype_et);
        number_of_seats = findViewById(R.id.number_of_seats);

        number_of_seats.setConfiguration(LinearLayout.HORIZONTAL, IncDecCircular.TYPE_INTEGER,
                IncDecCircular.DECREMENT, IncDecCircular.INCREMENT);
        number_of_seats.enableLongPress(true, true, 500);
        layout_other_op = findViewById(R.id.layout_other_op);

        mSaveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidations()) {
                    new AsyncUpdateUserProfile().execute();
                } else {
                    MessageUtils.showNoInternetAvailable(context);
                }
            }
        });

        mProfileIv = findViewById(R.id.circularImageView);

        if (!PrefUtils.getUserInfo().getThumb_image().equals("")) {


            Glide.with(this)
                    .load(PrefUtils.getUserInfo().getThumb_image())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.user_icon)
                    .dontTransform()
                    .into(mProfileIv);

        }

        SetValues();
        mProfileIv.setOnClickListener(this);
        mFirstNameEt.addTextChangedListener(this);
        mLastNameEt.addTextChangedListener(this);
        mAddressEt.addTextChangedListener(this);
        mMobileEt.addTextChangedListener(this);
        mEmailEt.addTextChangedListener(this);
        mVhmodel_Et.addTextChangedListener(this);
        mVhtype_Et.addTextChangedListener(this);
        number_of_seats.setOnValueChangeListener(this);
    }

    private void SetValues() {
        mUserNameTv.setText(mUserBean.getmFirstName() + " " + mUserBean.getmLastName());
        mFirstNameEt.setText(mUserBean.getmFirstName());
        mLastNameEt.setText(mUserBean.getmLastName());
        mMobileEt.setText(mUserBean.getmMobileNo());
        mEmailEt.setText(mUserBean.getmEmail());
        mAddressEt.setText(mUserBean.getmAddress());
        if (mUserBean.getCar_info() != null) {
            mVhmodel_Et.setText(mUserBean.getCar_info().getCar_model());
            mVhtype_Et.setText(mUserBean.getCar_info().getCar_type());
            number_of_seats.setupValues(1, 4, 1, Float.parseFloat(mUserBean.getCar_info().getSeating_capacity()));
        } else {
            mVhmodel_Et.setText("");
            mVhtype_Et.setText("");
            number_of_seats.setupValues(1, 4, 1, 1);
        }
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
            } else if (number_of_seats.getValue().isEmpty()) {
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

        if (RideShareApp.mHomeTabPos != 0) {
            RideShareApp.mHomeTabPos = 4;
            Intent i = new Intent(EditProfileActivity.this, HomeNewActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            RideShareApp.mRideTypeTabPos = 4;
            Intent i = new Intent(EditProfileActivity.this, RideTypeActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            selectImage();
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {

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
            Glide.with(this)
                    .load(uri)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.user_icon)
                    .dontTransform()
                    .into(mProfileIv);

        } else if (PICK_CAMERA == requestCode && resultCode == Activity.RESULT_OK) {
            imagePath = convertImageUriToFile(imageUri, activity);
            Glide.with(this)
                    .load(imagePath)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.user_icon)
                    .dontTransform()
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

            /*if (size == 0) {
            } else {
                if (cursor.moveToFirst()) {
                    Path = cursor.getString(file_ColumnIndex);
                }
            }*/
            if (size > 0) {
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
         TedPermission.with(EditProfileActivity.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service" +
                        "\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }

    @Override
    public void onValueChange(IncDecImageButton view, float oldValue, float newValue) {
        mSaveTv.setVisibility(View.VISIBLE);
    }


    @SuppressLint("StaticFieldLeak")
    private class AsyncUpdateUserProfile extends AsyncTask<Objects, Void, String> {

        CustomProgressDialog mProgressDialog;
        String mVh_Model;
        String mVh_Type;
        String mMax_Passengers;

        private AsyncUpdateUserProfile() {

            mProgressDialog = new CustomProgressDialog(EditProfileActivity.this);
            mProgressDialog.show();
            mVh_Model = mVhmodel_Et.getText().toString();
            mVh_Type = mVhtype_Et.getText().toString();
            mMax_Passengers = number_of_seats.getValue().trim();
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

                        JSONObject jObjcarInfo = jObjResult.optJSONObject("car_info");
                        CarInfo carInfo = new CarInfo();
                        carInfo.setCar_model(jObjcarInfo.optString("car_model"));
                        carInfo.setCar_type(jObjcarInfo.optString("car_type"));
                        carInfo.setSeating_capacity(jObjcarInfo.optString("seating_capacity"));
                        beanUser.setCar_info(carInfo);

                        PrefUtils.addUserInfo(beanUser);

                        RideShareApp.mHomeTabPos = 4;

                        Intent i = new Intent(EditProfileActivity.this, HomeNewActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                        try {
                            finishAffinity();
                        } catch (Exception e) {
                            e.printStackTrace();
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