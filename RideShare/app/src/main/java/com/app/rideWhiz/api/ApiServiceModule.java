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

public class ApiServiceModule {

    //private static final String API_ENDPOINT = "http://ec2-18-220-69-218.us-east-2.compute.amazonaws.com/rideshare/api/";

    //public static final String API_ENDPOINT="http://ec2-13-58-7-10.us-east-2.compute.amazonaws.com/rideshare/api/"; OLD URL

    //public static final String API_ENDPOINT="http://ec2-18-222-137-245.us-east-2.compute.amazonaws.com/rideshare/api/";//OLD 2 URL
    public static final String API_ENDPOINT="https://www.myridewhiz.com/rideshare/api/";//NEW URL
    //http://ec2-18-222-137-245.us-east-2.compute.amazonaws.com/rideshare/

    /*public static final String WEBSOCKET_ENDPOINT="ws://ec2-18-220-69-218.us-east-2.compute.amazonaws.com:8090/ride-share-websocket/php-socket.php";*/
    //public static final String WEBSOCKET_ENDPOINT="ws://www.myridewhiz.com/ride-share-websocket/php-socket.php";
    public static final String WEBSOCKET_ENDPOINT="ws://18.218.151.202:8090/ride-share-websocket/php-socket.php";
    //public static final String WEBSOCKET_ENDPOINT="ws://ec2-18-218-151-202.us-east-2.compute.amazonaws.com:9090" +"/ride-share-websocket/php-socket.php";
    //"ws://18.218.151.202:8090/ride-share-websocket/php-socket.php"
    //public static final String API_ENDPOINT = "http://php.rlogical.com/rideshare/api/";

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
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Apikey", "$2y$10$lDBHZhvyNzWTdsgdgsg4cOivLqQAVTGppmV4yEeggsdtttwilio")
                        .build();
                return chain.proceed(newRequest);
            }
        });
        return okHttpClient;
    }

    public static void initializeSSLContext(Context mContext){
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
    private static  Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private static Retrofit.Builder builder = new Retrofit.Builder().baseUrl(API_ENDPOINT).addConverterFactory(GsonConverterFactory.create(gson));

    public static <S> S createService(Class<S> serviceClass,Context context) {
        Retrofit retrofit = builder.client(okHttpClientTokenHeader(context).build()).build();
        return retrofit.create(serviceClass);
    }

    /*public static <S> S createTokenizedService(Class<S> serviceClass, String token) {
        Retrofit retrofit = builder.client(okHttpClientTokenHeader().build()).build();
        return retrofit.create(serviceClass);
    }*/


}
