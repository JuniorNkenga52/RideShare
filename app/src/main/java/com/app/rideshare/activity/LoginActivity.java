package com.app.rideshare.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.api.ApiServiceModule;
import com.app.rideshare.api.RestApiInterface;
import com.app.rideshare.api.response.SignupResponse;
import com.app.rideshare.notification.GCMRegistrationIntentService;
import com.app.rideshare.utils.PrefUtils;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.utils.TypefaceUtils;
import com.app.rideshare.view.CustomProgressDialog;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private CardView mFacebookCv;
    private CardView mGoogleCv;

    LoginButton loginButton;
    CallbackManager callbackManager;

    private static final int RC_SIGN_IN = 101;
    private GoogleApiClient mGoogleApiClient;

    private Typeface mRobotoMediam;

    private EditText mEmailEt;
    private EditText mPasswordEt;

    private TextView mLoginTv;
    private TextView mForgotPasswordTv;
    private TextView mSignUpTv;


    CustomProgressDialog mProgressDialog;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PrefUtils.initPreference(this);


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {


            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)) {
                    token = intent.getStringExtra("token");
                    Log.d("token", token);
                } else if (intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)) {
                } else {
                }
            }
        };

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
            }
        } else {
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }

        mProgressDialog = new CustomProgressDialog(this);

        mRobotoMediam = TypefaceUtils.getTypefaceRobotoMediam(this);

        mEmailEt = (EditText) findViewById(R.id.username_et);
        mPasswordEt = (EditText) findViewById(R.id.password_et);
        mLoginTv = (TextView) findViewById(R.id.login_tv);
        mForgotPasswordTv = (TextView) findViewById(R.id.forgot_password_tv);
        mSignUpTv = (TextView) findViewById(R.id.signup_tv);

        mEmailEt.setTypeface(mRobotoMediam);
        mPasswordEt.setTypeface(mRobotoMediam);
        mLoginTv.setTypeface(mRobotoMediam);
        mForgotPasswordTv.setTypeface(mRobotoMediam);
        mSignUpTv.setTypeface(mRobotoMediam);

        mSignUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(i);
                finish();
            }
        });

        mFacebookCv = (CardView) findViewById(R.id.facebook_card);
        mGoogleCv = (CardView) findViewById(R.id.google_cv);

        mFacebookCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });

        mGoogleCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("226419699435-664j5a9sfct42n6icr0usefpkhrlld1a.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {
                                    Log.d("email", object.getString("email"));
                                    Log.d("id", object.getString("id"));
                                    Log.d("name", object.getString("name"));

                                    String mEmail = object.getString("email");
                                    String mId = object.getString("id");
                                    String mName = object.getString("name");

                                    try {
                                        String name[] = mName.split(" ");
                                        String mFiratName = name[0];
                                        String mLastName = name[1];

                                        loginfacebookuser(mId, mEmail, mFiratName, mLastName);

                                    } catch (Exception e) {

                                        loginfacebookuser(mId, mEmail, mName, mName);
                                    }

                                } catch (Exception e) {

                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.d("error", "" + error);
            }
        });

        mLoginTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmailEt.getText().toString().isEmpty()) {
                    ToastUtils.showShort(LoginActivity.this, "Please enter mobile number or email");
                } else if (mPasswordEt.getText().toString().isEmpty()) {
                    ToastUtils.showShort(LoginActivity.this, "Please enter password.");
                } else {
                    loginuser(mEmailEt.getText().toString(), mPasswordEt.getText().toString());
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            Log.d("email", acct.getEmail());
            Log.d("id", acct.getId());
            Log.d("name", acct.getDisplayName());
            Log.d("name", acct.getGivenName());

            logingoogle(acct.getId(), acct.getEmail(), acct.getDisplayName(), acct.getGivenName());

        } else {
            Log.d("faile", "faile");
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("connection failed", connectionResult.getErrorMessage());
    }


    private void loginuser(final String mEmail, final String password) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).login(mEmail, password, token).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getmStatus().equals("error")) {
                        PrefUtils.addUserInfo(response.body().getMlist().get(0));
                        PrefUtils.putBoolean("islogin", true);
                        PrefUtils.putBoolean("isFriends", true);

                        PrefUtils.putString("loginwith", "normal");
                        PrefUtils.putString("gemail", mEmail);
                        PrefUtils.putString("gId", password);


                        if (PrefUtils.getUserInfo().getmMobileNo().equals("")) {
                            Intent i = new Intent(getBaseContext(), MobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else if (PrefUtils.getUserInfo().getmIsVerify().equals("0")) {
                            Intent i = new Intent(getBaseContext(), VerifyMobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                            startActivity(i);
                            finish();
                        }
                    } else {
                        ToastUtils.showShort(LoginActivity.this, response.body().getmMessage());
                    }
                } else {

                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }


    private void loginfacebookuser(final String mFacebookId, final  String mEmail, final String mFirstName,final  String mLastName) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).signfacebook(mFacebookId, mEmail, mFirstName, mLastName, token).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getmStatus().equals("error")) {
                        PrefUtils.addUserInfo(response.body().getMlist().get(0));
                        PrefUtils.putBoolean("isFriends", true);
                        PrefUtils.putBoolean("islogin", true);

                        PrefUtils.putString("loginwith", "facebook");
                        PrefUtils.putString("gemail", mEmail);
                        PrefUtils.putString("gId", mFacebookId);
                        PrefUtils.putString("gfname", mFirstName);
                        PrefUtils.putString("glast", mLastName);

                        if (PrefUtils.getUserInfo().getmMobileNo().equals("")) {
                            Intent i = new Intent(getBaseContext(), MobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else if (PrefUtils.getUserInfo().getmIsVerify().equals("0")) {
                            Intent i = new Intent(getBaseContext(), VerifyMobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                            startActivity(i);
                            finish();
                        }

                    } else {
                        ToastUtils.showShort(LoginActivity.this, response.body().getmMessage());
                    }
                } else {

                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }


    private void logingoogle(final String mGoogleId, final String mEmail, final String mFirstName, final String mLastName) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).signGoogleplus(mGoogleId, mEmail, mFirstName, mLastName, token).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getmStatus().equals("error")) {
                        PrefUtils.addUserInfo(response.body().getMlist().get(0));
                        PrefUtils.putBoolean("islogin", true);
                        PrefUtils.putBoolean("isFriends", true);

                        PrefUtils.putString("loginwith", "google");
                        PrefUtils.putString("gemail", mEmail);
                        PrefUtils.putString("gId", mGoogleId);
                        PrefUtils.putString("gfname", mFirstName);
                        PrefUtils.putString("glast", mLastName);

                        if (PrefUtils.getUserInfo().getmMobileNo().equals("")) {
                            Intent i = new Intent(getBaseContext(), MobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else if (PrefUtils.getUserInfo().getmIsVerify().equals("0")) {
                            Intent i = new Intent(getBaseContext(), VerifyMobileNumberActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(getBaseContext(), RideTypeActivity.class);
                            startActivity(i);
                            finish();
                        }
                    } else {
                        ToastUtils.showShort(LoginActivity.this, response.body().getmMessage());
                    }
                } else {

                }
                mProgressDialog.cancel();
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                t.printStackTrace();
                Log.d("error", t.toString());
                mProgressDialog.cancel();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}
