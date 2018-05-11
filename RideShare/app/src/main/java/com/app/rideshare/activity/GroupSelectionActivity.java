package com.app.rideshare.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.app.rideshare.model.GroupList;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class GroupSelectionActivity extends AppCompatActivity {

    private ListView mLvGroup;
    ArrayList<GroupList> mListGroup = new ArrayList<>();
    ArrayList<GroupList> mSearchListGroup = new ArrayList<>();
    EditText txtSearchGroup;
    Context context;
    GroupAdapter groupAdapter;
    TextView txtHeaderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_explore);

        PrefUtils.initPreference(this);

        context = this;

        txtSearchGroup = findViewById(R.id.txtSearchGroup);
        mLvGroup = findViewById(R.id.mLvGroup);

        txtHeaderName = findViewById(R.id.txtHeaderName);
        txtHeaderName.setText("Select Group");

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
    }

    @SuppressLint("StaticFieldLeak")
    public class AsyncAllGroup extends AsyncTask<Object, Integer, Object> {

        private CustomProgressDialog mProgressDialog;

        AsyncAllGroup() {
            mProgressDialog = new CustomProgressDialog(context);
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        public Object doInBackground(Object... params) {
            try {
                return RideShareApi.getFirstLoginGroup(PrefUtils.getUserInfo().getmUserId());
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

            if (bean.getStatus().equalsIgnoreCase("0"))
                holder.txtJoin.setVisibility(View.VISIBLE);
            else
                holder.txtJoin.setVisibility(View.GONE);

            holder.txtJoin.setTag(bean.getId());
            holder.txtJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = (String) v.getTag();
                    new AsyncJoinGroup(id, "1", (TextView) v).execute();
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

        String group_id;
        String status;
        CustomProgressDialog mProgressDialog;
        TextView txtJoin;

        AsyncJoinGroup(String group_id, String status, TextView txtJoin) {

            this.group_id = group_id;
            this.status = status;
            this.txtJoin = txtJoin;
            mProgressDialog = new CustomProgressDialog(context);
            mProgressDialog.show();
        }

        @Override
        public Object doInBackground(Object... params) {
            try {
                return RideShareApi.joinGroup(PrefUtils.getUserInfo().getmUserId(), group_id, status);
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
                    txtJoin.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
}