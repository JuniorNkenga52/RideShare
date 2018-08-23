package com.app.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.app.rideshare.R;
import com.app.rideshare.fragment.ExploreFragment;
import com.app.rideshare.fragment.HomeFragment;
import com.app.rideshare.fragment.NotificationFragment;
import com.app.rideshare.fragment.ProfileFragment;
import com.app.rideshare.model.Rider;
import com.app.rideshare.utils.PrefUtils;

import java.util.ArrayList;


public class HomeNewActivity extends AppCompatActivity {


    Context context;
    private static long back_pressed;
    public BottomNavigationView bottomNavigationView;

    public static String currentChat = "";
    ArrayList<Rider> mlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_new);

        context = this;

        mlist = (ArrayList<Rider>) getIntent().getSerializableExtra("list");


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        if (PrefUtils.getString("isBlank").equals("true")) {
            if (RideShareApp.mHomeTabPos == 4) {
                RideShareApp.mHomeTabPos = 4;
            } else {
                RideShareApp.mHomeTabPos = 1;
            }
        }

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
                                //selectedFragment = MessagesFragment.newInstance();
                                startActivity(new Intent(context, WebSocketActivity.class));
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

                        if (RideShareApp.mHomeTabPos != 2) {
                            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.frame_layout, selectedFragment);
                            transaction.commit();
                        }
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

        /*if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            RideShareApp.mHomeTabPos = 0;
            Intent i = new Intent(this, RideTypeActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            FragmentManager fm = getFragmentManager(); // or 'getSupportFragmentManager();'
            int count = fm.getBackStackEntryCount();
            for(int i = 0; i < count; ++i) {
                fm.popBackStack();
            }
            MessageUtils.showFailureMessage(getBaseContext(), "Press once again to exit!");
            back_pressed = System.currentTimeMillis();
        }*/
        if (PrefUtils.getString("isBlank").equals("true")) {
            RideShareApp.mHomeTabPos = 0;
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            System.exit(0);
        } else {
            RideShareApp.mHomeTabPos = 0;
            Intent i = new Intent(this, RideTypeActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

    }
}