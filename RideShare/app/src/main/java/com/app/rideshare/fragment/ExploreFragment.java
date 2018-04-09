package com.app.rideshare.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.app.rideshare.activity.GroupDetailActivity;
import com.app.rideshare.api.RideShareApi;
import com.app.rideshare.model.GroupList;
import com.app.rideshare.utils.Constant;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class ExploreFragment extends Fragment {

    private ListView mLvGroup;
    ArrayList<GroupList> mListGroup = new ArrayList<>();
    ArrayList<GroupList> mSearchListGroup = new ArrayList<>();
    GroupAdapter groupAdapter;

    EditText txtSearchGroup;

    public static ExploreFragment newInstance() {
        ExploreFragment fragment = new ExploreFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_explore, container,
                false);

        PrefUtils.initPreference(getActivity());

        txtSearchGroup = (EditText) rootView.findViewById(R.id.txtSearchGroup);
        mLvGroup = (ListView) rootView.findViewById(R.id.mLvGroup);

        new AsyncAllGroup().execute();

        txtSearchGroup.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = txtSearchGroup.getText().toString().toLowerCase(Locale.getDefault());
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

        mLvGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent ii = new Intent(getActivity(), GroupDetailActivity.class);
                ii.putExtra("groupDetail", mSearchListGroup.get(position));
                ii.putExtra("mTag", "Explore");
                ii.putExtra(Constant.intentKey.MyGroup, false);
                startActivity(ii);
                getActivity().finish();
            }
        });

        return rootView;
    }

    public class AsyncAllGroup extends AsyncTask<Object, Integer, Object> {

        CustomProgressDialog mProgressDialog;

        public AsyncAllGroup() {

            mProgressDialog = new CustomProgressDialog(getActivity());
            mProgressDialog.show();
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        public Object doInBackground(Object... params) {
            try {
                return RideShareApi.groupNew(PrefUtils.getUserInfo().getmUserId());
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
                        bean.setIs_joined(jObjResult.getString("is_joined"));
                        bean.setIs_admin(jObjResult.optString("is_admin"));
                        bean.setStatus(jObjResult.optString("status"));
                        bean.setShareLink(jObjResult.optString("share_link"));
                        bean.setCategory_id(jObjResult.optString("category_id"));

                        if (jObjResult.optString("is_admin").equalsIgnoreCase("0"))
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


        @Override
        public View getView(final int pos, View vi, ViewGroup parent) {

            final ViewHolder holder;

            if (vi == null) {

                vi = mInflater.inflate(R.layout.item_group, null);

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

            Picasso.with(getActivity()).load(bean.getCategory_image()).error(R.drawable.user_icon).into(holder.imgGroup);

            /*0 = None (Join)
            1 = Requested
            2 = Accept Request(Joined)
            3 = Decline
            4 = Confirm*/

            if (bean.getStatus().equalsIgnoreCase("0")) {
                holder.txtJoin.setText("Join");
                holder.txtJoin.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.btn_join));
                holder.txtJoin.setTextColor(getActivity().getResources().getColor(R.color.white));
            } else if (bean.getStatus().equalsIgnoreCase("1")) {
                holder.txtJoin.setText("Requested");
                holder.txtJoin.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.btn_requested));
                holder.txtJoin.setTextColor(getActivity().getResources().getColor(R.color.white));
            } else if (bean.getStatus().equalsIgnoreCase("2")) {
                holder.txtJoin.setText("Accept");
                holder.txtJoin.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.btn_joined));
                holder.txtJoin.setTextColor(getActivity().getResources().getColor(R.color.white));
            } else if (bean.getStatus().equalsIgnoreCase("3")) {
                holder.txtJoin.setText("Join");
                holder.txtJoin.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.btn_join));
                holder.txtJoin.setTextColor(getActivity().getResources().getColor(R.color.white));
            } else {

            }

            holder.txtJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!bean.getStatus().equalsIgnoreCase("1") && !bean.getStatus().equalsIgnoreCase("2")) {
                        if (bean.getStatus().equalsIgnoreCase("1")) {
                            bean.setStatus("0");
                        } else if (bean.getStatus().equalsIgnoreCase("0") || bean.getStatus().equalsIgnoreCase("3")) {
                            bean.setStatus("1");
                        }

                        mSearchListGroup.set(pos, bean);

                        new AsyncJoinGroup(bean.getId(), bean.getStatus()).execute();
                    }

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

    public class AsyncJoinGroup extends AsyncTask<Object, Integer, Object> {

        String group_id;
        String status;
        CustomProgressDialog mProgressDialog;

        public AsyncJoinGroup(String group_id, String status) {

            this.group_id = group_id;
            this.status = status;

            mProgressDialog = new CustomProgressDialog(getActivity());
            mProgressDialog.show();
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        public Object doInBackground(Object... params) {
            try {
                return RideShareApi.joinGroup(PrefUtils.getUserInfo().getmUserId(), group_id,status);
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

                    groupAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
}