package com.app.rideshare.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.adapter.CarBrandAdapter;
import com.app.rideshare.adapter.CarMonthAdapter;
import com.app.rideshare.adapter.CarYearAdapter;
import com.app.rideshare.adapter.CartypeAdapter;
import com.app.rideshare.model.CarBrand;
import com.app.rideshare.model.CarMonth;
import com.app.rideshare.model.CarType;
import com.app.rideshare.model.CarYear;

import java.text.SimpleDateFormat;
import java.util.List;

public class DialogUtils {

    private Activity activity;
    private DatePickerDialog toDatePickerDialog;
    private SimpleDateFormat dateFormatter;
    //SharedPreferences sPref;
    String issue;

    String str_time = "";
    String str_assigntime = "";
    String csv_type = "";
    String csv_typename = "";

    //float discountAmount, discountAmountForServer;
    public DialogUtils(Activity activity) {
        this.activity = activity;
        PrefUtils.initPreference(activity);
    }

    private Dialog buildDialogView(@LayoutRes int layout, int styleAnimation) {
        final Dialog dialog = new Dialog(activity, R.style.DialogLevelCompleted);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (styleAnimation == 1)
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation1;
        else
            dialog.getWindow().getAttributes().windowAnimations = R.style.MoreDialogAnimation;
        return dialog;
    }

    //Dialog Msg
    public Dialog buildDialogMessage(final CallbackMessage callback, String message) {
        final Dialog dialog = buildDialogView(R.layout.dialog_alert, 1);
        ((TextView) dialog.findViewById(R.id.txt_msg)).setText(message);
        ((TextView) dialog.findViewById(R.id.txt_cancleclass_send)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onSuccess(dialog);
            }
        });
        ((ImageButton) dialog.findViewById(R.id.image_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancel(dialog);
            }
        });

        return dialog;
    }


    public Dialog buildDialogCartype(final CallbackCarType callback, String message, final List<CarBrand> list_cartype) {

        final Dialog dialog = buildDialogView(R.layout.dialog_cartype, 1);

        ((ImageButton) dialog.findViewById(R.id.image_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onCancel(dialog);
            }
        });

        RecyclerView recycler_cartype = (RecyclerView) dialog.findViewById(R.id.recycler_cartype);

        if (list_cartype != null && list_cartype.size() > 0) {
            recycler_cartype.setHasFixedSize(true);
            recycler_cartype.setLayoutManager(new LinearLayoutManager(activity));
            CartypeAdapter adapter = new CartypeAdapter(activity, list_cartype);
            recycler_cartype.setAdapter(adapter);
        }


        ((TextView) dialog.findViewById(R.id.txt_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (list_cartype != null && list_cartype.size() > 0) {

                    boolean isSelectflag = false;
                    for (int i = 0; i < list_cartype.size(); i++) {
                        // PlanData packdata = Common.plandataitem.get(i);
                        CarBrand carTypedata = list_cartype.get(i);

                        if (carTypedata.isSelected()) {
                            isSelectflag = true;
                            callback.onSuccess(dialog, carTypedata.getBrand(), "", "", "");
                            break;
                        }
                    }

                   /* if (!isSelectflag)
                    {
                        callback.onCancel(dialog);
                    }*/

                }

            }
        });

        return dialog;
    }

    //Dialog Month
    public Dialog buildDialogMonth(final CallbackYear callback, String message, final List<CarMonth> months_list) {

        final Dialog dialog = buildDialogView(R.layout.dialog_cartype, 1);

        ((ImageButton) dialog.findViewById(R.id.image_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onCancel(dialog);
            }
        });

        ((TextView) dialog.findViewById(R.id.txt_msg)).setText(message);

        RecyclerView recycler_cartype = (RecyclerView) dialog.findViewById(R.id.recycler_cartype);

        if (months_list != null && months_list.size() > 0) {
            recycler_cartype.setHasFixedSize(true);
            recycler_cartype.setLayoutManager(new LinearLayoutManager(activity));
            CarMonthAdapter adapter = new CarMonthAdapter(activity, months_list);
            recycler_cartype.setAdapter(adapter);
        }

        ((TextView) dialog.findViewById(R.id.txt_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (months_list != null && months_list.size() > 0) {
                    boolean isSelectflag = false;
                    for (int i = 0; i < months_list.size(); i++) {
                        CarMonth carYeardata = months_list.get(i);

                        if (carYeardata.isSelected()) {

                            isSelectflag = true;
                            callback.onSuccess(dialog, carYeardata.getMonth());
                            break;
                        }
                    }

                    /*if (!isSelectflag) {
                        callback.onCancel(dialog);
                    }*/

                }


            }
        });


        return dialog;
    }

    //Dialog Year
    public Dialog buildDialogYear(final CallbackYear callback, String message, final List<CarYear> year_list) {

        final Dialog dialog = buildDialogView(R.layout.dialog_cartype, 1);

        ((ImageButton) dialog.findViewById(R.id.image_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onCancel(dialog);
            }
        });

        ((TextView) dialog.findViewById(R.id.txt_msg)).setText(message);

        RecyclerView recycler_cartype = (RecyclerView) dialog.findViewById(R.id.recycler_cartype);

        if (year_list != null && year_list.size() > 0) {
            recycler_cartype.setHasFixedSize(true);
            recycler_cartype.setLayoutManager(new LinearLayoutManager(activity));
            CarYearAdapter adapter = new CarYearAdapter(activity, year_list);
            recycler_cartype.setAdapter(adapter);
        }

        ((TextView) dialog.findViewById(R.id.txt_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (year_list != null && year_list.size() > 0) {

                    boolean isSelectflag = false;
                    for (int i = 0; i < year_list.size(); i++) {
                        CarYear carYeardata = year_list.get(i);

                        if (carYeardata.isSelected()) {

                            isSelectflag = true;
                            callback.onSuccess(dialog, carYeardata.getYear());
                            break;
                        }
                    }

                   /* if (!isSelectflag) {
                        callback.onCancel(dialog);
                    }*/

                }


            }
        });


        return dialog;
    }

    //Dialog CarBrand
    public Dialog buildDialogBrand(final CallbackYear callback, String message, final List<CarBrand> Brand_list) {

        final Dialog dialog = buildDialogView(R.layout.dialog_cartype, 1);

        ((ImageButton) dialog.findViewById(R.id.image_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callback.onCancel(dialog);
            }
        });

        ((TextView) dialog.findViewById(R.id.txt_msg)).setText(message);

        RecyclerView recycler_cartype = (RecyclerView) dialog.findViewById(R.id.recycler_cartype);

        if (Brand_list != null && Brand_list.size() > 0) {
            recycler_cartype.setHasFixedSize(true);
            recycler_cartype.setLayoutManager(new LinearLayoutManager(activity));
            CarBrandAdapter adapter = new CarBrandAdapter(activity, Brand_list);
            recycler_cartype.setAdapter(adapter);
        }

        ((TextView) dialog.findViewById(R.id.txt_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Brand_list != null && Brand_list.size() > 0) {

                    boolean isSelectflag = false;
                    for (int i = 0; i < Brand_list.size(); i++) {
                        CarBrand carBranddata = Brand_list.get(i);
                        if (carBranddata.isSelected()) {

                            isSelectflag = true;
                            callback.onSuccess(dialog, carBranddata.getBrand());
                            break;
                        }
                    }

                   /* if (!isSelectflag) {
                        callback.onCancel(dialog);
                    }*/

                }

            }
        });


        return dialog;
    }
}