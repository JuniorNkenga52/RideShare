package com.app.rideWhiz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.adapter.PlacesAutoCompleteAdapter;
import com.app.rideWhiz.model.SearchPlace;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

public class PlaceSerachActivity extends AppCompatActivity implements PlacesAutoCompleteAdapter.ClickListener {

    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private RecyclerView recyclerView;
    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (recyclerView.getVisibility() == View.GONE) {
                    recyclerView.setVisibility(View.VISIBLE);
                }
            } else {
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_serach);
        Places.initialize(this, getResources().getString(R.string.google_places_server_key));

        ImageView back_iv = findViewById(R.id.back_iv);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = findViewById(R.id.places_recycler_view);
        ((AutoCompleteTextView) findViewById(R.id.place_search)).addTextChangedListener(filterTextWatcher);

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAutoCompleteAdapter.setClickListener(this);
        recyclerView.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.notifyDataSetChanged();

    }

    @Override
    public void click(Place place) {
        if (place == null) {
            return;
        }
        try {
            String latitude = "" + place.getLatLng().latitude;
            String longitude = "" + place.getLatLng().longitude;

            SearchPlace bean = new SearchPlace();
            bean.setmAddress(place.getAddress());
            bean.setmArea(place.getName());
            bean.setmLatitude(latitude);
            bean.setmLongitude(longitude);
            bean.setmLocationId(place.getId());

            Intent returnIntent = new Intent();
            returnIntent.putExtra("location", bean);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
