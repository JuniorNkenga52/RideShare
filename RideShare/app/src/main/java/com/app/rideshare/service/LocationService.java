package com.app.rideshare.service;

import android.app.IntentService;

import android.content.Intent;
import android.util.Log;


public class LocationService extends IntentService {

	private String TAG = this.getClass().getSimpleName();
	public LocationService() {
		super("Fused Location");
	}
	
	public LocationService(String name) {
		super("Fused Location");
	}

	@Override
	protected void onHandleIntent(Intent intent) {



		Log.w("UpdateLocation Service","N");
			
	}

}
