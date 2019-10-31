package com.app.rideWhiz.utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.adapter.CarBrandAdapter;
import com.app.rideWhiz.adapter.CarMonthAdapter;
import com.app.rideWhiz.adapter.CarYearAdapter;
import com.app.rideWhiz.adapter.CartypeAdapter;
import com.app.rideWhiz.adapter.FinishRideAdapter;
import com.app.rideWhiz.api.response.AcceptRider;
import com.app.rideWhiz.listner.CallbackFinishRider;
import com.app.rideWhiz.listner.CallbackRequestType;
import com.app.rideWhiz.listner.CallbackStartRider;
import com.app.rideWhiz.model.CarBrand;
import com.app.rideWhiz.model.CarMonth;
import com.app.rideWhiz.model.CarYear;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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

    // Show Ride AcceptReject Dialog

    public Dialog buildDialogRequestType(final CallbackRequestType callback, String message, String source, String destination) {

        final Dialog dialog = buildDialogView(R.layout.dialog_rider_request, 1);

        TextView txt_rider_name = dialog.findViewById(R.id.txt_rider_name);
        txt_rider_name.setText(message);

        TextView starting_address_tv = dialog.findViewById(R.id.starting_address_tv);
        starting_address_tv.setText(source);
        TextView ending_address_tv = dialog.findViewById(R.id.ending_address_tv);
        ending_address_tv.setText(destination);

        Button btnAccept_ride = dialog.findViewById(R.id.btnAccept_ride);
        Button btnReject_ride = dialog.findViewById(R.id.btnReject_ride);
        ImageView image_close_ride_dialog = dialog.findViewById(R.id.image_close_ride_dialog);

        btnAccept_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onAccept();
            }
        });

        btnReject_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onReject();
            }
        });

        image_close_ride_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancel();
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
                }

            }
        });


        return dialog;
    }

    //Dialog Riders

    public Dialog buildDialogFinishRide(final CallbackFinishRider callback, ArrayList<AcceptRider> ridersList, String msg, final Activity activity) {

        final Dialog dialog = buildDialogView(R.layout.dialog_rider_finish, 1);
        RecyclerView rv_car_features = (dialog.findViewById(R.id.rv_car_features));
        RecyclerView.LayoutManager rlManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        rv_car_features.setLayoutManager(rlManager);
        final ArrayList<HashMap<String, String>> finshedRiderList = new ArrayList<>();
        if (finshedRiderList.size() == 0) {
            for (int i = 0; i < ridersList.size(); i++) {
                Log.e("", "Finish Riders-" + ridersList.get(i).getFromRider().getmFirstName());
                if (!ridersList.get(i).getFromRider().getIs_new_request()) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("finish_riders", ridersList.get(i).getFromRider().getmFirstName());
                    hashMap.put("rider_id", ridersList.get(i).getRide_id());
                    hashMap.put("user_id", ridersList.get(i).getFromRider().getnUserId());
                    hashMap.put("driver_id", ridersList.get(i).getToRider().getnUserId());
                    if (i == 0)
                        hashMap.put("isSelected", "1");
                    else
                        hashMap.put("isSelected", "0");
                    finshedRiderList.add(hashMap);
                }
            }
        }
        final FinishRideAdapter car_featuresAdapter = new FinishRideAdapter(activity, finshedRiderList);
        rv_car_features.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        rv_car_features.setAdapter(car_featuresAdapter);

        Button btn_msg = dialog.findViewById(R.id.popup_ok);
        btn_msg.setText(msg);

        if (((TextView) dialog.findViewById(R.id.popup_ok)).getText().toString().equals(activity.getResources().getString(R.string.cancel))) {

            (dialog.findViewById(R.id.popup_ok)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onCancle(dialog);
                }
            });

        } else {
            car_featuresAdapter.setOnCarFeaturesItemClickListener(new FinishRideAdapter.OnCarFeaturesClickListener() {
                @Override
                public void selectCarFeatures(int position) {
                    car_featuresAdapter.updatePersonCount();
                }
            });

            (dialog.findViewById(R.id.popup_ok)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Carfeatures selection
                    String listoffeatures = "";
                    String userID = "";
                    String driverID = "";
                    int listSelect = 0;
                    int pos=0;
                    for (HashMap<String, String> hashMap : finshedRiderList) {
                        if (hashMap.get("isSelected").equals("1")) {
                            listoffeatures += hashMap.get("rider_id") + ",";
                            userID += hashMap.get("user_id") + ",";
                            driverID += hashMap.get("driver_id") + ",";
                            pos = finshedRiderList.indexOf(hashMap);
                            listSelect++;
                        }
                    }
                    if (listSelect > 5) {
                        //Please select only 5 options.
                        callback.onError("", dialog);
                    } else {
                        Log.e("", "Selected Position" + pos);
                        Log.e("", "List of carfeatures" + listoffeatures);
                        if (listoffeatures != null && listoffeatures.length() > 0) {
                            callback.onselectCarFeatures(listoffeatures.substring(0, listoffeatures.length() - 1), userID.substring(0, userID.length() - 1), driverID.substring(0, driverID.length() - 1),pos, dialog);
                        } else {
                            callback.onselectCarFeatures("", "", "",pos, dialog);
                        }
                        callback.onCreate(dialog);
                    }

                }
            });

        }
        (dialog.findViewById(R.id.popup_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancle(dialog);
            }
        });

        return dialog;
    }

    public Dialog buildDialogStartRide(final CallbackStartRider callback, String driverName, String time, String distance, final Activity activity) {

        final Dialog dialog = buildDialogView(R.layout.dialog_rider_start, 1);


        TextView tv_start_title_caption = dialog.findViewById(R.id.tv_start_title_caption);
        tv_start_title_caption.setText(activity.getString(R.string.start_ride_request) + " " + driverName);

        TextView ride_time_tv = dialog.findViewById(R.id.ride_time_tv);
        ride_time_tv.setText(time);

        TextView ride_dis_tv = dialog.findViewById(R.id.ride_dis_tv);
        ride_dis_tv.setText(distance);

        ImageView img_btn_chat=dialog.findViewById(R.id.img_btn_chat);
        img_btn_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onChat(dialog);
            }
        });

        if (((TextView) dialog.findViewById(R.id.btn_start_ride)).getText().toString().equals(activity.getResources().getString(R.string.cancel))) {

            (dialog.findViewById(R.id.btn_start_ride)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.onCancle(dialog);
                }
            });

        } else {

            (dialog.findViewById(R.id.btn_start_ride)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onStart( dialog);
                    //callback.onCreate(dialog);
                }
            });

        }
        (dialog.findViewById(R.id.popup_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCancle(dialog);
            }
        });

        return dialog;
    }
}