package com.app.rideWhiz.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.rideWhiz.R;
import com.app.rideWhiz.chat.MyService;
import com.app.rideWhiz.chat.MyXMPP;
import com.app.rideWhiz.fragment.ExploreFragment;
import com.app.rideWhiz.fragment.HomeFragment;
import com.app.rideWhiz.fragment.MessagesFragment;
import com.app.rideWhiz.fragment.NotificationFragment;
import com.app.rideWhiz.fragment.ProfileFragment;
import com.app.rideWhiz.model.Rider;
import com.app.rideWhiz.service.LocationProvider;
import com.app.rideWhiz.utils.Constants;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;


public class HomeNewActivity extends AppCompatActivity implements LocationProvider.LocationCallback {


    public static String currentChat = "";
    private static long back_pressed;
    public BottomNavigationView bottomNavigationView;
    Context context;
    Activity activity;
    ArrayList<Rider> mlist = new ArrayList<>();
    LocationProvider mLocationProvider;
    CustomProgressDialog mProgressDialog;
    String userType;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);

        context = this;
        activity = this;

        mProgressDialog = new CustomProgressDialog(activity);
        mlist = (ArrayList<Rider>) getIntent().getSerializableExtra("list");

        mLocationProvider = new LocationProvider(this, this);

        bottomNavigationView = findViewById(R.id.navigation);
        if (PrefUtils.getString("isBlank").equals("true")) {
            if (RideShareApp.mHomeTabPos == 4) {
                RideShareApp.mHomeTabPos = 4;
            } else {
                RideShareApp.mHomeTabPos = 1;
            }
        }

        userID = PrefUtils.getUserInfo().getmUserId();
        userType = RideShareApp.getmUserType();

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                RideShareApp.mHomeTabPos = 0;
                                selectedFragment = HomeFragment.newInstance(mlist);
                                break;
                            case R.id.action_item1:
                                RideShareApp.mHomeTabPos = 1;
                                if (PrefUtils.getString("isBlank").equals("true")) {
                                    selectedFragment = GroupSelectionFragment.newInstance();
                                } else {
                                    selectedFragment = ExploreFragment.newInstance();
                                }
                                break;
                            case R.id.action_item2:
                                RideShareApp.mHomeTabPos = 2;
                                selectedFragment = MessagesFragment.newInstance();
                                //startActivity(new Intent(context, WebSocketActivity.class));
                                break;
                            case R.id.action_item3:
                                RideShareApp.mHomeTabPos = 3;
                                selectedFragment = NotificationFragment.newInstance();
                                break;
                            case R.id.action_item4:
                                RideShareApp.mHomeTabPos = 4;
                                selectedFragment = ProfileFragment.newInstance();
                                break;
                        }

                        //if (RideShareApp.mHomeTabPos != 2) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                        //}
                        return true;
                    }
                });

        if (RideShareApp.mHomeTabPos == 0)
            bottomNavigationView.setSelectedItemId(R.id.action_home);
        else if (RideShareApp.mHomeTabPos == 1)
            bottomNavigationView.setSelectedItemId(R.id.action_item1);
        else if (RideShareApp.mHomeTabPos == 2)
            bottomNavigationView.setSelectedItemId(R.id.action_item2);
        else if (RideShareApp.mHomeTabPos == 3)
            bottomNavigationView.setSelectedItemId(R.id.action_item3);
        else if (RideShareApp.mHomeTabPos == 4)
            bottomNavigationView.setSelectedItemId(R.id.action_item4);


    }

    @Override
    public void onBackPressed() {

        if (PrefUtils.getString("isBlank").equals("true")) {
            RideShareApp.mHomeTabPos = 0;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            System.exit(0);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1 || RideShareApp.mHomeTabPos != 0) {
                bottomNavigationView.setSelectedItemId(R.id.action_home);
                //getSupportFragmentManager().popBackStack();
                int count = getSupportFragmentManager().getBackStackEntryCount();
                for (int i = 0; i < count; ++i) {
                    getSupportFragmentManager().popBackStack();
                }
                Fragment selectedFragment = HomeFragment.newInstance(mlist);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
            } else {

                Intent i = new Intent(this, RideTypeActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
            RideShareApp.mHomeTabPos = 0;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                MyService.xmpp = MyXMPP.getInstance(context, Constants.intentKey.jabberPrefix + PrefUtils.getUserInfo().getmUserId());
                MyService.xmpp.connect("onCreate");
            }
        }, 1000);

        if (RideShareApp.mLocation == null) {
            mLocationProvider.connect(activity);
        }
    }

    @Override
    public void handleNewLocation(Location location) {

    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        MyXMPP.destroy_connect();
        Intent intent = new Intent(activity, MyService.class);
        stopService(intent);
    }*/
}