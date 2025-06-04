package com.example.localist.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.localist.R;
import com.example.localist.adapters.PopularAdapter;
import com.example.localist.models.ItemModel;

import java.util.ArrayList;
public class SearchResultFragment extends Fragment {

    private static final String ARG_RESULTS = "results";
    private ArrayList<ItemModel> results;

    public static SearchResultFragment newInstance(ArrayList<ItemModel> results) {
        SearchResultFragment fragment = new SearchResultFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESULTS, results);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            results = (ArrayList<ItemModel>) getArguments().getSerializable(ARG_RESULTS);
        } else {
            results = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);

        ProgressBar progressBar = view.findViewById(R.id.search_progress);
        TextView noResults = view.findViewById(R.id.search_no_results);
        RecyclerView recyclerView = view.findViewById(R.id.search_results_recycler);

        progressBar.setVisibility(View.VISIBLE);

        if (results != null && !results.isEmpty()) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            recyclerView.setAdapter(new PopularAdapter(getContext(), results));
            recyclerView.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        } else {
            noResults.setVisibility(View.VISIBLE);
        }

        progressBar.setVisibility(View.GONE);

        return view;
    }
}
