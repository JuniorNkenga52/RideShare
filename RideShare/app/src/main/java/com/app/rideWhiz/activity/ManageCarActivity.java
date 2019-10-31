package com.app.rideWhiz.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.response.ManageCarResponce;
import com.app.rideWhiz.model.ManageCar;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.rideWhiz.utils.Constants.manageCarsList;

public class ManageCarActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edt_carmake, edt_seatingcapacity, edt_modelofthecar;
    TextView txt_addcar;
    //alert pennel
    RelativeLayout rlMainView;
    TextView tvTitle;
    String str_cartypeid = "", str_carmonth = "", str_caryear = "", str_carbrand = "",
            str_carid = "";
    CustomProgressDialog mProgressDialog;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_car);

        context = this;
        mProgressDialog = new CustomProgressDialog(context);
        PrefUtils.initPreference(context);
        Initdata();
    }

    private void Initdata() {
        if (AppUtils.isInternetAvailable(this)) {
            //requestCartype();
        } else {
            //Common.showInternetInfo(this, "Network is not available");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Cars");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RideShareApp.mHomeTabPos != 0) {
                    RideShareApp.mHomeTabPos = 4;
                    Intent i = new Intent(ManageCarActivity.this, HomeNewActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    RideShareApp.mRideTypeTabPos = 4;

                    Intent i = new Intent(ManageCarActivity.this, RideTypeActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            }
        });

        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                Typeface titleFont = Typeface.
                        createFromAsset(getAssets(), "OpenSans-Regular.ttf");
                if (tv.getText().equals(toolbar.getTitle())) {
                    tv.setTypeface(titleFont);
                    break;
                }
            }
        }

        rlMainView = findViewById(R.id.rlMainView);
        tvTitle = findViewById(R.id.tvTitle);

        //layout_back_arrow=findViewById(R.id.layout_back_arrow);
        edt_carmake = findViewById(R.id.edt_carmake);
        edt_seatingcapacity = findViewById(R.id.edt_seatingcapacity);
        edt_modelofthecar = findViewById(R.id.edt_modelofthecar);
        txt_addcar = findViewById(R.id.txt_addcar);
        txt_addcar.setOnClickListener(this);


        if (PrefUtils.getBoolean("EditCar")) {
            if (manageCarsList != null && manageCarsList.size() > 0) {
                ManageCar managecardata = manageCarsList.get(PrefUtils.getInt("EditCarPosition"));

                txt_addcar.setText("EDIT CAR");
                str_carid = managecardata.getId();

                edt_carmake.setText("" + managecardata.getCar_make());
                edt_seatingcapacity.setText("" + managecardata.getSeating_capacity());
                edt_modelofthecar.setText("" + managecardata.getCar_model());
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_colorplat) {
           /* Intent i = new Intent(context, ColorPickerActivity.class);
            startActivity(i);*/
        } else if (v.getId() == R.id.txt_addcar) {
            if (edt_carmake.getText().toString().trim().equals("")) {
                showMkErrorMessage(this, getResources().getString(R.string.entercarmake));
                edt_carmake.requestFocus();
                return;
            } else if (edt_modelofthecar.getText().toString().trim().equals("")) {
                showMkErrorMessage(this, getResources().getString(R.string.entermodelofthecar));
                edt_modelofthecar.requestFocus();
                return;
            } else if (edt_seatingcapacity.getText().toString().trim().equals("")) {
                showMkErrorMessage(this, getResources().getString(R.string.entercarseatingcapacity));
                edt_seatingcapacity.requestFocus();
                return;
            } else {


                if (PrefUtils.getBoolean("EditCar")) {

                    if (AppUtils.isInternetAvailable(this)) {

                        manageCar(PrefUtils.getUserInfo().getmUserId(), edt_carmake.getText().toString().trim(), str_carmonth, str_caryear, "1234", str_carbrand, str_cartypeid
                                , edt_seatingcapacity.getText().toString().trim(), edt_modelofthecar.getText().toString().trim());

                    }

                } else {
                    if (AppUtils.isInternetAvailable(this)) {

                        manageCar(PrefUtils.getUserInfo().getmUserId(), edt_carmake.getText().toString().trim(), str_carmonth, str_caryear, "1234", str_carbrand, "Honda"
                                , edt_seatingcapacity.getText().toString().trim(), edt_modelofthecar.getText().toString().trim());
                    }
                }
            }

        }
    }

    public static void showMkErrorMessage(final Activity act, String message) {
        if (!act.isFinishing()) {

            Animation slideUpAnimation;

            final Dialog MKInfoPanelDialog = new Dialog(act, android.R.style.Theme_Translucent_NoTitleBar);

            MKInfoPanelDialog.setContentView(R.layout.mkinfopanel);

            final RelativeLayout layout_info_panel = (RelativeLayout) MKInfoPanelDialog.findViewById(R.id.layout_info_panel);

            layout_info_panel.setVisibility(View.VISIBLE);

            MKInfoPanelDialog.show();
            slideUpAnimation = AnimationUtils.loadAnimation(act.getApplicationContext(),
                    R.anim.slide_up_map);

            layout_info_panel.startAnimation(slideUpAnimation);

            layout_info_panel.setBackgroundResource(R.color.dialog_error_color);

            TextView subtitle = (TextView) MKInfoPanelDialog.findViewById(R.id.subtitle);
            subtitle.setText(message);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (MKInfoPanelDialog.isShowing() && !act.isFinishing()) {
                            MKInfoPanelDialog.cancel();
                            layout_info_panel.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 2000);

        }
    }

    private void manageCar(String m_user_id, String m_car_make, String m_car_month, String m_car_year, String m_license_plate, String m_brand, String m_car_type, String m_seating_capacity, String m_car_model) {
        mProgressDialog.show();

        ApiServiceModule.createService(RestApiInterface.class, context).manageCar(m_user_id, m_car_make, m_car_month, m_car_year, m_license_plate, m_brand, m_car_type, m_seating_capacity, m_car_model).enqueue(new Callback<ManageCarResponce>() {
            @Override
            public void onResponse(Call<ManageCarResponce> call, Response<ManageCarResponce> response) {
                if (response.body() != null || response.body().getResult() != null) {
                    if (response.body().getResult() != null) {
                        MessageUtils.showSuccessMessage(context, response.body().getMessage());
                    }
                }
                //finish();
                Intent i = new Intent(ManageCarActivity.this, HomeNewActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<ManageCarResponce> call, Throwable t) {
                MessageUtils.showFailureMessage(context, "Problem occurs while submitting");
                mProgressDialog.cancel();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (RideShareApp.mHomeTabPos != 0) {
            RideShareApp.mHomeTabPos = 4;
            Intent i = new Intent(ManageCarActivity.this, HomeNewActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            RideShareApp.mRideTypeTabPos = 4;

            Intent i = new Intent(ManageCarActivity.this, RideTypeActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }
    }
}
