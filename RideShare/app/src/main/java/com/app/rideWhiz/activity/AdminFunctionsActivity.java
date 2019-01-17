package com.app.rideWhiz.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.adapter.AdminFunctionsAdapter;
import com.app.rideWhiz.adapter.ChooseGroupAdapter;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.response.GroupListResponce;
import com.app.rideWhiz.api.response.MyGroupsResponce;
import com.app.rideWhiz.model.ChooseGroupModel;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminFunctionsActivity extends AppCompatActivity {

    private ListView list_riders;
    private AdminFunctionsAdapter adminFunctionsAdapter;
    private Context context;

    private ArrayList<ChooseGroupModel> mygroupdata = new ArrayList<>();
    private Spinner admin_choose_group;

    private ChooseGroupModel chooseGroupModel;
    private CustomProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_functions);

        context = this;
        PrefUtils.initPreference(this);
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

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
                if (tv.getText().equals(toolbar.getTitle())) {
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }

        list_riders = (ListView) findViewById(R.id.list_riders);
        mProgressDialog = new CustomProgressDialog(this);

        my_group_data(PrefUtils.getUserInfo().getmUserId());

        admin_choose_group = (Spinner) findViewById(R.id.admin_choose_group);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void my_group_data(final String user_id) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class,context).mygroups(user_id).enqueue(new Callback<GroupListResponce>() {
            @Override
            public void onResponse(Call<GroupListResponce> call, Response<GroupListResponce> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mygroupdata.clear();
                    if (response.body().getResult().size() != 0) {
                        for (int i = 0; i < response.body().getResult().size(); i++) {
                            int group_id = response.body().getResult().get(i).getId();
                            String name = response.body().getResult().get(i).getGroup_name();
                            chooseGroupModel = new ChooseGroupModel(group_id, name);

                            mygroupdata.add(chooseGroupModel);
                        }
                        bindSpinner(admin_choose_group, mygroupdata.get(0).getGroup_name());
                    }
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<GroupListResponce> call, Throwable t) {
                MessageUtils.showFailureMessage(AdminFunctionsActivity.this, "Problem in Retrieving data");
                mProgressDialog.cancel();
            }
        });
        {

        }
    }

    private void group_data(String group_id, String user_id) {
        mProgressDialog.show();
        PrefUtils.listAdminData = new ArrayList<>();
        ApiServiceModule.createService(RestApiInterface.class,context).groupusers(group_id, user_id).enqueue(new Callback<MyGroupsResponce>() {
            @Override
            public void onResponse(Call<MyGroupsResponce> call, Response<MyGroupsResponce> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResult().size() > 0) {

                    PrefUtils.listAdminData.addAll(response.body().getResult());

                    adminFunctionsAdapter = new AdminFunctionsAdapter(context, response.body().getResult());
                    list_riders.setAdapter(adminFunctionsAdapter);
                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<MyGroupsResponce> call, Throwable t) {
                MessageUtils.showFailureMessage(AdminFunctionsActivity.this, "Problem in Retrieving data");
                mProgressDialog.cancel();
            }

        });
    }

    public void bindSpinner(final Spinner spinner, final String value) {
        spinner.setAdapter(new ChooseGroupAdapter(this, mygroupdata));
        spinner.post(new Runnable() {
            @Override
            public void run() {
                spinner.setSelection(mygroupdata.indexOf(value));
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String.valueOf(mygroupdata.get(position).getId())
                group_data(String.valueOf(mygroupdata.get(position).getId()), PrefUtils.getUserInfo().getmUserId());
                list_riders.invalidateViews();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
