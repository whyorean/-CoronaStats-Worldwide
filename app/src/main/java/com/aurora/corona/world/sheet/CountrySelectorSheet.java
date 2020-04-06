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

package com.aurora.corona.world.sheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.corona.world.Constants;
import com.aurora.corona.world.R;
import com.aurora.corona.world.model.covid19api.country.Country;
import com.aurora.corona.world.model.item.CountryListItem;
import com.aurora.corona.world.util.PrefUtil;
import com.google.gson.reflect.TypeToken;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.select.SelectExtension;

import java.lang.reflect.Type;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CountrySelectorSheet extends BaseBottomSheet {


    public static final String TAG = "COUNTRY_SELECTOR_SHEET";

    @BindView(R.id.recycler)
    RecyclerView recycler;

    private FastItemAdapter<CountryListItem> fastItemAdapter;
    private SelectExtension<CountryListItem> selectExtension;

    @Nullable
    @Override
    public View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_country_selector, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onContentViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecycler();
        fetchAllCountries();
    }

    @OnClick({R.id.btn_positive})
    public void closeSheet() {
        dismissAllowingStateLoss();
    }

    private void setupRecycler() {
        fastItemAdapter = new FastItemAdapter<>();
        selectExtension = new SelectExtension<>(fastItemAdapter);

        fastItemAdapter.addExtension(selectExtension);
        fastItemAdapter.addEventHook(new CountryListItem.CheckBoxClickEvent());
        fastItemAdapter.setOnPreClickListener((view, blacklistItemIAdapter, blacklistItem, position) -> true);

        selectExtension.setSelectable(true);
        selectExtension.setMultiSelect(false);
        selectExtension.setSelectWithItemUpdate(true);
        selectExtension.setSelectionListener((item, selected) -> {
            if (selected) {
                for (CountryListItem selectedItems : selectExtension.getSelectedItems()) {
                    selectedItems.setSelected(false);
                    selectedItems.setChecked(false);
                }
                fastItemAdapter.notifyAdapterDataSetChanged();
                PrefUtil.putString(requireContext(), Constants.PREFERENCE_COVID19_COUNTRY_SELECTED, gson.toJson(item.getCountry()));
                item.setSelected(true);
            }
        });

        recycler.setAdapter(fastItemAdapter);
        recycler.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
    }

    private void fetchAllCountries() {
        final String rawCountryList = PrefUtil.getString(requireContext(), Constants.PREFERENCE_COVID19_COUNTRIES);
        final Type type = new TypeToken<List<Country>>() {
        }.getType();

        final List<Country> countryList = gson.fromJson(rawCountryList, type);

        if (!countryList.isEmpty())
            Observable.fromIterable(countryList)
                    .subscribeOn(Schedulers.io())
                    .map(CountryListItem::new)
                    .toList()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSuccess(localeItems -> fastItemAdapter.add(localeItems))
                    .subscribe();
    }
}
