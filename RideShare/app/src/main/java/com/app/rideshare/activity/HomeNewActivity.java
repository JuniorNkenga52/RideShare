package com.app.rideshare.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.app.rideshare.R;
import com.app.rideshare.fragment.ExploreFragment;
import com.app.rideshare.fragment.HomeFragment;
import com.app.rideshare.fragment.MessagesFragment;
import com.app.rideshare.fragment.NotificationFragment;
import com.app.rideshare.fragment.ProfileFragment;
import com.app.rideshare.model.Rider;

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
                                selectedFragment = ExploreFragment.newInstance();
                                break;
                            case R.id.action_item2:
                                RideShareApp.mHomeTabPos = 2;
                                selectedFragment = MessagesFragment.newInstance();
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
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
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

        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }

    }
}
