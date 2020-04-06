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
import com.aurora.corona.world.model.casetime.Cases_time_series;
import com.aurora.corona.world.util.PrefUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class DailyReportModel extends AndroidViewModel implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Gson gson = new Gson();
    private MutableLiveData<List<Cases_time_series>> data = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();

    public DailyReportModel(@NonNull Application application) {
        super(application);
        fetchDataFromPreferences();
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public LiveData<List<Cases_time_series>> getData() {
        return data;
    }

    public void fetchDataFromPreferences() {
        final String rawCaseTimeSeries = PrefUtil.getString(getApplication(), Constants.PREFERENCE_CASE_TIME_SERIES);
        final Type type = new TypeToken<List<Cases_time_series>>() {
        }.getType();
        if (!rawCaseTimeSeries.isEmpty()) {
            final List<Cases_time_series> casesTimeSeriesList = gson.fromJson(rawCaseTimeSeries, type);
            if (!casesTimeSeriesList.isEmpty())
                data.setValue(casesTimeSeriesList);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.PREFERENCE_CASE_TIME_SERIES)) {
            fetchDataFromPreferences();
        }
    }
}
