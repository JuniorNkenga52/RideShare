package com.app.rideWhiz.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.rideWhiz.R;
import com.app.rideWhiz.api.RideShareApi;
import com.app.rideWhiz.model.GroupList;
import com.app.rideWhiz.model.InProgressRide;
import com.app.rideWhiz.service.LocationProvider;
import com.app.rideWhiz.utils.AppUtils;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MyGroupSelectionActivity extends AppCompatActivity implements LocationProvider.LocationCallback {

    ArrayList<GroupList> mListGroup = new ArrayList<>();
    ArrayList<GroupList> mSearchListGroup = new ArrayList<>();
    Context context;
    ListView mLvMyGroup;
    GroupAdapter groupAdapter;
    EditText tvSearchGroup;
    //String adminid;
    CustomProgressDialog mProgressDialog;
    TextView txtskip;
    SwipeRefreshLayout swipeRefreshRequests;
    String InprogressRide = "";

    LocationProvider mLocationProvider;
    InProgressRide inProgressRideModel;
    String rideUserID;
    String Is_driver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group_selection);
        context = this;
        PrefUtils.initPreference(this);
        mProgressDialog = new CustomProgressDialog(context);

        //adminid = PrefUtils.getString("AdminID");

        mLocationProvider = new LocationProvider(this, this);


        mLvMyGroup = findViewById(R.id.mLvMyGroup);
        txtskip = findViewById(R.id.txtskip);
        swipeRefreshRequests = findViewById(R.id.swipeRefreshRequests);

        new AsyncMyGroup().execute();

        tvSearchGroup = (EditText) findViewById(R.id.txtSearchGroup);

        tvSearchGroup.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = tvSearchGroup.getText().toString().toLowerCase(Locale.getDefault());
                if (groupAdapter != null)
                    groupAdapter.filter(text.trim());
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
        });
        txtskip.setOnClickListener(v -> {
            RideShareApp.mRideTypeTabPos = 0;
            if (mSearchListGroup != null && mSearchListGroup.size() > 0 && PrefUtils.getMyGroupInfo() != null && PrefUtils.getMyGroupInfo().size() > 0) {
                PrefUtils.putString("SelectedGroup", mSearchListGroup.get(0).getGroup_name());
                PrefUtils.putString("SelectedGroupID", mSearchListGroup.get(0).getId());
                PrefUtils.putBoolean(PrefUtils.PREF_IS_ADMIN, false);
                for (int i = 0; i < PrefUtils.getMyGroupInfo().size(); i++) {
                    if (mSearchListGroup.get(0).getId().equals(PrefUtils.getMyGroupInfo().get(i).getId())) {
                        PrefUtils.putBoolean(PrefUtils.PREF_IS_ADMIN, true);
                    }
                }

                Intent i = new Intent(context, RideTypeActivity.class);
                if (!InprogressRide.equals("")) {
                    i.putExtra("inprogress", "busy");
                    i.putExtra("rideprogress", inProgressRideModel);
                    i.putExtra("rideUserID", rideUserID);
                    i.putExtra("Is_driver", Is_driver);
                }

                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else {
                MessageUtils.showFailureMessage(context, "Please Select at least one Group");
            }

        });

        swipeRefreshRequests.setOnRefreshListener(() -> {
            if (AppUtils.isInternetAvailable(context)) {
                new AsyncMyGroup().execute();
            } else {
                swipeRefreshRequests.setRefreshing(false);
            }
        });
        swipeRefreshRequests.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        if (getIntent().hasExtra("inprogress")) {
            InprogressRide = getIntent().getExtras().getString("inprogress");
            inProgressRideModel = (InProgressRide) getIntent().getExtras().getSerializable("rideprogress");
            rideUserID = getIntent().getExtras().getString("rideUserID");
            Is_driver = getIntent().getExtras().getString("Is_driver");
        }
    }

    public class AsyncMyGroup extends AsyncTask<Object, Integer, Object> {

        //CustomProgressDialog mProgressDialog;

        AsyncMyGroup() {

            //mProgressDialog = new CustomProgressDialog(context);
            mProgressDialog.show();
            swipeRefreshRequests.setRefreshing(false);
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        public Object doInBackground(Object... params) {
            try {
                return RideShareApi.mygroups(PrefUtils.getUserInfo().getmUserId(), context);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void onPostExecute(Object result) {
            super.onPostExecute(result);

            mProgressDialog.dismiss();

            try {
                Log.e("AsyncAllGroup", "onPostExecute: result>> " + result.toString());
                JSONObject jsonObject = new JSONObject(result.toString());

                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    JSONArray jArrayResult = new JSONArray(jsonObject.getString("result"));

                    mListGroup = new ArrayList<>();

                    if (jArrayResult.length() == 0) {
                        PrefUtils.putString("isBlank", "true");
                        Intent i = new Intent(MyGroupSelectionActivity.this, HomeNewActivity.class);
                        startActivity(i);

                    } else {
                        for (int i = 0; i < jArrayResult.length(); i++) {
                            JSONObject jObjResult = jArrayResult.getJSONObject(i);

                            GroupList bean = new GroupList();

                            bean.setId(jObjResult.getString("id"));
                            bean.setGroup_name(jObjResult.getString("group_name"));
                            bean.setGroup_description(jObjResult.getString("group_description"));
                            bean.setCategory_name(jObjResult.getString("category_name"));
                            bean.setCategory_image(jObjResult.getString("category_image"));
                            bean.setCategory_thumb_image(jObjResult.getString("category_thumb_image"));
                            bean.setIs_joined(jObjResult.optString("is_joined"));
                            bean.setShareLink(jObjResult.optString("share_link"));
                            bean.setCategory_id(jObjResult.optString("category_id"));
                            bean.setIs_assigned(jObjResult.optString("is_assigned"));

                            mListGroup.add(bean);
                            if (bean.getIs_assigned().equals("1")) {
                                mListGroup.remove(i);
                                mListGroup.add(0, bean);
                            }
                        }
                        mSearchListGroup.clear();
                        mSearchListGroup.addAll(mListGroup);

                        groupAdapter = new GroupAdapter();
                        mLvMyGroup.setAdapter(groupAdapter);
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GroupAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private GroupAdapter() {

            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mSearchListGroup.size();
        }

        @Override
        public GroupList getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            ImageView imgGroup;
            TextView txtGroupName;
            TextView txtGroupDescription;
            TextView txtJoin;
        }


        @SuppressLint("InflateParams")
        @Override
        public View getView(final int pos, View vi, ViewGroup parent) {

            final GroupAdapter.ViewHolder holder;

            if (vi == null) {

                vi = mInflater.inflate(R.layout.item_group, null);

                holder = new GroupAdapter.ViewHolder();

                holder.imgGroup = vi.findViewById(R.id.imgGroup);

                holder.txtGroupName = vi.findViewById(R.id.txtGroupName);

                holder.txtGroupDescription = vi.findViewById(R.id.txtGroupDescription);

                holder.txtJoin = vi.findViewById(R.id.txtJoin);
                holder.txtJoin.setTextColor(Color.WHITE);
                holder.txtJoin.setText("Select");
                vi.setTag(holder);
            } else {
                holder = (GroupAdapter.ViewHolder) vi.getTag();
            }

            final GroupList bean = mSearchListGroup.get(pos);

            holder.txtGroupName.setText(bean.getGroup_name());
            holder.txtGroupDescription.setText(bean.getGroup_description());

            Picasso.get().load(bean.getCategory_image()).error(R.drawable.user_icon).into(holder.imgGroup);

            /*0 = None (Join)
            1 = Requested
            2 = Accept Request(Joined)
            3 = Decline
            4 = Confirm*/

            if (bean.getIs_assigned().equalsIgnoreCase("0")) {
                holder.txtJoin.setText("Select");
                holder.txtJoin.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_join));
                holder.txtJoin.setTextColor(context.getResources().getColor(R.color.white));
            } else if (bean.getIs_assigned().equalsIgnoreCase("1")) {
                holder.txtJoin.setText("Selected");
                holder.txtJoin.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.btn_requested));
                holder.txtJoin.setTextColor(context.getResources().getColor(R.color.white));
                txtskip.setVisibility(View.VISIBLE);
            }

            holder.txtJoin.setTag(pos);
            holder.txtJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int poss = (int) v.getTag();
                    //PrefUtils.putString("MyID","");
                    new AsyncJoinGroup(poss).execute();
                }
            });

            return vi;
        }

        // Filter Class
        public void filter(String charText) {
            try {
                charText = charText.toLowerCase(Locale.getDefault());
                mSearchListGroup.clear();
                if (charText.length() == 0) {
                    mSearchListGroup.addAll(mListGroup);
                } else {
                    for (GroupList gp : mListGroup) {
                        if (gp.getGroup_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                            mSearchListGroup.add(gp);
                        }
                    }
                }
                notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class AsyncJoinGroup extends AsyncTask<Object, Integer, Object> {

        //private CustomProgressDialog mProgressDialog;
        int poss;
        TextView txtJoin;

        AsyncJoinGroup(int poss) {
            //mProgressDialog = new CustomProgressDialog(context);
            this.poss = poss;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        public Object doInBackground(Object... params) {
            try {
                return RideShareApi.getUpdateUserGroup(PrefUtils.getUserInfo().getmUserId(), mSearchListGroup.get(poss).getId(), context);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void onPostExecute(Object result) {
            super.onPostExecute(result);
            try {

                JSONObject jsonObject = new JSONObject(result.toString());

                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    RideShareApp.mRideTypeTabPos = 0;
                    if (mSearchListGroup.size() > 0) {
                        PrefUtils.putString("SelectedGroup", mSearchListGroup.get(poss).getGroup_name());
                        PrefUtils.putString("SelectedGroupID", mSearchListGroup.get(poss).getId());
                        PrefUtils.putBoolean(PrefUtils.PREF_IS_ADMIN, false);
                        for (int i = 0; i < PrefUtils.getMyGroupInfo().size(); i++) {
                            if (mSearchListGroup.get(poss).getId().equals(PrefUtils.getMyGroupInfo().get(i).getId())) {
                                PrefUtils.putBoolean(PrefUtils.PREF_IS_ADMIN, true);
                            }
                        }
                        Intent i = new Intent(context, RideTypeActivity.class);
                        if (!InprogressRide.equals("")) {
                            i.putExtra("inprogress", "busy");
                            i.putExtra("rideprogress", inProgressRideModel);
                            i.putExtra("rideUserID", rideUserID);
                            i.putExtra("Is_driver", Is_driver);
                        }

                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else {
                        MessageUtils.showFailureMessage(context, "Please Select at least one Group");
                    }
                    groupAdapter.notifyDataSetChanged();

                }
                mProgressDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLocationProvider.connect(MyGroupSelectionActivity.this);
    }

    public void handleNewLocation(Location location) {
        //currentLocation = location;
    }
}
