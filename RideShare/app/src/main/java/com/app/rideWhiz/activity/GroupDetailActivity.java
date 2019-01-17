package com.app.rideWhiz.activity;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.api.RideShareApi;
import com.app.rideWhiz.model.GroupList;
import com.app.rideWhiz.model.User;
import com.app.rideWhiz.utils.CommonDialog;
import com.app.rideWhiz.utils.Constants;
import com.app.rideWhiz.utils.PrefUtils;
import com.app.rideWhiz.view.CustomProgressDialog;
import com.bumptech.glide.Glide;
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
    private boolean MyGroup;
    Activity activity;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        activity=this;
        context=this;
        PrefUtils.initPreference(this);
        mUserBean = PrefUtils.getUserInfo();

        if(getIntent().hasExtra("groupDetail")){
            groupDetail = (GroupList) getIntent().getSerializableExtra("groupDetail");
            mTag = getIntent().getStringExtra("mTag");
            MyGroup =  getIntent().getBooleanExtra(Constants.intentKey.MyGroup, false);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Group info");
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

        mNoUserTv = (TextView) findViewById(R.id.no_user);
        mNoUserTv.setVisibility(View.GONE);

        mGroupName = (TextView) findViewById(R.id.groupname_tv);
        mGroupDescription = (TextView) findViewById(R.id.description_tv);

        mGroupIv = (CircularImageView) findViewById(R.id.circularImageView);
        if (!PrefUtils.getUserInfo().getProfile_image().equals("")) {
            Picasso.with(this).load(groupDetail.getCategory_image()).resize(300, 300)
                    .centerCrop().into(mGroupIv);
            /*Glide.with(this).load(groupDetail.getCategory_image())
                    .error(R.drawable.icon_test)
                    .placeholder(R.drawable.icon_test)
                    .into(mGroupIv);*/
        }

        mGroupName.setText(groupDetail.getGroup_name());
        mGroupDescription.setText(groupDetail.getGroup_description());

        mLvGroup = (ListView) findViewById(R.id.mLvGroup);


        LinearLayout llMyGroup = (LinearLayout) findViewById(R.id.llMyGroup);
        ImageView ivInviteLink = (ImageView) findViewById(R.id.ivInviteLink);
        ImageView ivEditGroup = (ImageView) findViewById(R.id.ivEditGroup);
        if (MyGroup){
            llMyGroup.setVisibility(View.VISIBLE);
        } else {
            llMyGroup.setVisibility(View.GONE);
        }

        ivInviteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareData = groupDetail.getShareLink();
                if (shareData != null && !shareData.trim().isEmpty()) {
                    CommonDialog.shareInviteLinkDialog(GroupDetailActivity.this, shareData,1);
                } else {
                    CommonDialog.shareInviteLinkDialog(GroupDetailActivity.this, "Testing data",1);
                }
            }
        });

        if( !groupDetail.getUser_id().equals(PrefUtils.getUserInfo().getmUserId())){
            ivEditGroup.setVisibility(View.GONE);
        }else {
            ivEditGroup.setVisibility(View.VISIBLE);
        }
        ivEditGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent i = new Intent(getApplicationContext(), CreateGroupActivity.class);
                i.putExtra(Constants.intentKey.isEditGroup, true);
                i.putExtra(Constants.intentKey.groupDetail, groupDetail);
                startActivity(i);
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });

        new AsyncGroupDetail(groupDetail.getId()).execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(mTag.equalsIgnoreCase("Explore")) {
            RideShareApp.mHomeTabPos = 1;

            Intent i = new Intent(GroupDetailActivity.this, HomeNewActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            activity.finish();
            //finish();
        } else {
            RideShareApp.mHomeTabPos = 4;

            Intent i = new Intent(GroupDetailActivity.this, MyGroupActivity.class);
            i.putExtra("", false);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            activity.finish();
            //finish();
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
                return RideShareApi.groupusers(PrefUtils.getUserInfo().getmUserId(), group_id,context);
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
                        bean.setThumb_image(jObjResult.getString("thumb_image"));

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

            final ViewHolder holder;

            if (vi == null) {

                vi = mInflater.inflate(R.layout.item_group_user, null);

                holder = new ViewHolder();

                holder.circularImageView = (CircularImageView) vi.findViewById(R.id.circularImageView);
                holder.mGroupUserName = (TextView) vi.findViewById(R.id.mGroupUserName);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            final User bean = mListGroupUser.get(pos);

            holder.mGroupUserName.setText(bean.getmFirstName() + " " + bean.getmLastName());

            /*Picasso.with(GroupDetailActivity.this).load(bean.getProfile_image()).resize(300, 300)
                    .centerCrop().error(R.drawable.user_icon).into(holder.circularImageView );*/
            Glide.with(GroupDetailActivity.this).load(bean.getThumb_image())
                    .error(R.drawable.icon_test)
                    .into(holder.circularImageView);

            return vi;
        }
    }

}
