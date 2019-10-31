package com.app.rideWhiz.api;

import android.content.Context;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.app.rideWhiz.utils.Constants.SERVER_URL;

public class ApiServiceModule {


    private static OkHttpClient.Builder okHttpClient() {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.connectTimeout(60 * 1000, TimeUnit.MILLISECONDS);
        okHttpClient.readTimeout(60 * 1000, TimeUnit.MILLISECONDS);
        return okHttpClient;
    }

    private static OkHttpClient.Builder okHttpClientTokenHeader(Context context) {
        OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
        okHttpClient.connectTimeout(60 * 1000, TimeUnit.MILLISECONDS);
        okHttpClient.readTimeout(60 * 1000, TimeUnit.MILLISECONDS);

        initializeSSLContext(context);
        okHttpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder()
                        .addHeader("Apikey", "$2y$10$lDBHZhvyNzWTdsgdgsg4cOivLqQAVTGppmV4yEeggsdtttwilio")
                        .build();
                return chain.proceed(newRequest);
            }
        });
        return okHttpClient;
    }

    public static void initializeSSLContext(Context mContext) {
        try {
            SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            ProviderInstaller.installIfNeeded(mContext.getApplicationContext());
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(SERVER_URL).addConverterFactory(GsonConverterFactory.create(gson));

    public static <S> S createService(Class<S> serviceClass, Context context) {
        Retrofit retrofit = builder.client(okHttpClientTokenHeader(context).build()).build();
        return retrofit.create(serviceClass);
    }
}
