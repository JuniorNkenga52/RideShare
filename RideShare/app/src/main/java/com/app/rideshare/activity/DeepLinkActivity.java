package com.app.rideshare.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.rideshare.api.RideShareApi;
import com.app.rideshare.model.GroupList;
import com.app.rideshare.utils.Constants;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.view.CustomProgressDialog;

import org.json.JSONObject;

public class DeepLinkActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;

        PrefUtils.initPreference(context);

        Uri uri = getIntent().getData();

        String groupId = uri.getQueryParameter("group");

        new AsyncGetGroupDetail().execute(groupId);
    }

    public class AsyncGetGroupDetail extends AsyncTask<String, Integer, String> {

        CustomProgressDialog mProgressDialog;

        public AsyncGetGroupDetail() {
            mProgressDialog = new CustomProgressDialog(context);
            mProgressDialog.show();
        }

        @Override
        public String doInBackground(String... params) {
            try {
                return RideShareApi.getGroupDetailFromId(PrefUtils.getUserInfo().getmUserId(), params[0],context);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);

            mProgressDialog.dismiss();

            try {

                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.getString("status").equalsIgnoreCase("success")) {

                    JSONObject jObjResult = new JSONObject(jsonObject.getString("message"));

                    GroupList bean = new GroupList();

                    bean.setId(jObjResult.getString("id"));
                    bean.setGroup_name(jObjResult.getString("group_name"));
                    bean.setGroup_description(jObjResult.getString("group_description"));
                    bean.setCategory_name(jObjResult.getString("name"));
                    bean.setCategory_image(jObjResult.getString("category_image"));
                    bean.setIs_joined(jObjResult.optString("is_join"));
                    bean.setShareLink(jObjResult.optString("share_link"));
                    bean.setCategory_id(jObjResult.optString("category_id"));

                    finish();
                    Intent ii = new Intent(context, GroupDetailActivity.class);
                    ii.putExtra("groupDetail", bean);
                    ii.putExtra("mTag", "Deeplink");
                    ii.putExtra(Constants.intentKey.MyGroup, false);
                    startActivity(ii);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}