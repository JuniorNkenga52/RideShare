package com.app.rideWhiz.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.rideWhiz.R;
import com.app.rideWhiz.activity.SignUpActivity;
import com.app.rideWhiz.utils.MessageUtils;
import com.app.rideWhiz.utils.TypefaceUtils;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AddressFragment extends Fragment {

    private static PlacesAutoCompleteAdapter mAdapter;
    private AutoCompleteTextView pickUpText;
    private RecyclerView recyclerView;

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            if (!s.toString().equals("")) {
                mAdapter.getFilter().filter(s.toString());
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_address, container,
                false);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Places.initialize(getActivity(), getResources().getString(R.string.google_places_server_key));
        Typeface mRobotoRegular = TypefaceUtils.getOpenSansRegular(getActivity());

        ImageView imgBack = rootView.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpActivity.mViewPager.setCurrentItem(2);
            }
        });

        TextView txtAddress = rootView.findViewById(R.id.txtAddress);
        txtAddress.setText(SignUpActivity.HomeAddress);

        TextView txtNext = rootView.findViewById(R.id.txtNext);
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

        pickUpText = rootView.findViewById(R.id.autoCompleteTextView);
        pickUpText.setTypeface(mRobotoRegular);
        pickUpText.setDropDownVerticalOffset(100);

        recyclerView = rootView.findViewById(R.id.places_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pickUpText.addTextChangedListener(filterTextWatcher);


        mAdapter = new PlacesAutoCompleteAdapter(getActivity());
        mAdapter.setClickListener(new PlacesAutoCompleteAdapter.ClickListener() {
            @Override
            public void click(Place place) {
                pickUpText.setText(place.getName()+", "+place.getAddress());
                recyclerView.setVisibility(View.GONE);
            }
        });
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        return rootView;
    }


    public static class PlacesAutoCompleteAdapter extends RecyclerView.Adapter<PlacesAutoCompleteAdapter.PredictionHolder> implements Filterable {
        private static final String TAG = "PlacesAutoAdapter";
        private final PlacesClient placesClient;
        private ArrayList<PlaceAutocomplete> mResultList = new ArrayList<>();
        private Context mContext;
        private CharacterStyle STYLE_BOLD;
        private CharacterStyle STYLE_NORMAL;
        private ClickListener clickListener;

        PlacesAutoCompleteAdapter(Context context) {
            mContext = context;
            STYLE_BOLD = new StyleSpan(Typeface.BOLD);
            STYLE_NORMAL = new StyleSpan(Typeface.NORMAL);
            placesClient = Places.createClient(context);
        }

        void setClickListener(ClickListener clickListener) {
            this.clickListener = clickListener;
        }

        /**
         * Returns the filter for the current set of autocomplete results.
         */
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();
                    // Skip the autocomplete query if no constraints are given.
                    if (constraint != null) {
                        // Query the autocomplete API for the (constraint) search string.
                        mResultList = getPredictions(constraint);
                        if (mResultList != null) {
                            // The API successfully returned results.
                            results.values = mResultList;
                            results.count = mResultList.size();
                        }
                    }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        // The API returned at least one result, update the data.
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, "No Results Found", Toast.LENGTH_SHORT).show();
                        // The API did not return any results, invalidate the data set.
                        //notifyDataSetInvalidated();
                    }
                }
            };
        }

        private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {

            final ArrayList<PlaceAutocomplete> resultList = new ArrayList<>();

            // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
            // and once again when the user makes a selection (for example when calling fetchPlace()).
            AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


            // Use the builder to create a FindAutocompletePredictionsRequest.
            FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                    .setSessionToken(token)
                    .setQuery(constraint.toString())
                    .build();

            Task<FindAutocompletePredictionsResponse> autocompletePredictions = placesClient.findAutocompletePredictions(request);

            // This method should have been called off the main UI thread. Block and wait for at most
            // 60s for a result from the API.
            try {
                Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS);
            } catch (ExecutionException | InterruptedException | TimeoutException e) {
                e.printStackTrace();
            }

            if (autocompletePredictions.isSuccessful()) {
                FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictions.getResult();
                if (findAutocompletePredictionsResponse != null)
                    for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                        Log.i(TAG, prediction.getPlaceId());
                        resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(STYLE_NORMAL).toString(), prediction.getFullText(STYLE_BOLD).toString()));
                    }

                return resultList;
            } else {
                return resultList;
            }

        }

        @NonNull
        @Override
        public PredictionHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View convertView = layoutInflater.inflate(R.layout.item_searchlocation_signup, viewGroup, false);
            return new PredictionHolder(convertView);
        }

        @Override
        public void onBindViewHolder(@NonNull PredictionHolder mPredictionHolder, final int i) {
            mPredictionHolder.address.setText(mResultList.get(i).address);
        }

        @Override
        public int getItemCount() {
            return mResultList.size();
        }

        public PlaceAutocomplete getItem(int position) {
            return mResultList.get(position);
        }

        public interface ClickListener {
            void click(Place place);
        }

        public class PredictionHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView address;

            PredictionHolder(View itemView) {
                super(itemView);
                address = itemView.findViewById(R.id.lbl_item_searchlocation_name);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                PlaceAutocomplete item = mResultList.get(getAdapterPosition());
                if (v.getId() == R.id.address_search) {

                    String placeId = String.valueOf(item.placeId);

                    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                    FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                    placesClient.fetchPlace(request).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                        @Override
                        public void onSuccess(FetchPlaceResponse response) {
                            Place place = response.getPlace();
                            clickListener.click(place);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            if (exception instanceof ApiException) {
                                Toast.makeText(mContext, exception.getMessage() + "", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        }

        /**
         * Holder for Places Geo Data Autocomplete API results.
         */
        public class PlaceAutocomplete {

            CharSequence placeId;
            CharSequence address, area;

            PlaceAutocomplete(CharSequence placeId, CharSequence area, CharSequence address) {
                this.placeId = placeId;
                this.area = area;
                this.address = address;
            }

            @Override
            public String toString() {
                return area.toString();
            }
        }
    }
}