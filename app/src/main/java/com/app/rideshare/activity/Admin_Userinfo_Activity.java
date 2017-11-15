package com.app.rideshare.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.utils.PrefUtils;

public class Admin_Userinfo_Activity extends AppCompatActivity {

    private TextView item_user_name_tv, item_last_name, item_role;
    private CheckBox  item_user_req, item_disable, item_ap_priv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_userinfo);

        Toolbar toolbar = (Toolbar) findViewById(R.id.admin_fun_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin Functions");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        PrefUtils.initPreference(this);
        item_user_name_tv = (TextView) findViewById(R.id.item_user_name_tv);
        item_last_name = (TextView) findViewById(R.id.item_last_name);
        item_role = (TextView) findViewById(R.id.item_role);
        item_user_req = (CheckBox) findViewById(R.id.item_user_req);
        item_disable = (CheckBox) findViewById(R.id.item_disable);
        item_ap_priv = (CheckBox) findViewById(R.id.item_ap_priv);

        item_user_name_tv.setText(PrefUtils.getAdminInfo().getU_firstname());
        item_last_name.setText(PrefUtils.getAdminInfo().getU_lastname());
        if(PrefUtils.getAdminInfo().getIs_rider().equals("1")){
            item_role.setText("Rider");
        }

        /*item_user_req.setText(PrefUtils.getAdminInfo().getIs_rider());
        item_disable.setText(PrefUtils.getAdminInfo().getIs_rider());
        item_ap_priv.setText(PrefUtils.getAdminInfo().getIs_rider());*/
    }
}
