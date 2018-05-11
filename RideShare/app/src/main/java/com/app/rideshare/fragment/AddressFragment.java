package com.app.rideshare.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.rideshare.R;
import com.app.rideshare.activity.SignUpActivity;
import com.app.rideshare.model.SearchPlace;
import com.app.rideshare.utils.MessageUtils;
import com.app.rideshare.utils.TypefaceUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class AddressFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    Typeface mRobotoRegular;

    private ImageView imgBack;
    private TextView txtNext;

    public TextView txtAddress;

    AutoCompleteTextView pickUpText;

    static PlaceAutocompleteAdapter mAdapter;

    CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);

    private GoogleApiClient googleApiClient;
    public static final LatLngBounds BOUNDS = new LatLngBounds(
            new LatLng(-0, 0), // southwest
            new LatLng(0, 0)); // northeast
    ArrayList<SearchPlace> mlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_address, container,
                false);

        mRobotoRegular = TypefaceUtils.getOpenSansRegular(getActivity());

        imgBack = (ImageView) rootView.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.mViewPager.setCurrentItem(2);
            }
        });

        txtAddress = (TextView) rootView.findViewById(R.id.txtAddress);
        txtAddress.setText(SignUpActivity.HomeAddress);

        txtNext = (TextView) rootView.findViewById(R.id.txtNext);
        txtNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (pickUpText.getText().toString().isEmpty()) {
                    MessageUtils.showFailureMessage(getActivity(), "Please enter Home Address.");
                } else {
                    SignUpActivity.HomeAddress = pickUpText.getText().toString().trim();

                    SignUpActivity.mViewPager.setCurrentItem(4);
                }
            }
        });

        mlist = new ArrayList<>();

        pickUpText = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView);
        pickUpText.setTypeface(mRobotoRegular);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .build();
        }

        mAdapter = new PlaceAutocompleteAdapter(getActivity(), googleApiClient, BOUNDS, null);
        pickUpText.setAdapter(mAdapter);

        return rootView;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }


    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    public class PlaceAutocompleteAdapter extends ArrayAdapter<AutocompletePrediction> implements Filterable {

        Context context;
        private ArrayList<AutocompletePrediction> mResultList;
        private GoogleApiClient mGoogleApiClients;
        private LatLngBounds mBounds;
        private AutocompleteFilter mPlaceFilter;

        public PlaceAutocompleteAdapter(Context context, GoogleApiClient googleApiClient,
                                        LatLngBounds bounds, AutocompleteFilter filter) {
            super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
            this.mGoogleApiClients = googleApiClient;
            this.context = context;
            mBounds = bounds;

            mPlaceFilter = filter;

        }

        public void setBounds(LatLngBounds bounds) {
            mBounds = bounds;
        }

        @Override
        public int getCount() {
            return mResultList.size();
        }

        @Override
        public AutocompletePrediction getItem(int position) {
            return mResultList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_searchlocation_signup, parent, false);

            final AutocompletePrediction item = getItem(position);


            //TextView lblIcon = (TextView) view.findViewById(R.id.lbl_item_searchlocation);
            TextView lblName = (TextView) view.findViewById(R.id.lbl_item_searchlocation_name);

            //lblIcon.setText("\uf041");
            lblName.setText("" + item.getPrimaryText(STYLE_BOLD) + ", " + item.getSecondaryText(STYLE_BOLD));
            //lblIcon.setTypeface(Config.fontFamily);

            /*lblName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        //pickUpText.setText("" + item.getPrimaryText(STYLE_BOLD) + ", " + item.getSecondaryText(STYLE_BOLD));
                        //pickUpText.setSelection(pickUpText.getText().length());

                        *//*PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, item.getPlaceId());
                        placeResult.setResultCallback(mUpdatePlaceDetailsCallback);*//*

                    } catch (Exception e) {
                    }
                }
            });*/

            return view;

        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    if (constraint != null) {
                        mResultList = getAutocomplete(constraint);
                        if (mResultList != null) {
                            results.values = mResultList;
                            results.count = mResultList.size();
                        }
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {

                        mResultList = (ArrayList<AutocompletePrediction>) results.values;
                        notifyDataSetChanged();

                    } else {
                        // The API did not return any results, invalidate the data set.
                        notifyDataSetInvalidated();
                    }

                }

                @Override
                public CharSequence convertResultToString(Object resultValue) {

                    if (resultValue instanceof AutocompletePrediction) {
                        return ((AutocompletePrediction) resultValue).getFullText(null);
                    } else {
                        return super.convertResultToString(resultValue);
                    }
                }
            };
        }


        private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
            if (googleApiClient.isConnected()) {

                PendingResult<AutocompletePredictionBuffer> results =
                        Places.GeoDataApi
                                .getAutocompletePredictions(googleApiClient, constraint.toString(),
                                        mBounds, mPlaceFilter);

                AutocompletePredictionBuffer autocompletePredictions = results
                        .await(60, TimeUnit.SECONDS);

                final Status status = autocompletePredictions.getStatus();
                if (!status.isSuccess()) {
                    MessageUtils.showFailureMessage(getContext(), "Error contacting API: " + status.toString());

                    autocompletePredictions.release();
                    return null;
                }

                return DataBufferUtils.freezeAndClose(autocompletePredictions);
            }
            return null;
        }


    }
}