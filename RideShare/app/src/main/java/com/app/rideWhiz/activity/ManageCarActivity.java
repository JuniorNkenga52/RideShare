package com.app.rideWhiz.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.api.ApiServiceModule;
import com.app.rideWhiz.api.RestApiInterface;
import com.app.rideWhiz.api.response.ManageCarResponce;
import com.app.rideWhiz.model.CarBrand;
import com.app.rideWhiz.model.CarMonth;
import com.app.rideWhiz.model.CarType;
import com.app.rideWhiz.model.CarYear;
import com.app.rideWhiz.model.ManageCar;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.CallbackCarType;
import com.app.rideWhiz.utils.CallbackYear;
import com.app.rideWhiz.utils.DialogUtils;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.rideWhiz.utils.Constants.manageCarsList;

public class ManageCarActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView image_colorplat, image_header, image_carplatcolor;

    private TextView txt_carplatno, text_carmonth, text_caryear, text_brand, text_cartype, text_transmissiontype;

    EditText edt_carmake, edt_licenseplate, edt_brand, edt_seatingcapacity, edt_modelofthecar;

    TextView txt_addcar;

    //RelativeLayout layout_back_arrow;

    //info pennel
    RelativeLayout layout_info_panel;

    TextView subtitle;

    //alert pennel
    RelativeLayout rlMainView;

    TextView tvTitle;

    private List<CarYear> year_list;
    private List<CarBrand> Brand_list;
    private List<CarMonth> Month_list;
    private List<CarType> list_cartype;



    String selectcolor, str_cartype = "", str_cartypeid = "", str_carmonth = "", str_caryear = "", str_carbrand = "",
            str_transmissiontype = "", str_carid = "", str_ispooling = "0", str_iscurrent, str_caricon;

    CustomProgressDialog mProgressDialog;

    private Dialog dialog;
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

        ArrayOfBrand();
        ArrayOfMonth();
        ArrayOfYear();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Cars");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ManageCarActivity.this, HomeNewActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
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


        image_colorplat = findViewById(R.id.image_colorplat);
        image_header = findViewById(R.id.image_header);
        image_carplatcolor = findViewById(R.id.image_carplatcolor);
        txt_carplatno = findViewById(R.id.txt_carplatno);
        text_carmonth = findViewById(R.id.text_carmonth);
        text_caryear = findViewById(R.id.text_caryear);
        text_brand = findViewById(R.id.text_brand);
        text_cartype = findViewById(R.id.text_cartype);
        text_transmissiontype = findViewById(R.id.text_transmissiontype);

        //layout_back_arrow=findViewById(R.id.layout_back_arrow);
        edt_carmake = findViewById(R.id.edt_carmake);
        edt_licenseplate = findViewById(R.id.edt_licenseplate);
        edt_brand = findViewById(R.id.edt_brand);
        edt_seatingcapacity = findViewById(R.id.edt_seatingcapacity);
        edt_modelofthecar = findViewById(R.id.edt_modelofthecar);
        txt_addcar = findViewById(R.id.txt_addcar);
        //layout_back_arrow=findViewById(R.id.layout_back_arrow);
        //subtitle=findViewById(R.id.subtitle);
        //tvTitle=findViewById(R.id.tvTitle);


        image_colorplat.setOnClickListener(this);
        text_cartype.setOnClickListener(this);
        text_carmonth.setOnClickListener(this);
        text_caryear.setOnClickListener(this);
        text_brand.setOnClickListener(this);
        text_transmissiontype.setOnClickListener(this);
        txt_addcar.setOnClickListener(this);

        AddTextChangeListener(edt_licenseplate, "address");

        if (PrefUtils.getBoolean("EditCar")) {
            if (manageCarsList != null && manageCarsList.size() > 0) {
                ManageCar managecardata = manageCarsList.get(PrefUtils.getInt("EditCarPosition"));

                txt_addcar.setText("EDIT CAR");
                str_carid = managecardata.getId();

                edt_carmake.setText("" + managecardata.getCar_make());
                edt_seatingcapacity.setText("" + managecardata.getSeating_capacity());
                edt_modelofthecar.setText("" + managecardata.getCar_model());
                edt_brand.setText("" + managecardata.getBrand());

                if (list_cartype != null && list_cartype.size() > 0) {
                    for (int i = 0; i < list_cartype.size(); i++) {
                        // PlanData packdata = Common.plandataitem.get(i);
                        CarType carTypedata = list_cartype.get(i);

                        if (carTypedata.getCabid().equals(managecardata.getCar_type())) {
                            text_cartype.setText(carTypedata.getCartype());
                            str_cartype = carTypedata.getCartype();
                            str_cartypeid = carTypedata.getCabid();
                            str_ispooling = carTypedata.getIs_pool();
                            str_caricon = carTypedata.getActive_side_icon();
                            carTypedata.setIsselect(true);
                            break;
                        }
                    }
                }

                if (Month_list != null && Month_list.size() > 0) {
                    for (int i = 0; i < Month_list.size(); i++) {
                        CarMonth carYeardata = Month_list.get(i);

                        if (carYeardata.getMonth().equals(managecardata.getCar_month())) {

                            text_carmonth.setText(carYeardata.getMonth());
                            str_carmonth = carYeardata.getMonth();
                            carYeardata.setSelected(true);

                            break;
                        }
                    }
                } else {
                    text_carmonth.setText(managecardata.getCar_month());
                    str_carmonth = managecardata.getCar_month();
                }


                if (year_list != null && year_list.size() > 0) {
                    for (int i = 0; i < year_list.size(); i++) {
                        CarYear carYeardata = year_list.get(i);

                        if (carYeardata.getYear().equals(managecardata.getCar_year())) {

                            text_caryear.setText(carYeardata.getYear());
                            str_caryear = carYeardata.getYear();
                            carYeardata.setSelected(true);

                            break;
                        }
                    }
                } else {
                    text_caryear.setText(managecardata.getCar_year());
                    str_caryear = managecardata.getCar_year();
                }


                if (Brand_list != null && Brand_list.size() > 0) {

                    for (int i = 0; i < Brand_list.size(); i++) {
                        CarBrand carBranddata = Brand_list.get(i);

                        if (carBranddata.getBrand().equals(managecardata.getBrand())) {

                            text_brand.setText(carBranddata.getBrand());
                            str_carbrand = carBranddata.getBrand();
                            carBranddata.setSelected(true);
                            break;
                        }
                    }
                } else {
                    text_brand.setText(managecardata.getBrand());
                    str_carbrand = managecardata.getBrand();
                }
            }
        }
    }

    public void AddTextChangeListener(final EditText edit, final String field) {
        edit.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                txt_carplatno.setText(s.toString());
            }
        });
    }

    public void ArrayOfMonth() {

        Month_list = new ArrayList<CarMonth>();

        Month_list.add(new CarMonth(getResources().getString(R.string.January), false));
        Month_list.add(new CarMonth(getResources().getString(R.string.February), false));
        Month_list.add(new CarMonth(getResources().getString(R.string.March), false));
        Month_list.add(new CarMonth(getResources().getString(R.string.April), false));
        Month_list.add(new CarMonth(getResources().getString(R.string.May), false));
        Month_list.add(new CarMonth(getResources().getString(R.string.June), false));
        Month_list.add(new CarMonth(getResources().getString(R.string.July), false));
        Month_list.add(new CarMonth(getResources().getString(R.string.August), false));
        Month_list.add(new CarMonth(getResources().getString(R.string.September), false));
        Month_list.add(new CarMonth(getResources().getString(R.string.October), false));
        Month_list.add(new CarMonth(getResources().getString(R.string.November), false));
        Month_list.add(new CarMonth(getResources().getString(R.string.December), false));
    }

    public void ArrayOfBrand() {

        Brand_list = new ArrayList<CarBrand>();

        Brand_list.add(new CarBrand(getResources().getString(R.string.Volkswagen), false));
        Brand_list.add(new CarBrand(getResources().getString(R.string.Audi), false));
        Brand_list.add(new CarBrand(getResources().getString(R.string.Mercedes), false));
        Brand_list.add(new CarBrand(getResources().getString(R.string.Bmw), false));
        Brand_list.add(new CarBrand(getResources().getString(R.string.Volvo), false));
        Brand_list.add(new CarBrand(getResources().getString(R.string.Toyota), false));
        Brand_list.add(new CarBrand(getResources().getString(R.string.Opel), false));
        Brand_list.add(new CarBrand(getResources().getString(R.string.Renault), false));
        Brand_list.add(new CarBrand(getResources().getString(R.string.Peugeot), false));
        Brand_list.add(new CarBrand(getResources().getString(R.string.Jeep), false));
        Brand_list.add(new CarBrand(getResources().getString(R.string.RangeRover), false));
        Brand_list.add(new CarBrand(getResources().getString(R.string.Chevrolet), false));
        Brand_list.add(new CarBrand(getResources().getString(R.string.Suzuki), false));
        Brand_list.add(new CarBrand(getResources().getString(R.string.Honda), false));
    }

    public void ArrayOfYear() {

        year_list = new ArrayList<CarYear>();

        for (int i = 0; i < 25; i++) {
            String s = "-" + i;
            //year_list.add("" + getPreviousYear(s));
            year_list.add(new CarYear("" + getPreviousYear(s), false));
        }
    }

    private static int getPreviousYear(String value) {
        int v = Integer.parseInt(value);
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, v);
        return prevYear.get(Calendar.YEAR);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.image_colorplat) {
           /* Intent i = new Intent(context, ColorPickerActivity.class);
            startActivity(i);*/
        } else if (v.getId() == R.id.text_cartype) {
            str_cartype = "";
            str_cartypeid = "";

            dialog = new DialogUtils(this).buildDialogCartype(new CallbackCarType() {
                @Override
                public void onSuccess(Dialog dialog, String cartype, String carid, String caricon, String ispooling) {
                    str_cartype = cartype;
                    str_cartypeid = carid;
                    text_cartype.setText("" + cartype);
                    str_caricon = caricon;
                    str_ispooling = ispooling;

                    //SharedPreferences.Editor sid = sPref.edit();
                    PrefUtils.putString("cartypeid", carid);
                    PrefUtils.putString("cartypename", str_cartype);
                    PrefUtils.putString("car_icon", caricon);
                    //sid.commit();

                    dialog.dismiss();

                }

                @Override
                public void onCancel(Dialog dialog) {
                    dialog.dismiss();
                    str_cartype = "";
                    str_cartypeid = "";
                    text_cartype.setText("");
                }

            }, "Transmission type", Brand_list);

            dialog.show();
        } else if (v.getId() == R.id.text_carmonth) {
            str_carmonth = "";
            Dialog dialog = new DialogUtils(this).buildDialogMonth(new CallbackYear() {
                @Override
                public void onSuccess(Dialog dialog, String year) {
                    str_carmonth = year;
                    text_carmonth.setText("" + year);
                    dialog.dismiss();
                }

                @Override
                public void onCancel(Dialog dialog) {
                    dialog.dismiss();
                    str_carmonth = "";
                    text_carmonth.setText("");
                }

            }, "Selected Month", Month_list);

            dialog.show();

        } else if (v.getId() == R.id.text_caryear) {
            str_caryear = "";
            dialog = new DialogUtils(this).buildDialogYear(new CallbackYear() {
                @Override
                public void onSuccess(Dialog dialog, String year) {
                    str_caryear = year;
                    text_caryear.setText("" + year);
                    dialog.dismiss();
                }

                @Override
                public void onCancel(Dialog dialog) {
                    dialog.dismiss();
                    str_caryear = "";
                    text_caryear.setText("");
                }

            }, "Selected Year", year_list);

            dialog.show();

        } else if (v.getId() == R.id.text_brand) {

            str_carbrand = "";
            dialog = new DialogUtils(this).buildDialogBrand(new CallbackYear() {
                @Override
                public void onSuccess(Dialog dialog, String brand) {
                    str_carbrand = brand;
                    text_brand.setText("" + brand);
                    dialog.dismiss();
                }

                @Override
                public void onCancel(Dialog dialog) {
                    dialog.dismiss();
                    str_carbrand = "";
                    text_brand.setText("");
                }

            }, "Selected Brand", Brand_list);

            dialog.show();

        } else if (v.getId() == R.id.txt_addcar) {
            if (edt_carmake.getText().toString().trim().equals("")) {
                showMkErrorMessage(this, getResources().getString(R.string.entercarmake));
                edt_carmake.requestFocus();
                return;
            } /*else if (str_carmonth.equals("")) {
                showMkErrorMessage(this, getResources().getString(R.string.entercarMonth));
            } else if (str_caryear.equals("")) {
                showMkErrorMessage(this, getResources().getString(R.string.entercaryear));
            }
            else if (edt_brand.getText().toString().equals("")) {
                showMKPanelError(this, getResources().getString(R.string.entercarbrand), rlMainView, tvTitle);
                edt_brand.requestFocus();
            } else if (str_cartype.equals("")) {
                showMkErrorMessage(this, getResources().getString(R.string.entercartype));
            }*/
            else if (edt_modelofthecar.getText().toString().trim().equals("")) {
                showMkErrorMessage(this, getResources().getString(R.string.entermodelofthecar));
                edt_modelofthecar.requestFocus();
                return;
            }else if (edt_seatingcapacity.getText().toString().trim().equals("")) {
                showMkErrorMessage(this, getResources().getString(R.string.entercarseatingcapacity));
                edt_seatingcapacity.requestFocus();
                return;
            } else {

                str_carbrand = edt_brand.getText().toString();

                if (PrefUtils.getBoolean("EditCar")) {

                    if (AppUtils.isInternetAvailable(this)) {

                        manageCar(PrefUtils.getUserInfo().getmUserId(), edt_carmake.getText().toString().trim(), str_carmonth, str_caryear, edt_licenseplate.getText().toString().trim(), str_carbrand, str_cartypeid
                                , edt_seatingcapacity.getText().toString().trim(), edt_modelofthecar.getText().toString().trim());

                    }

                } else {
                    if (AppUtils.isInternetAvailable(this)) {

                        manageCar(PrefUtils.getUserInfo().getmUserId(), edt_carmake.getText().toString().trim(), str_carmonth, str_caryear, edt_licenseplate.getText().toString().trim(), str_carbrand, "Honda"
                                , edt_seatingcapacity.getText().toString().trim(), edt_modelofthecar.getText().toString().trim());
                    }
                }
            }

        }
    }


    public static void ValidationGone(final Activity activity, final RelativeLayout rlMainView, EditText edt_reg_username) {
        edt_reg_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //  Log.d("charSequence", "charSequence = " + charSequence.length() + "==" + rlMainView.getVisibility() + "==" + View.VISIBLE);
                if (charSequence.length() > 0 && rlMainView.getVisibility() == View.VISIBLE) {
                    if (!activity.isFinishing()) {
                        TranslateAnimation slideUp = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -100);
                        slideUp.setDuration(10);
                        slideUp.setFillAfter(true);
                        rlMainView.startAnimation(slideUp);
                        slideUp.setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                rlMainView.setVisibility(View.GONE);
                            }
                        });

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public static void showMKPanelError(final Activity act, String message, final RelativeLayout rlMainView, TextView tvTitle) {
        if (!act.isFinishing() && (rlMainView.getVisibility() == View.GONE)) {
            if ((rlMainView.getVisibility() == View.GONE)) {
                rlMainView.setVisibility(View.VISIBLE);
            }

            rlMainView.setBackgroundResource(R.color.dialog_error_color);
            tvTitle.setText(message);
            Animation slideUpAnimation = AnimationUtils.loadAnimation(act.getApplicationContext(), R.anim.slide_up_map);
            rlMainView.startAnimation(slideUpAnimation);
            //rlMainView.clearAnimation();
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

        ApiServiceModule.createService(RestApiInterface.class,context).manageCar(m_user_id, m_car_make, m_car_month, m_car_year, m_license_plate, m_brand, m_car_type, m_seating_capacity, m_car_model).enqueue(new Callback<ManageCarResponce>() {
            @Override
            public void onResponse(Call<ManageCarResponce> call, Response<ManageCarResponce> response) {
                if (response.body() != null || response.body().getResult()!=null) {
                    if (response.body().getResult()!=null) {
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
        Intent i = new Intent(ManageCarActivity.this, HomeNewActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
