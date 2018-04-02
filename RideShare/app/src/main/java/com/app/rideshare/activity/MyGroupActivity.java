package com.app.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.RideShareApi;
import com.app.rideshare.fragment.ExploreFragment;
import com.app.rideshare.model.Category;
import com.app.rideshare.model.GroupList;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MyGroupActivity extends AppCompatActivity {
    private ImageView mBackIv;

    User mUserBean;

    private ListView mLvGroup;
    ArrayList<GroupList> mListGroup = new ArrayList<>();
    GroupAdapter groupAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mygroup);

        PrefUtils.initPreference(this);
        mUserBean = PrefUtils.getUserInfo();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Groups");
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

        mLvGroup = (ListView) findViewById(R.id.mLvGroup);

        mLvGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ii = new Intent(MyGroupActivity.this, GroupDetailActivity.class);
                ii.putExtra("groupDetail", mListGroup.get(position));
                ii.putExtra("mTag", "Profile");
                startActivity(ii);
                finish();
            }
        });

        new AsyncAllGroup().execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        RideShareApp.mHomeTabPos = 3;

        Intent i = new Intent(MyGroupActivity.this, HomeNewActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();

    }

    public class AsyncAllGroup extends AsyncTask<Object, Integer, Object> {

        CustomProgressDialog mProgressDialog;

        public AsyncAllGroup() {

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
                return RideShareApi.mygroups(PrefUtils.getUserInfo().getmUserId());
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

                        GroupList bean = new GroupList();

                        bean.setId(jObjResult.getString("id"));
                        bean.setGroup_name(jObjResult.getString("group_name"));
                        bean.setGroup_description(jObjResult.getString("group_description"));
                        bean.setCategory_name(jObjResult.getString("category_name"));
                        bean.setCategory_image(jObjResult.getString("category_image"));
                        bean.setIs_joined(jObjResult.optString("is_joined"));

                        mListGroup.add(bean);

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
            return mListGroup.size();
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

            final GroupAdapter.ViewHolder holder;

            if (vi == null) {

                vi = mInflater.inflate(R.layout.item_group, null);

                holder = new GroupAdapter.ViewHolder();

                holder.imgGroup = (ImageView) vi.findViewById(R.id.imgGroup);
                holder.txtGroupName = (TextView) vi.findViewById(R.id.txtGroupName);
                holder.txtGroupDescription = (TextView) vi.findViewById(R.id.txtGroupDescription);
                holder.txtJoin = (TextView) vi.findViewById(R.id.txtJoin);

                vi.setTag(holder);
            } else {
                holder = (GroupAdapter.ViewHolder) vi.getTag();
            }

            final GroupList bean = mListGroup.get(pos);

            holder.txtGroupName.setText(bean.getGroup_name());
            holder.txtGroupDescription.setText(bean.getGroup_description());

            Picasso.with(MyGroupActivity.this).load(bean.getCategory_image()).error(R.drawable.user_icon).into(holder.imgGroup);

            holder.txtJoin.setVisibility(View.GONE);

            return vi;
        }
    }


}
