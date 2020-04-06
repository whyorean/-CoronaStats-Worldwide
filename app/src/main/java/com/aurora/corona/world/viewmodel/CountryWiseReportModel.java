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

package com.aurora.corona.world.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aurora.corona.world.Constants;
import com.aurora.corona.world.model.covid19api.summary.CountrySummary;
import com.aurora.corona.world.model.item.CountryItem;
import com.aurora.corona.world.util.PrefUtil;
import com.aurora.corona.world.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class CountryWiseReportModel extends AndroidViewModel implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private MutableLiveData<List<CountryItem>> data = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    public CountryWiseReportModel(@NonNull Application application) {
        super(application);
        sharedPreferences = Util.getPrefs(application);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        fetchDataFromPreferences();
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public LiveData<List<CountryItem>> getData() {
        return data;
    }

    public void fetchDataFromPreferences() {
        final String rawStateWiseDate = PrefUtil.getString(getApplication(), Constants.PREFERENCE_COVID19_SUMMARY_COUNTRIES);
        final Type type = new TypeToken<List<CountrySummary>>() {
        }.getType();

       /* if (!rawStateWiseDate.isEmpty()) {
            final List<CountrySummary> countrySummaryList = gson.fromJson(rawStateWiseDate, type);
            if (!countrySummaryList.isEmpty()) {
                Observable.fromIterable(countrySummaryList)
                        .map(CountryItem::new)
                        .toList()
                        .doOnSuccess(countryItems -> data.setValue(countryItems))
                        .doOnError(throwable -> error.setValue(throwable.getMessage()))
                        .subscribe();
            }
        }*/
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.PREFERENCE_STATE_WISE)) {
            fetchDataFromPreferences();
        }
    }

    @Override
    protected void onCleared() {
        try {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        } catch (Exception ignored) {
        }
        super.onCleared();
    }
}
