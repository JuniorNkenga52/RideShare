package com.app.rideWhiz.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app.rideWhiz.R;

public class DriverPrivileges extends AppCompatActivity {

    private CheckBox item_rider_req, item_disable, item_ap_priv;
    private TextView item_vehicle_model, item_vehicle_type, item_vehicle_passengers;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rider_priviledges);

        context=this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.admin_fun_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Driver Privileges");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        for(int i = 0; i < toolbar.getChildCount(); i++){
            View view = toolbar.getChildAt(i);
            if(view instanceof TextView){
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
                if(tv.getText().equals(toolbar.getTitle())){
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }

        item_vehicle_model= (TextView) findViewById(R.id.item_vehicle_model);
        item_vehicle_type= (TextView) findViewById(R.id.item_vehicle_type);
        item_vehicle_passengers= (TextView) findViewById(R.id.item_vehicle_passengers);


    }
}
