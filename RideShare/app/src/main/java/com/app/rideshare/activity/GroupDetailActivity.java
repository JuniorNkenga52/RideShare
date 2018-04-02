package com.app.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.RideShareApi;
import com.app.rideshare.model.GroupList;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class GroupDetailActivity extends AppCompatActivity {
    private ImageView mBackIv;

    User mUserBean;
    GroupList groupDetail;
    String mTag;

    private ListView mLvGroup;
    ArrayList<User> mListGroupUser = new ArrayList<>();
    GroupAdapter groupAdapter;

    private CircularImageView mGroupIv;
    private TextView mGroupName;
    private TextView mGroupDescription;

    private TextView mNoUserTv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        PrefUtils.initPreference(this);
        mUserBean = PrefUtils.getUserInfo();

        if(getIntent().hasExtra("groupDetail")){
            groupDetail = (GroupList) getIntent().getSerializableExtra("groupDetail");
            mTag = getIntent().getStringExtra("mTag");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Group info");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        mNoUserTv = (TextView) findViewById(R.id.no_user);
        mNoUserTv.setVisibility(View.GONE);

        mGroupName = (TextView) findViewById(R.id.groupname_tv);
        mGroupDescription = (TextView) findViewById(R.id.description_tv);

        mGroupIv = (CircularImageView) findViewById(R.id.circularImageView);
        if (!PrefUtils.getUserInfo().getProfile_image().equals("")) {
            Picasso.with(this).load(groupDetail.getCategory_image()).resize(300, 300)
                    .centerCrop().into(mGroupIv);
        }

        mGroupName.setText(groupDetail.getGroup_name());
        mGroupDescription.setText(groupDetail.getGroup_description());

        mLvGroup = (ListView) findViewById(R.id.mLvGroup);

        new AsyncGroupDetail(groupDetail.getId()).execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(mTag.equalsIgnoreCase("Explore")) {
            RideShareApp.mHomeTabPos = 0;

            Intent i = new Intent(GroupDetailActivity.this, HomeNewActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            RideShareApp.mHomeTabPos = 3;

            Intent i = new Intent(GroupDetailActivity.this, MyGroupActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }



    }

    public class AsyncGroupDetail extends AsyncTask<Object, Integer, Object> {

        CustomProgressDialog mProgressDialog;
        String group_id;
        public AsyncGroupDetail(String group_id) {

            this.group_id = group_id;

            mProgressDialog = new CustomProgressDialog(GroupDetailActivity.this);
            mProgressDialog.show();
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        public Object doInBackground(Object... params) {
            try {
                return RideShareApi.groupusers(PrefUtils.getUserInfo().getmUserId(), group_id);
                //return RideShareApi.groupusers("30", group_id);
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

                    for (int i = 0; i < jArrayResult.length(); i++) {
                        JSONObject jObjResult = jArrayResult.getJSONObject(i);

                        User bean = new User();

                        bean.setmUserId(jObjResult.getString("u_id"));
                        bean.setmFirstName(jObjResult.getString("u_firstname"));
                        bean.setmLastName(jObjResult.getString("u_lastname"));
                        bean.setProfile_image(jObjResult.getString("profile_image"));

                        mListGroupUser.add(bean);

                    }

                    if (mListGroupUser.size() == 0) {
                        mNoUserTv.setVisibility(View.VISIBLE);
                    }

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

            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mListGroupUser.size();
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
            CircularImageView circularImageView;
            TextView mGroupUserName;

        }


        @Override
        public View getView(final int pos, View vi, ViewGroup parent) {

            final GroupAdapter.ViewHolder holder;

            if (vi == null) {

                vi = mInflater.inflate(R.layout.item_group_user, null);

                holder = new GroupAdapter.ViewHolder();

                holder.circularImageView = (CircularImageView) vi.findViewById(R.id.circularImageView);
                holder.mGroupUserName = (TextView) vi.findViewById(R.id.mGroupUserName);

                vi.setTag(holder);
            } else {
                holder = (GroupAdapter.ViewHolder) vi.getTag();
            }

            final User bean = mListGroupUser.get(pos);

            holder.mGroupUserName.setText(bean.getmFirstName() + " " + bean.getmLastName());

            Picasso.with(GroupDetailActivity.this).load(bean.getProfile_image()).resize(300, 300)
                    .centerCrop().error(R.drawable.user_icon).into(holder.circularImageView );

            return vi;
        }
    }


}
