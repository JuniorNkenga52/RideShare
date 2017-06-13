package com.app.rideshare.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PrefUtils.initPreference(this);

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
                if(mEmailEt.getText().toString().isEmpty())
                {
                    ToastUtils.showShort(LoginActivity.this,"Please enter mobile number or email");
                }else if (mPasswordEt.getText().toString().isEmpty()){
                    ToastUtils.showShort(LoginActivity.this,"Please enter password.");
                }else{
                loginuser(mEmailEt.getText().toString(),mPasswordEt.getText().toString());
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


    private void loginuser(String mEmail, String password) {
        mProgressDialog.show();
        ApiServiceModule.createService(RestApiInterface.class).login(mEmail, password).enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (!response.body().getmStatus().equals("error")) {
                        PrefUtils.addUserInfo(response.body().getMlist().get(0));
                        PrefUtils.putBoolean("islogin",true);
                        Intent i = new Intent(LoginActivity.this, RideTypeActivity.class);
                        startActivity(i);
                        finish();
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
}
