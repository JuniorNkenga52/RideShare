package com.app.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.fragment.HistoryFragment;
import com.app.rideshare.fragment.HomeFragment;
import com.app.rideshare.listner.OnBackPressedListener;
import com.app.rideshare.model.Rider;
import com.app.rideshare.utils.PrefUtils;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import it.sephiroth.android.library.easing.Circ;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private ArrayList<Rider> mlist;
    private ImageView mPopupIv;
    PopupWindow popupWindow;
    private GoogleApiClient mGoogleApiClient;

    public static OnBackPressedListener onBackPressed;
    FragmentManager mFragmentManager;

    private TextView mFnameTv;
    private TextView mEmailTv;
    private ImageView mEditProfileIv;
    CircularImageView mProfileIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ride Share");

        PrefUtils.initPreference(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mFnameTv = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header_fname_tv);
        mEmailTv = (TextView) navigationView.getHeaderView(0).findViewById(R.id.header_email_tv);
        mProfileIv=(CircularImageView)navigationView.getHeaderView(0).findViewById(R.id.nav_profile);



        mEditProfileIv = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.edit_profile);
        mEditProfileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        mFnameTv.setText(PrefUtils.getUserInfo().getmFirstName());
        mEmailTv.setText(PrefUtils.getUserInfo().getmEmail());
        if(!PrefUtils.getUserInfo().getProfile_image().equals("")){
            Picasso.with(this).load(PrefUtils.getUserInfo().getProfile_image()).into(mProfileIv);
        }else {
            Picasso.with(this).load(R.mipmap.ic_launcher).into(mProfileIv);
        }




        mlist = (ArrayList<Rider>) getIntent().getSerializableExtra("list");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("226419699435-664j5a9sfct42n6icr0usefpkhrlld1a.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        switchFragment(HomeFragment.newInstance(mlist), "HOMEFRAGEMNT");


        mPopupIv = (ImageView) toolbar.findViewById(R.id.popup_iv);


        mPopupIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    showPopup(v);
                }
            }
        });
        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerSlide(View arg0, float arg1)
            {
                if(PrefUtils.getUserInfo()!=null)
                {
                    if(!PrefUtils.getUserInfo().getProfile_image().equals("")){
                        Picasso.with(HomeActivity.this).load(PrefUtils.getUserInfo().getProfile_image()).into(mProfileIv);
                    }else {
                        Picasso.with(HomeActivity.this).load(R.mipmap.ic_launcher).into(mProfileIv);
                    }
                    mFnameTv.setText(PrefUtils.getUserInfo().getmFirstName());
                    mEmailTv.setText(PrefUtils.getUserInfo().getmEmail());
                }

            }
            @Override public void onDrawerStateChanged(int arg0) {}
            @Override public void onDrawerOpened(View arg0) {}
            @Override public void onDrawerClosed(View arg0) {}
        });
    }
    public void showPopup(View v) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.popup_layout, null);

        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setOutsideTouchable(true);

        final RadioButton mFriendRb = (RadioButton) popupView.findViewById(R.id.fridnds_rb);
        final RadioButton mAllRb = (RadioButton) popupView.findViewById(R.id.all_rb);

        if (PrefUtils.getBoolean("isFriends")) {
            mFriendRb.setChecked(true);
        } else if (PrefUtils.getBoolean("isAll")) {
            mAllRb.setChecked(true);
        }

        mFriendRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFriendRb.setChecked(true);
                    mAllRb.setChecked(false);
                    PrefUtils.putBoolean("isFriends", true);
                    PrefUtils.putBoolean("isAll", false);
                } else {
                    mFriendRb.setChecked(false);
                    mAllRb.setChecked(true);
                    PrefUtils.putBoolean("isFriends", false);
                    PrefUtils.putBoolean("isAll", true);
                }
                popupWindow.dismiss();

            }
        });
        mAllRb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mFriendRb.setChecked(false);
                    mAllRb.setChecked(true);
                    PrefUtils.putBoolean("isAll", true);
                    PrefUtils.putBoolean("isFriends", false);
                } else {
                    mFriendRb.setChecked(true);
                    mAllRb.setChecked(false);
                    PrefUtils.putBoolean("isAll", false);
                    PrefUtils.putBoolean("isFriends", true);
                }
                popupWindow.dismiss();
            }
        });

        popupWindow.showAsDropDown(v);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (onBackPressed != null) {
                onBackPressed.doBack();
            } else {
                super.onBackPressed();
            }
        }
    }

    protected void switchFragment(Fragment mFragment, String classname) {
        if (mFragment != null) {

            mFragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            transaction.replace(R.id.content_main, mFragment, classname);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(classname);
            transaction.commit();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            HomeFragment myFragment = (HomeFragment) mFragmentManager.findFragmentByTag("HOMEFRAGEMNT");

            if (myFragment != null && myFragment.isVisible()) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            } else {
                switchFragment(HomeFragment.newInstance(mlist), "HOMEFRAGEMNT");
            }

        } else if (id == R.id.nav_history) {
            HistoryFragment myFragment = (HistoryFragment) mFragmentManager.findFragmentByTag("HISTORYFRAGMENT");

            if (myFragment != null && myFragment.isVisible()) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            } else {
                switchFragment(HistoryFragment.newInstance(), "HISTORYFRAGMENT");
            }

        } else if (id == R.id.nav_logout) {
            LoginManager.getInstance().logOut();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.d("status", "" + status);
                        }
                    });
            PrefUtils.remove(PrefUtils.PREF_USER_INFO);
            PrefUtils.remove("islogin");
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        onBackPressed = onBackPressedListener;
    }
}
