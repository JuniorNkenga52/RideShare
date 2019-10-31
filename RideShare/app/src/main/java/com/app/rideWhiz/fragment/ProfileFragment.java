package com.app.rideWhiz.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.app.rideWhiz.R;
import com.app.rideWhiz.activity.EditProfileActivity;
import com.app.rideWhiz.activity.HistoryActivity;
import com.app.rideWhiz.activity.ManageCarActivity;
import com.app.rideWhiz.activity.MyGroupActivity;
import com.app.rideWhiz.activity.MyGroupSelectionActivity;
import com.app.rideWhiz.activity.RideShareApp;
import com.app.rideWhiz.activity.RideTypeActivity;
import com.app.rideWhiz.activity.SettingActivity;
import com.app.rideWhiz.activity.SignUpActivity;
import com.app.rideWhiz.listner.OnBackPressedListener;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ProfileFragment extends Fragment implements OnBackPressedListener, View.OnClickListener {

    CircularImageView imgProfilePhoto;
    TextView txtUserName;

    private LinearLayout llMyProfile;
    private LinearLayout llMyGroup;
    private LinearLayout llManageCar;
    private LinearLayout llHistory;
    private LinearLayout need_ride_ll;
    private LinearLayout offer_ride_ll;
    private LinearLayout llSetting;
    private LinearLayout llLogout;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container,
                false);

        PrefUtils.initPreference(getActivity());
        imgProfilePhoto = (CircularImageView) rootView.findViewById(R.id.imgProfilePhoto);

        if (PrefUtils.getUserInfo().getThumb_image().length() != 0) {
            Glide.with(getActivity())
                    .load(PrefUtils.getUserInfo().getThumb_image())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .error(R.drawable.user_icon)
                    .dontTransform()
                    .into(imgProfilePhoto);
        }

        txtUserName = (TextView) rootView.findViewById(R.id.txtUserName);
        txtUserName.setText(PrefUtils.getUserInfo().getmFirstName() + " " + PrefUtils.getUserInfo().getmLastName());

        llMyProfile = (LinearLayout) rootView.findViewById(R.id.llMyProfile);
        llMyProfile.setOnClickListener(this);

        llMyGroup = (LinearLayout) rootView.findViewById(R.id.llMyGroup);
        llMyGroup.setOnClickListener(this);

        llManageCar = (LinearLayout) rootView.findViewById(R.id.llManageCar);
        llManageCar.setOnClickListener(this);

        llHistory = (LinearLayout) rootView.findViewById(R.id.llHistory);
        llHistory.setOnClickListener(this);

        llSetting = (LinearLayout) rootView.findViewById(R.id.llSetting);
        llSetting.setOnClickListener(this);

        need_ride_ll = (LinearLayout) rootView.findViewById(R.id.need_ride_ll);
        need_ride_ll.setOnClickListener(this);

        offer_ride_ll = (LinearLayout) rootView.findViewById(R.id.offer_ride_ll);
        offer_ride_ll.setOnClickListener(this);


        llLogout = (LinearLayout) rootView.findViewById(R.id.llLogout);
        llLogout.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void doBack() {
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        if (AppUtils.isInternetAvailable(getActivity())) {
            switch (v.getId()) {
                case R.id.llMyProfile:
                    Intent i = new Intent(getActivity(), EditProfileActivity.class);
                    startActivity(i);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    getActivity().finish();
                    break;
                case R.id.llMyGroup:
                    Intent i2 = new Intent(getActivity(), MyGroupActivity.class);
                    startActivity(i2);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    getActivity().finish();
                    break;
                case R.id.llManageCar:
                    Intent i3 = new Intent(getActivity(), ManageCarActivity.class);
                    startActivity(i3);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    getActivity().finish();
                    break;
                case R.id.llHistory:
                    Intent i4 = new Intent(getActivity(), HistoryActivity.class);
                    startActivity(i4);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    getActivity().finish();
                    break;
                case R.id.llSetting:
                    Intent i5 = new Intent(getActivity(), SettingActivity.class);
                    startActivity(i5);
                    break;
                case R.id.need_ride_ll:
                    RideShareApp.mHomeTabPos = 0;
                    RideShareApp.mRideTypeTabPos = 0;
                    Intent i6 = new Intent(getActivity(), RideTypeActivity.class);
                    startActivity(i6);
                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    getActivity().finish();
                    break;
                case R.id.offer_ride_ll:
                    if (AppUtils.isInternetAvailable(getContext())) {
                        Intent intent = new Intent(getActivity(), MyGroupSelectionActivity.class);
                        startActivity(intent);
                    } else {
                        MessageUtils.showNoInternetAvailable(getContext());
                    }
                    break;
                case R.id.llLogout:
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());

                    builder.setTitle(getResources().getString(R.string.logout))
                            .setMessage(getResources().getString(R.string.logout_message))
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        PrefUtils.putBoolean("islogin", false);

                                        PrefUtils.putString("loginwith", "");

                                        Intent intent = new Intent(getActivity(),
                                                SignUpActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        getActivity().finish();
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            getActivity().finishAffinity();
                                        }

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                    break;
            }
        } else {
            MessageUtils.showNoInternetAvailable(getActivity());
        }
    }
}