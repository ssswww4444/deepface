package au.edu.unimelb.eresearch.happypets.ui.fragments;


import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import au.edu.unimelb.eresearch.happypets.Constants;
import au.edu.unimelb.eresearch.happypets.R;
import au.edu.unimelb.eresearch.happypets.adapters.BreedRVAdapter;
import au.edu.unimelb.eresearch.happypets.model.Breed;
import au.edu.unimelb.eresearch.happypets.utils.Alert;
import au.edu.unimelb.eresearch.happypets.utils.Api;

/**
 * Breed list fragment - shows a list of pet breeds
 */
public class BreedListFragment extends BaseFragment {
    private static final String TAG = "BreedListFragment";

    private SwipeRefreshLayout swipeLayout;

    private BreedRow catBreedRowData;
    private BreedRow dogBreedRowData;

    public BreedListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_breed_list, container, false);

        swipeLayout = view.findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(this::retrieveBreedData);

        initBreedRows(view);
        retrieveBreedData();

        return view;
    }

    private void retrieveBreedData() {
        // Reset previous data (if exists)
        catBreedRowData.breeds = new ArrayList<>();
        dogBreedRowData.breeds = new ArrayList<>();

        Api.shared.getBreedList(new Api.ApiCallback() {
            @Override
            public void pass(JSONObject result) {
                Log.d(TAG, "Retrieved breed information.");
                Objects.requireNonNull(getFActivity()).runOnUiThread(() -> swipeLayout.setRefreshing(false));

                try {
                    JSONArray catData = result.getJSONArray("cat");
                    JSONArray dogData = result.getJSONArray("dog");

                    for (int i = 0; i < catData.length(); i ++) {
                        catBreedRowData.breeds.add(new Breed(catData.getJSONObject(i)));
                    }
                    for (int i = 0; i < dogData.length(); i ++) {
                        dogBreedRowData.breeds.add(new Breed(dogData.getJSONObject(i)));
                    }

                    getFActivity().runOnUiThread(() -> {
                        catBreedRowData.breedRVAdapter.updateData(catBreedRowData.breeds);
                        dogBreedRowData.breedRVAdapter.updateData(dogBreedRowData.breeds);
                    });
                } catch (JSONException e) {
                    Log.d(TAG, "Failed to get cat/dog breeds from API response: " + e.getMessage());
                }
            }

            @Override
            public void fail(Exception e, String description) {
                Log.d(TAG, "Failed to retrieve breed information, error: " + e.getMessage());
                Objects.requireNonNull(getFActivity()).runOnUiThread(() -> swipeLayout.setRefreshing(false));
                Alert.alert(getFActivity(), Constants.ALERT_TITLE_ERROR_OOPS,
                        description, Constants.ALERT_BTN_OK);
            }
        });
    }

    private void initBreedRows(View view) {
        RelativeLayout catBreedRow = view.findViewById(R.id.view_cat_breed);
        RelativeLayout dogBreedRow = view.findViewById(R.id.view_dog_breed);
        RecyclerView catRV = view.findViewById(R.id.rv_cat_breeds);
        RecyclerView dogRV = view.findViewById(R.id.rv_dog_breeds);

        // Initialize breed row data and set adapters
        catBreedRowData = new BreedRow(new ArrayList<>());
        dogBreedRowData = new BreedRow(new ArrayList<>());

        catRV.setAdapter(catBreedRowData.breedRVAdapter);
        dogRV.setAdapter(dogBreedRowData.breedRVAdapter);

        // Hide breed recycler view by default
        catRV.setVisibility(View.GONE);
        dogRV.setVisibility(View.GONE);

        catBreedRow.setOnClickListener(rowView -> {
            if (catBreedRowData.expanded) {
                // Collapse cat breeds
                catBreedRowData.expanded = false;
                catRV.setVisibility(View.GONE);
                ((ImageView)rowView.findViewById(R.id.img_expcol_cat))
                        .setImageResource(android.R.drawable.arrow_down_float);
            } else {
                // Expand cat breeds
                catBreedRowData.expanded = true;
                catRV.setVisibility(View.VISIBLE);
                ((ImageView)rowView.findViewById(R.id.img_expcol_cat))
                        .setImageResource(android.R.drawable.arrow_up_float);
            }
        });
        dogBreedRow.setOnClickListener(rowView -> {
            if (dogBreedRowData.expanded) {
                // Collapse dog breeds
                dogBreedRowData.expanded = false;
                dogRV.setVisibility(View.GONE);
                ((ImageView)rowView.findViewById(R.id.img_expcol_dog))
                        .setImageResource(android.R.drawable.arrow_down_float);
            } else {
                // Expand dog breeds
                dogBreedRowData.expanded = true;
                dogRV.setVisibility(View.VISIBLE);
                ((ImageView)rowView.findViewById(R.id.img_expcol_dog))
                        .setImageResource(android.R.drawable.arrow_up_float);
            }
        });
    }

    private class BreedRow {
        boolean expanded = false;
        ArrayList<Breed> breeds;
        BreedRVAdapter breedRVAdapter;

        BreedRow(ArrayList<Breed> breeds) {
            this.breeds = breeds;
            this.breedRVAdapter = new BreedRVAdapter(this.breeds, getFActivity());
        }
    }
}
