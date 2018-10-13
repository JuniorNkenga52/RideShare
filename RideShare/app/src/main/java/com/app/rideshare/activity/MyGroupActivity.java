package com.app.rideshare.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.RideShareApi;
import com.app.rideshare.model.GroupList;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.Constants;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class MyGroupActivity extends AppCompatActivity {
    User mUserBean;

    private ListView mLvGroup;
    private ArrayList<GroupList> mListGroup = new ArrayList<>();
    private ArrayList<GroupList> mSearchListGroup = new ArrayList<>();
    private GroupAdapter groupAdapter;

    private EditText tvSearchGroup;
    Activity activity;
    Context context;
    private TextView txt_no_grp;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygroup);

        activity = this;
        context=this;
        PrefUtils.initPreference(this);
        mUserBean = PrefUtils.getUserInfo();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Groups");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
                onBackPressed();
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

        mLvGroup = (ListView) findViewById(R.id.mLvGroup);

        mLvGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ii = new Intent(MyGroupActivity.this, GroupDetailActivity.class);
                ii.putExtra("groupDetail", mSearchListGroup.get(position));
                ii.putExtra("mTag", "Profile");
                ii.putExtra(Constants.intentKey.MyGroup, true);
                startActivity(ii);
//                finish();
            }
        });

        ImageView ivCreateGroup = (ImageView) findViewById(R.id.ivCreateGroup);
        ivCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(getApplicationContext(), CreateGroupActivity.class);
                i.putExtra(Constants.intentKey.isEditGroup, false);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                finish();
            }
        });

        new AsyncAllGroup().execute();


        tvSearchGroup = (EditText) findViewById(R.id.tvSearchGroup);
        txt_no_grp = findViewById(R.id.txt_no_grp);
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

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Constants.isGroupDataUpdated) { // IN CREATE GROUP ACTIVITY DATA UPDATED THAN REFRESH THE LIST HERE
            Constants.isGroupDataUpdated = false;
            Log.e("MyGroupActivity", "onResume: isGroupDataUpdated");
            new AsyncAllGroup().execute();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        RideShareApp.mHomeTabPos = 4;
        Intent i = new Intent(MyGroupActivity.this, HomeNewActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        activity.finish();
        //finish();

    }

    public class AsyncAllGroup extends AsyncTask<Object, Integer, Object> {

        CustomProgressDialog mProgressDialog;

        AsyncAllGroup() {

            mProgressDialog = new CustomProgressDialog(MyGroupActivity.this);
            mProgressDialog.show();
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        public Object doInBackground(Object... params) {
            try {
                return RideShareApi.mygroups(PrefUtils.getUserInfo().getmUserId(),context);
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
                    if (jArrayResult.length() > 0) {
                        PrefUtils.putString("isBlank", "false");
                    }
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
                        bean.setUser_id(jObjResult.optString("user_id"));
                        mListGroup.add(bean);

                    }

                    mSearchListGroup.clear();
                    mSearchListGroup.addAll(mListGroup);

                    groupAdapter = new GroupAdapter();
                    mLvGroup.setAdapter(groupAdapter);
                    if(mSearchListGroup.size()==0){
                        txt_no_grp.setVisibility(View.VISIBLE);
                    }else {
                        txt_no_grp.setVisibility(View.GONE);
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

        @Override
        public View getView(final int pos, View vi, ViewGroup parent) {

            final ViewHolder holder;

            if (vi == null) {

                vi = mInflater.inflate(R.layout.item_group, parent, false);

                holder = new ViewHolder();

                holder.imgGroup = (ImageView) vi.findViewById(R.id.imgGroup);
                holder.txtGroupName = (TextView) vi.findViewById(R.id.txtGroupName);
                holder.txtGroupDescription = (TextView) vi.findViewById(R.id.txtGroupDescription);
                holder.txtJoin = (TextView) vi.findViewById(R.id.txtJoin);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            final GroupList bean = mSearchListGroup.get(pos);

            holder.txtGroupName.setText(bean.getGroup_name());
            holder.txtGroupDescription.setText(bean.getGroup_description());

            Picasso.with(MyGroupActivity.this).load(bean.getCategory_thumb_image()).error(R.drawable.user_icon).into(holder.imgGroup);

            if (bean.getUser_id().equals(PrefUtils.getUserInfo().getmUserId())) {
                holder.txtJoin.setVisibility(View.VISIBLE);
                holder.txtJoin.setBackgroundColor(activity.getResources().getColor(R.color.white));
                holder.txtJoin.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
                holder.txtJoin.setText("Admin");
                holder.txtJoin.setTextSize(15);
                //holder.txtJoin.setBackground(R.color.white);
            } else {
                holder.txtJoin.setVisibility(View.GONE);
            }


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
}
