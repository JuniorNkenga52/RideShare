package com.app.rideshare.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.adapter.AdminFuncitonsAdapter;
import com.app.rideshare.model.AdminFunctions;

import java.util.ArrayList;

public class Admin_FunctionsActivity extends AppCompatActivity {

    TextView admin_fname, admin_lname, admin_urole, admin_aprove_req, admin_disable, admin_aprove_priviledge;
    ListView list_riders;
    AdminFuncitonsAdapter adminFuncitonsAdapter;
    Context context;
    ArrayList<AdminFunctions> listdata;
    AdminFunctions adminFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_functions);

        context = this;

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
        admin_fname = (TextView) findViewById(R.id.admin_fname);
        admin_lname = (TextView) findViewById(R.id.admin_lname);
        admin_urole = (TextView) findViewById(R.id.admin_urole);
        admin_aprove_req = (TextView) findViewById(R.id.admin_aprove_req);
        admin_disable = (TextView) findViewById(R.id.admin_disable);
        admin_aprove_priviledge = (TextView) findViewById(R.id.admin_aprove_priviledge);
        list_riders = (ListView) findViewById(R.id.list_riders);

        listdata = new ArrayList<>();
        adminFunctions = new AdminFunctions("Pattrik Harris", "Richardson", "Rider", 0, 0, 0);
        listdata.add(adminFunctions);
        adminFuncitonsAdapter = new AdminFuncitonsAdapter(context, listdata);
        list_riders.setAdapter(adminFuncitonsAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
