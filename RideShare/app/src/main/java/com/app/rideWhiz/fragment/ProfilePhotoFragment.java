package com.app.rideWhiz.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.activity.HomeNewActivity;
import com.app.rideWhiz.activity.SignUpActivity;
import com.app.rideWhiz.api.RideShareApi;
import com.app.rideWhiz.model.CarInfo;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.bumptech.glide.Glide;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;
import com.tangxiaolv.telegramgallery.GalleryActivity;
import com.tangxiaolv.telegramgallery.GalleryConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfilePhotoFragment extends Fragment {

    static Context context;

    private ImageView imgBack;
    private TextView txtNext;

    static CircularImageView imgProfilePhoto;

    Uri imageUri = null;

    private int PICK_CAMERA = 1;
    private int PICK_GALLERY = 2;
    String firstname, lastname, homeaddress, emailid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo, container,
                false);

        PrefUtils.initPreference(getActivity());

        context = getActivity();

        imgBack = (ImageView) rootView.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.mViewPager.setCurrentItem(4);
            }
        });

        txtNext = (TextView) rootView.findViewById(R.id.txtNext);
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updateProfile();

                new AsyncUpdateUserProfile().execute();

               /* Intent i = new Intent(getActivity(), HomeNewActivity.class);
                startActivity(i);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                getActivity().finish();*/
            }
        });

        imgProfilePhoto = (CircularImageView) rootView.findViewById(R.id.imgProfilePhoto);

        if (SignUpActivity.ProfilePhotoPath.length() != 0) {
            Picasso.get().load("file://" + SignUpActivity.ProfilePhotoPath).resize(300, 300).centerCrop().into(imgProfilePhoto);
        }

        imgProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnitemClick();
            }
        });
        firstname = SignUpActivity.FirstName;
        lastname = SignUpActivity.LastName;
        homeaddress = SignUpActivity.HomeAddress;
        emailid = PrefUtils.getString("UemailID");
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private class AsyncUpdateUserProfile extends AsyncTask<Objects, Void, String> {

        CustomProgressDialog mProgressDialog;

        public AsyncUpdateUserProfile() {

            mProgressDialog = new CustomProgressDialog(getActivity());
            mProgressDialog.show();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(Objects... param) {
            try {
                /*return RideShareApi.updateProfileNew(SignUpActivity.mUserId, SignUpActivity.FirstName,
                        SignUpActivity.LastName, SignUpActivity.HomeAddress,
                        SignUpActivity.EmailId, SignUpActivity.ProfilePhotoPath,
                        "", "", "");*/
                return RideShareApi.updateProfileNew(SignUpActivity.mUserId, firstname,
                        lastname, homeaddress,
                        PrefUtils.getString("UemailID"), SignUpActivity.ProfilePhotoPath,
                        SignUpActivity.CarModel, SignUpActivity.CarType, SignUpActivity.CarSeats,"0");
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

                        PrefUtils.putString("isBlank", "true");
                        Intent i = new Intent(getActivity(), HomeNewActivity.class);
                        startActivity(i);
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        getActivity().finish();
                        /*Intent i = new Intent(getActivity(), GroupSelectionFragment.class);
                        startActivity(i);
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        getActivity().finish();*/
                        /*Intent i = new Intent(getActivity(), RideTypeActivity.class);
                        startActivity(i);
                        getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        getActivity().finish();*/

                    } else {
                        MessageUtils.showPleaseTryAgain(getActivity());
                    }
                } else {
                    MessageUtils.showPleaseTryAgain(getActivity());
                }
            } catch (Exception e) {
                MessageUtils.showPleaseTryAgain(getActivity());
            }
        }
    }

    public Void OnitemClick() {

        TedPermission.with(getActivity())
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\n" +
                        "Please turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

        return null;
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {

                    String fileName = "Camera_Example.jpg";

                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");

                    imageUri = getActivity().getContentResolver().insert(
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
                    GalleryActivity.openActivity(getActivity(), PICK_GALLERY, config);
                }
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    public static void setProfilePhoto(String ProfilePhoto) {
        SignUpActivity.ProfilePhotoPath = ProfilePhoto;
        Picasso.get().load("file://" + SignUpActivity.ProfilePhotoPath).resize(300, 300).centerCrop().into(imgProfilePhoto);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (PICK_GALLERY == requestCode && resultCode == Activity.RESULT_OK) {
            ArrayList<String> photos = (ArrayList<String>) data.getSerializableExtra(GalleryActivity.PHOTOS);


            Picasso.get().load(photos.get(0)).into(imgProfilePhoto);
        } else if (PICK_CAMERA == requestCode && resultCode == Activity.RESULT_OK) {
            String imagePath = "file://" + convertImageUriToFile(imageUri, getActivity());
            SignUpActivity.ProfilePhotoPath = convertImageUriToFile(imageUri, getActivity());

            Glide.with(getActivity()).load(new File(SignUpActivity.ProfilePhotoPath)).into(imgProfilePhoto);
            //Picasso.with(getActivity()).load("file://" + SignUpActivity.ProfilePhotoPath).resize(300, 300).centerCrop().into(imgProfilePhoto);
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

                    imageUri,//  Get data for specific image URI
                    proj,    //  Which columns to return
                    null,    //  WHERE clause; which rows to return (all rows)
                    null,    //  WHERE clause selection arguments (none)
                    null     //  Order-by clause (ascending by name)

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
}