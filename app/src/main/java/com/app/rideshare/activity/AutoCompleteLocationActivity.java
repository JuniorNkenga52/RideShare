package com.app.rideshare.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.adapter.PlaceAutocompleteAdapter;
import com.app.rideshare.model.SearchPlace;
import com.app.rideshare.utils.ToastUtils;
import com.app.rideshare.utils.TypefaceUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;


public class AutoCompleteLocationActivity extends AppCompatActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    AutoCompleteTextView pickUpText;

    private PlaceAutocompleteAdapter mAdapter;
    private GoogleApiClient googleApiClient;
    public static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(-0, 0), // southwest
            new LatLng(0, 0)); // northeast

    ListView mPlaceList;
    ArrayList<SearchPlace> mlist;

    private ImageView mBackIv;
    int postion=0;

    Typeface mRobotoRegular;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auto_complte_location);

        mRobotoRegular= TypefaceUtils.getTypefaceRobotoMediam(this);

        pickUpText = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        pickUpText.setTypeface(mRobotoRegular);

        mPlaceList=(ListView)findViewById(R.id.place_lv);
        mlist=new ArrayList<>();

        mBackIv=(ImageView)findViewById(R.id.back_iv);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        mAdapter = new PlaceAutocompleteAdapter(this, googleApiClient, BOUNDS, null);
        pickUpText.setAdapter(mAdapter);

        mPlaceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                postion=position;
                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                            .getPlaceById(googleApiClient, mlist.get(position).getmLocationId());
                    placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            }
        });


    }
    public ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(final PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            try {
                Place _place = places.get(0);
                String latitude= ""+_place.getLatLng().latitude;
                String longitude=""+ _place.getLatLng().longitude;

                SearchPlace bean=mlist.get(postion);
                bean.setmLatitude(latitude);
                bean.setmLongitude(longitude);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("location",mlist.get(postion));
                setResult(Activity.RESULT_OK,returnIntent);
                finish();


            }catch (Exception e){}
            places.release();
        }
    };
    public void refreshList(ArrayList<SearchPlace> mArrSearchLocation){
        mlist.clear();
        mlist.addAll(mArrSearchLocation);
        mPlaceList.setAdapter(new SearchLocatioAdapter());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    private void getLocationFromPlaceId(String placeId, ResultCallback<PlaceBuffer> callback) {
        Places.GeoDataApi.getPlaceById(googleApiClient, placeId).setResultCallback(callback);
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }


    public class SearchLocatioAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mlist.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            View row = null;
            LayoutInflater L = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            row = L.inflate(R.layout.item_search_location, null);


            TextView lbl_item_searchlocation_address = (TextView) row.findViewById(R.id.lbl_item_searchlocation_address);
            TextView lbl_item_searchlocation_name = (TextView) row.findViewById(R.id.lbl_item_searchlocation_name);

            lbl_item_searchlocation_address.setText(mlist.get(position).getmAddress());
            lbl_item_searchlocation_name.setText(mlist.get(position).getmArea());

            return row;
        }
    }
}
