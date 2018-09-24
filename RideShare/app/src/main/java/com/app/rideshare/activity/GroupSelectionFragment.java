package com.app.rideshare.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.RideShareApi;
import com.app.rideshare.fragment.ExploreFragment;
import com.app.rideshare.model.GroupList;
import com.app.rideshare.utils.AppUtils;
import com.app.rideshare.utils.MessageUtils;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class GroupSelectionFragment extends Fragment {

    private ListView mLvGroup;
    ArrayList<GroupList> mListGroup = new ArrayList<>();
    ArrayList<GroupList> mSearchListGroup = new ArrayList<>();
    EditText txtSearchGroup;
    Context context;
    GroupAdapter groupAdapter;
    TextView txtHeaderName;
    SwipeRefreshLayout swipeRefreshRequests;



    public static GroupSelectionFragment newInstance() {
        GroupSelectionFragment fragment = new GroupSelectionFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container,
                false);






        PrefUtils.initPreference(getActivity());
        PrefUtils.putString("loginwith", "normal");
        context = getActivity();

        txtSearchGroup = rootView.findViewById(R.id.txtSearchGroup);
        mLvGroup = rootView.findViewById(R.id.mLvGroup);

        txtHeaderName = rootView.findViewById(R.id.txtHeaderName);
        txtHeaderName.setText("Select Group");

        swipeRefreshRequests = rootView.findViewById(R.id.swipeRefreshRequests);

        new AsyncAllGroup().execute();

        txtSearchGroup.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = txtSearchGroup.getText().toString().toLowerCase(Locale.getDefault());
                groupAdapter.filter(text.trim());

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }
        });

        swipeRefreshRequests.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (AppUtils.isInternetAvailable(context)) {
                    new AsyncAllGroup().execute();
                } else {
                    swipeRefreshRequests.setRefreshing(false);
                }
            }
        });
        swipeRefreshRequests.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myNotificationReceiver, new IntentFilter("new_user"));



    }

    private BroadcastReceiver myNotificationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (AppUtils.isInternetAvailable(context)) {
                    Fragment selectedFragment = null;
                    if (PrefUtils.getString("isBlank").equals("true")) {
                        selectedFragment = ExploreFragment.newInstance();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.frame_layout_group, selectedFragment);
                        transaction.commit();
                    }
                    //new AsyncAllGroup().execute();
                } else {
                    swipeRefreshRequests.setRefreshing(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myNotificationReceiver);
    }

    @SuppressLint("StaticFieldLeak")
    public class AsyncAllGroup extends AsyncTask<Object, Integer, Object> {

        private CustomProgressDialog mProgressDialog;

        AsyncAllGroup() {
            mProgressDialog = new CustomProgressDialog(context);
            swipeRefreshRequests.setRefreshing(false);
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        public Object doInBackground(Object... params) {
            try {
                return RideShareApi.getFirstLoginGroup(PrefUtils.getUserInfo().getmUserId(),context);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void onPostExecute(Object result) {
            super.onPostExecute(result);

            mProgressDialog.dismiss();

            try {

                JSONObject jsonObject = new JSONObject(result.toString());

                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    JSONArray jArrayResult = new JSONArray(jsonObject.getString("result"));

                    mListGroup.clear();
                    for (int i = 0; i < jArrayResult.length(); i++) {
                        JSONObject jObjResult = jArrayResult.getJSONObject(i);

                        GroupList bean = new GroupList();

                        bean.setId(jObjResult.getString("id"));
                        bean.setGroup_name(jObjResult.getString("group_name"));
                        bean.setGroup_description(jObjResult.getString("group_description"));
                        bean.setCategory_name(jObjResult.getString("category_name"));
                        bean.setCategory_image(jObjResult.getString("category_image"));
                        /*bean.setIs_joined(jObjResult.getString("is_joined"));
                        bean.setIs_admin(jObjResult.optString("is_admin"));*/
                        bean.setStatus(jObjResult.optString("status"));
                        bean.setShareLink(jObjResult.optString("share_link"));
                        //bean.setCategory_id(jObjResult.optString("category_id"));
                        //if (jObjResult.optString("is_admin").equalsIgnoreCase("0"))
                        mListGroup.add(bean);

                    }

                    mSearchListGroup.clear();
                    mSearchListGroup.addAll(mListGroup);

                    groupAdapter = new GroupAdapter();
                    mLvGroup.setAdapter(groupAdapter);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    private class GroupAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        private GroupAdapter() {

            mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

                vi.setTag(holder);
            } else {
                holder = (GroupAdapter.ViewHolder) vi.getTag();
            }

            final GroupList bean = mSearchListGroup.get(pos);

            holder.txtGroupName.setText(bean.getGroup_name());
            holder.txtGroupDescription.setText(bean.getGroup_description());

            Picasso.with(context).load(bean.getCategory_image()).error(R.drawable.user_icon).into(holder.imgGroup);

            /*0 = None (Join)
            1 = Requested
            2 = Accept Request(Joined)
            3 = Decline
            4 = Confirm*/

            if (bean.getStatus().equalsIgnoreCase("0") || bean.getStatus().equalsIgnoreCase("3")) {
                holder.txtJoin.setVisibility(View.VISIBLE);
            }else if(bean.getStatus().equalsIgnoreCase("1")){
                holder.txtJoin.setVisibility(View.VISIBLE);
                holder.txtJoin.setText("Requested");
                holder.txtJoin.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.btn_requested));
                holder.txtJoin.setTextColor(getActivity().getResources().getColor(R.color.white));
            }
            else
                holder.txtJoin.setVisibility(View.GONE);

            holder.txtJoin.setTag(pos);
            holder.txtJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int poss = (int) v.getTag();
                    holder.txtJoin.setText("Requested");
                    holder.txtJoin.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.btn_requested));
                    holder.txtJoin.setTextColor(getActivity().getResources().getColor(R.color.white));
                    if(!bean.getStatus().equalsIgnoreCase("1")){
                        new AsyncJoinGroup(poss).execute();
                    }
                }
            });

            return vi;
        }
        @Override
        public int getViewTypeCount() {
            return getCount();
        }
        @Override
        public int getItemViewType(int position) {

            return position;
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

        private CustomProgressDialog mProgressDialog;
        int poss;
        TextView txtJoin;

        AsyncJoinGroup(int poss) {
            mProgressDialog = new CustomProgressDialog(context);
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
                return RideShareApi.joinGroup(PrefUtils.getUserInfo().getmUserId(), mSearchListGroup.get(poss).getId(), "1",getContext());
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void onPostExecute(Object result) {
            super.onPostExecute(result);

            mProgressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(result.toString());

                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    mListGroup.get(poss).setStatus("1");
                    /*for(int i=0;i<mSearchListGroup.size();i++){
                        mSearchListGroup.get(i).setStatus("1");
                    }*/
                    //mSearchListGroup.get(poss).setStatus("1");
                    MessageUtils.showSuccessMessage(context, "Your request to join Group has been sent. You will see further messages in your notification tab.");
                    groupAdapter.notifyDataSetChanged();

                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
}