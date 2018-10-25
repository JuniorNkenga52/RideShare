package com.app.rideshare.activity;

import android.app.Activity;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.RideShareApi;
import com.app.rideshare.model.Category;
import com.app.rideshare.model.GroupList;
import com.app.rideshare.model.User;
import com.app.rideshare.utils.CommonDialog;
import com.app.rideshare.utils.Constants;
import com.app.rideshare.utils.MessageUtils;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CreateGroupActivity extends AppCompatActivity {

    User mUserBean;

    private TextView txtCreate;

    private EditText txtGroupName, txtGroupDescription;

    private RecyclerView mThemeRecycler;
    ArrayList<Category> mListTheme = new ArrayList<>();

    ThemeAdapter mAdapter;
    private boolean isEditGroupDetail;
    GroupList groupDetailInfo;
    Activity activity;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creategroup);

        Intent intent = getIntent();
        isEditGroupDetail = intent.getBooleanExtra(Constants.intentKey.isEditGroup, false);

        PrefUtils.initPreference(this);
        mUserBean = PrefUtils.getUserInfo();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        activity=this;
        context=this;
        if (isEditGroupDetail) {
            getSupportActionBar().setTitle("Edit Group");
            groupDetailInfo = (GroupList) intent.getSerializableExtra(Constants.intentKey.groupDetail);
        } else
            getSupportActionBar().setTitle("Create Group");

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                if (isEditGroupDetail){
                    Intent ii = new Intent(activity, GroupDetailActivity.class);
                    ii.putExtra("groupDetail", groupDetailInfo);
                    ii.putExtra("mTag", "EditGroup");
                    ii.putExtra(Constants.intentKey.MyGroup, true);
                    startActivity(ii);
                    //startActivity(new Intent(activity,GroupDetailActivity.class));
                    finish();
                }

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

        txtGroupName = (EditText) findViewById(R.id.txtGroupName);
        txtGroupDescription = (EditText) findViewById(R.id.txtGroupDescription);

        mThemeRecycler = (RecyclerView) findViewById(R.id.mThemeRecycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mThemeRecycler.setLayoutManager(layoutManager);

        new AsyncAllTheme().execute();

        txtCreate = (TextView) findViewById(R.id.txtCreate);
        txtCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String themeId = "";

                for (int i = 0; i < mListTheme.size(); i++) {
                    if (mListTheme.get(i).isSelect()) {
                        themeId = mListTheme.get(i).getId();
                        break;
                    }
                }

                if (txtGroupName.getText().toString().isEmpty()) {
                    MessageUtils.showFailureMessage(getApplicationContext(), "Please enter group name.");
                } else if (txtGroupDescription.getText().toString().isEmpty()) {
                    MessageUtils.showFailureMessage(getApplicationContext(), "Please enter description.");
                } else if (themeId.isEmpty()) {
                    MessageUtils.showFailureMessage(getApplicationContext(), "Please choose theme.");
                } else {
                    new AsyncCreateGroup(themeId,
                            txtGroupName.getText().toString().trim(),
                            txtGroupDescription.getText().toString().trim()).execute();
                }
            }
        });

        setData();
    }

    private void setData() {
        if (isEditGroupDetail && groupDetailInfo != null) {
            txtGroupDescription.setText(groupDetailInfo.getGroup_description());
            txtGroupName.setText(groupDetailInfo.getGroup_name());
            txtCreate.setText("Update");
        } else {
            txtCreate.setText("Create");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent ii = new Intent(activity, GroupDetailActivity.class);
        ii.putExtra("groupDetail", groupDetailInfo);
        ii.putExtra("mTag", "EditGroup");
        ii.putExtra(Constants.intentKey.MyGroup, true);
        finish();*/
        if (isEditGroupDetail){
            Intent ii = new Intent(activity, GroupDetailActivity.class);
            ii.putExtra("groupDetail", groupDetailInfo);
            ii.putExtra("mTag", "EditGroup");
            ii.putExtra(Constants.intentKey.MyGroup, true);
            startActivity(ii);
            //startActivity(new Intent(activity,GroupDetailActivity.class));
            finish();
        }
//        RideShareApp.mHomeTabPos = 3;
//        Intent i = new Intent(CreateGroupActivity.this, HomeNewActivity.class);
//        startActivity(i);
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//        finish();

    }

    public class AsyncAllTheme extends AsyncTask<Object, Integer, Object> {

        CustomProgressDialog mProgressDialog;

        public AsyncAllTheme() {

            mProgressDialog = new CustomProgressDialog(CreateGroupActivity.this);
            mProgressDialog.show();
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        public Object doInBackground(Object... params) {
            try {
                return RideShareApi.category(context);
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

                        Category bean = new Category();

                        bean.setId(jObjResult.getString("id"));
                        bean.setName(jObjResult.getString("name"));
                        bean.setImage(jObjResult.getString("image"));
                        bean.setThumb(jObjResult.getString("thumb"));
                        bean.setStatus(jObjResult.getString("status"));

                        if (isEditGroupDetail && groupDetailInfo != null) {
                            if (jObjResult.getString("id").equalsIgnoreCase(groupDetailInfo.getCategory_id()))
                                bean.setSelect(true);
                            else {
                                bean.setSelect(false);
                            }
                        } else {
                            bean.setSelect(false);
                        }
                        mListTheme.add(bean);

                    }

                    mAdapter = new ThemeAdapter(getApplicationContext(), mListTheme);

                    mThemeRecycler.setAdapter(mAdapter);

                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ItemRowHolder> {

        private List<Category> dataList;
        private Context mContext;


        public ThemeAdapter(Context context, List<Category> dataList) {
            this.dataList = dataList;
            this.mContext = context;
        }

        @Override
        public ItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_theme, null);
            return new ItemRowHolder(v);
        }

        @Override
        public void onBindViewHolder(final ItemRowHolder itemRowHolder, final int position) {

            final Category bean = dataList.get(position);

            //MyImageUtils.loadImagePicasso(mContext, (itemRowHolder).imageView, bean.getFlower_image(), R.drawable.flower_unselect_bg);
            Picasso.with(mContext).load(bean.getThumb())
                    .resize(300, 300).centerCrop()
                    .error(R.drawable.user_icon).into((itemRowHolder).imageView);

            if (bean.isSelect) {
                (itemRowHolder).imageView.setBorderColor(mContext.getResources().getColor(R.color.colorAccent));
                (itemRowHolder).imageView.setBorderWidth(mContext.getResources().getDimension(R.dimen._5sdp));

            } else {
                (itemRowHolder).imageView.setBorderWidth(0);
            }

            itemRowHolder.llTheme.setTag(itemRowHolder.llTheme);
            itemRowHolder.llTheme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (dataList.get(position).isSelect()) {
                        for (int i = 0; i < dataList.size(); i++) {
                            dataList.get(i).setSelect(false);
                        }
                    } else {
                        for (int i = 0; i < dataList.size(); i++) {
                            dataList.get(i).setSelect(false);
                        }

                        dataList.get(position).setSelect(true);
                    }


                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return (null != dataList ? dataList.size() : 0);
            //return 10;
        }

        public class ItemRowHolder extends RecyclerView.ViewHolder {

            CircularImageView imageView;
            LinearLayout llTheme;

            public ItemRowHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.imgPhoto);
                llTheme = view.findViewById(R.id.llTheme);
            }
        }
    }

    private class AsyncCreateGroup extends AsyncTask<Objects, Void, String> {

        CustomProgressDialog mProgressDialog;
        String categoryId, groupName, groupDescription;

        public AsyncCreateGroup(String categoryId, String groupName, String groupDescription) {

            mProgressDialog = new CustomProgressDialog(CreateGroupActivity.this);
            mProgressDialog.show();

            this.categoryId = categoryId;
            this.groupName = groupName;
            this.groupDescription = groupDescription;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(Objects... param) {
            try {
                if (isEditGroupDetail) {
                    return RideShareApi.editGroup(mUserBean.getmUserId(),
                            groupDetailInfo.getId(),
                            categoryId,
                            groupName, groupDescription,context);
                } else {
                    return RideShareApi.createGroup(mUserBean.getmUserId(),
                            categoryId,
                            groupName, groupDescription,context);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {

            mProgressDialog.dismiss();
            Log.e("AsyncCreateGroup", "onPostExecute: result >>" + result);

            try {

                if (result != null) {

                    JSONObject jObj = new JSONObject(result);

                    if (jObj.getString("status").equals("success")) {
                        if (isEditGroupDetail) {
                            MessageUtils.showSuccessMessage(getApplicationContext(), "Group info updated successfully.");
                        } else {
                            MessageUtils.showSuccessMessage(getApplicationContext(), "Group Created.");
                        }
//                        RideShareApp.mHomeTabPos = 0;
//                        Intent i = new Intent(CreateGroupActivity.this, HomeNewActivity.class);
//                        startActivity(i);
//                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//                        finish();

                        Constants.isGroupDataUpdated = true;

                        if (isEditGroupDetail) {
                            finish();
                        } else {
                            JSONObject resultJsonObject = jObj.optJSONObject("result");
                            if (resultJsonObject != null){
                                CommonDialog.shareInviteLinkDialog(CreateGroupActivity.this, resultJsonObject.optString("share_link"),0);
                                PrefUtils.putString("isBlank", "false");
                            }
                        }
                    } else {
                        MessageUtils.showFailureMessage(getApplicationContext(), "The Group Name field must contain a unique value.");
                    }
                } else {
                    MessageUtils.showPleaseTryAgain(getApplicationContext());
                }
            } catch (Exception e) {
                MessageUtils.showPleaseTryAgain(getApplicationContext());
            }
        }
    }

}