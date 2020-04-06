/*
 * Corona Stats
 * Copyright (C) 2020, Rahul Kumar Patel <auroraoss.dev@gmail.com>
 *
 * Aurora Store is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 * Corona Stats is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Aurora Store.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aurora.corona.world.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.corona.world.Constants;
import com.aurora.corona.world.R;
import com.aurora.corona.world.RecyclerDataObserver;
import com.aurora.corona.world.model.covid19api.summary.CountrySummary;
import com.aurora.corona.world.model.item.CountryItem;
import com.aurora.corona.world.sheet.CountryWiseSheet;
import com.aurora.corona.world.viewmodel.CountryWiseReportModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.listeners.ItemFilterListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountryListFragment extends Fragment implements ItemFilterListener<CountryItem> {

    @BindView(R.id.txt_title)
    AppCompatTextView txtTitle;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;
    @BindView(R.id.txt_input_search)
    TextInputEditText txtInputSearch;
    @BindView(R.id.action2)
    ImageView action2;

    private Gson gson = new Gson();
    private CountryWiseReportModel model;
    private RecyclerDataObserver dataObserver;
    private FastAdapter<CountryItem> fastAdapter;
    private ItemAdapter<CountryItem> itemAdapter;
    private boolean isDataLoaded = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_country_wise, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupRecycler();
        setupSearchBar();

        model = new ViewModelProvider(this).get(CountryWiseReportModel.class);
        model.getData().observe(getViewLifecycleOwner(), countryItems -> {
            itemAdapter.add(countryItems);
            isDataLoaded = true;
        });
    }

    private void setupSearchBar() {
        action2.setImageDrawable(getResources().getDrawable(R.drawable.ic_cancel));
        action2.setOnClickListener(v -> {
            txtInputSearch.setText("");
        });

        txtInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isDataLoaded)
                    itemAdapter.filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupRecycler() {
        fastAdapter = new FastAdapter<>();
        itemAdapter = new ItemAdapter<>();

        fastAdapter.addAdapter(0, itemAdapter);

        fastAdapter.setOnClickListener((view, itemIAdapter, item, position) -> {
            final FragmentManager fragmentManager = getChildFragmentManager();
            if (fragmentManager.findFragmentByTag(CountryWiseSheet.TAG) == null) {
                CountryWiseSheet sheet = new CountryWiseSheet();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.STRING_EXTRA, gson.toJson(item.getCountrySummary()));
                sheet.setArguments(bundle);
                sheet.show(getChildFragmentManager(), CountryWiseSheet.TAG);
            }
            return false;
        });

        itemAdapter.getItemFilter().setFilterPredicate((genericItem, charSequence) -> {
            final CountrySummary countrySummary = genericItem.getCountrySummary();
            final String query = charSequence.toString().toLowerCase();
            return countrySummary.getCountry().toLowerCase().contains(query);
        });

        itemAdapter.getItemFilter().setItemFilterListener(this);

        //dataObserver = new RecyclerDataObserver(recyclerView, emptyLayout, progressLayout);
        //fastAdapter.registerAdapterDataObserver(dataObserver);

        recycler.setAdapter(fastAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
    }


    @Override
    public void itemsFiltered(@org.jetbrains.annotations.Nullable CharSequence charSequence, @org.jetbrains.annotations.Nullable List<? extends CountryItem> list) {

    }

    @Override
    public void onReset() {

    }
}
