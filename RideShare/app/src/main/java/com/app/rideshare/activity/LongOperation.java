package com.app.rideshare.activity;

import android.os.AsyncTask;
import com.app.rideshare.api.RideShareApi;

public class LongOperation extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {
        return RideShareApi.postApiCall(params[0], params[1]);
    }
}