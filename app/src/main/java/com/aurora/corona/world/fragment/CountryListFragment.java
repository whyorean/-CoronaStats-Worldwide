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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aurora.corona.world.Constants;
import com.aurora.corona.world.R;
import com.aurora.corona.world.RecyclerDataObserver;
import com.aurora.corona.world.model.item.CountryItem;
import com.aurora.corona.world.model.ninja.Country;
import com.aurora.corona.world.sheet.CountryDetailSheet;
import com.aurora.corona.world.util.Util;
import com.aurora.corona.world.util.diff.CountryDiffCallback;
import com.aurora.corona.world.viewmodel.NinjaCountriesModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.diff.FastAdapterDiffUtil;
import com.mikepenz.fastadapter.listeners.ItemFilterListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountryListFragment extends Fragment implements ItemFilterListener<CountryItem> {

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
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

        final NinjaCountriesModel model = new ViewModelProvider(this).get(NinjaCountriesModel.class);
        model.getData().observe(getViewLifecycleOwner(), countryItems -> {
            dispatchAppsToAdapter(countryItems);
            isDataLoaded = true;
            swipeLayout.setRefreshing(false);
        });

        if (Util.isNinjaCountriesAvailable(requireContext())) {
            model.fetchDataFromPreferences();
        } else {
            model.fetchDataOnline();
        }

        swipeLayout.setOnRefreshListener(model::fetchDataOnline);
    }

    @Override
    public void onPause() {
        swipeLayout.setRefreshing(false);
        super.onPause();
    }

    private void dispatchAppsToAdapter(List<CountryItem> updatesItems) {
        final FastAdapterDiffUtil fastAdapterDiffUtil = FastAdapterDiffUtil.INSTANCE;
        final CountryDiffCallback diffCallback = new CountryDiffCallback();
        final DiffUtil.DiffResult diffResult = fastAdapterDiffUtil.calculateDiff(itemAdapter, updatesItems, diffCallback);
        fastAdapterDiffUtil.set(itemAdapter, diffResult);
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
            if (fragmentManager.findFragmentByTag(CountryDetailSheet.TAG) == null) {
                CountryDetailSheet sheet = new CountryDetailSheet();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.STRING_EXTRA, gson.toJson(item.getCountry()));
                sheet.setArguments(bundle);
                sheet.show(getChildFragmentManager(), CountryDetailSheet.TAG);
            }
            return false;
        });

        itemAdapter.getItemFilter().setFilterPredicate((genericItem, charSequence) -> {
            final Country country = genericItem.getCountry();
            final String query = charSequence.toString().toLowerCase();
            return country.getCountry().toLowerCase().contains(query);
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
