package com.app.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.MyGroupsResponce;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.view.CustomProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserinfoActivity extends AppCompatActivity {

    private TextView item_user_name_tv;
    private TextView item_last_name;
    private TextView item_role;

    private CheckBox item_driver_req;
    private CheckBox item_disable;
    private CheckBox item_ap_priv;

    private Button item_btn_ad_submit;
    private CustomProgressDialog mProgressDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_userinfo);

        context=this;
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

        PrefUtils.initPreference(this);
        Intent intent=getIntent();
        final int pos= 0;

        mProgressDialog = new CustomProgressDialog(this);
        item_user_name_tv = (TextView) findViewById(R.id.item_user_name_tv);
        item_last_name = (TextView) findViewById(R.id.item_last_name);
        item_role = (TextView) findViewById(R.id.item_role);
        item_driver_req = (CheckBox) findViewById(R.id.item_driver_req);
        item_disable = (CheckBox) findViewById(R.id.item_disable);
        item_ap_priv = (CheckBox) findViewById(R.id.item_ap_priv);
        item_btn_ad_submit = (Button) findViewById(R.id.item_btn_ad_submit);

        item_user_name_tv.setText(PrefUtils.getAdminData().get(pos).getU_firstname());
        item_last_name.setText(PrefUtils.getAdminData().get(pos).getU_lastname());

        if (PrefUtils.getAdminData().get(pos).getIs_driver().equals("1")) {
            item_driver_req.setChecked(true);
        }
        if (PrefUtils.getAdminData().get(pos).getU_status().equals("1")) {
            item_disable.setChecked(true);
        }

        if (PrefUtils.getAdminData().get(pos).getIs_rider().equals("1")) {
            item_role.setText("Rider");
        }
        item_btn_ad_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dreq = "0", disable = "0", ad_priv = "0";
                if (item_driver_req.isChecked()) {
                    dreq = "1";
                }
                if (item_disable.isChecked()) {
                    disable = "1";
                }
                if (item_ap_priv.isChecked()) {
                    ad_priv = "1";
                }
                update_admin_info(PrefUtils.getUserInfo().getmUserId(),dreq , disable,PrefUtils.getAdminData().get(pos).getGroup_id(),ad_priv);
            }
        });
    }

    private void update_admin_info(String user_id, String driver_request, String user_disable, String group_id, String ad_priv_type) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).updateAdminFunction(user_id, driver_request, user_disable,group_id,ad_priv_type).enqueue(new Callback<MyGroupsResponce>() {
            @Override
            public void onResponse(Call<MyGroupsResponce> call, Response<MyGroupsResponce> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult().size() > 0) {
                    ToastUtils.showShort(AdminUserinfoActivity.this, response.body().getMessage());
                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<MyGroupsResponce> call, Throwable t) {
                ToastUtils.showShort(AdminUserinfoActivity.this, "Problem in Retrieving data");
                mProgressDialog.cancel();
            }

        });
    }
}
