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
import androidx.lifecycle.MutableLiveData;

import com.aurora.corona.world.Constants;
import com.aurora.corona.world.model.covid19api.country.Country;
import com.aurora.corona.world.model.ninja.historical.Historical;
import com.aurora.corona.world.model.ninja.historical.HistoricalCombined;
import com.aurora.corona.world.task.NetworkTask;
import com.aurora.corona.world.util.Log;
import com.aurora.corona.world.util.PrefUtil;
import com.aurora.corona.world.util.Util;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class Covid19StatsModel extends AndroidViewModel implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private MutableLiveData<HistoricalCombined> data = new MutableLiveData<>();

    private MutableLiveData<String> error = new MutableLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();

    public Covid19StatsModel(@NonNull Application application) {
        super(application);
        sharedPreferences = Util.getPrefs(application);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData<HistoricalCombined> getConfirmedData() {
        return data;
    }

    public void fetchOnlineData2(String countryISO) {
        disposable.add(Observable.fromCallable(() -> new NetworkTask()
                .get("https://corona.lmao.ninja/v2/historical/" + countryISO))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rawResponse -> {
                    final JsonObject jsonObject = gson.fromJson(rawResponse, JsonObject.class);
                    final JsonObject timeLineObject = jsonObject.get("timeline").getAsJsonObject();

                    final JsonObject casesObject = timeLineObject.get("cases").getAsJsonObject();
                    final JsonObject deathsObject = timeLineObject.get("deaths").getAsJsonObject();
                    final JsonObject recoveredObject = timeLineObject.get("recovered").getAsJsonObject();

                    final HistoricalCombined historicalCombined = new HistoricalCombined();

                    final List<Historical> casesList = new ArrayList<>();
                    final List<Historical> deathsList = new ArrayList<>();
                    final List<Historical> recoveredList = new ArrayList<>();

                    float lastValue = 0;
                    for (String key : casesObject.keySet()) {
                        float currentValue = casesObject.get(key).getAsFloat();
                        casesList.add(new Historical(key, currentValue - lastValue));
                        lastValue = currentValue;
                    }

                    lastValue = 0;
                    for (String key : deathsObject.keySet()) {
                        float currentValue = deathsObject.get(key).getAsFloat();
                        deathsList.add(new Historical(key, currentValue - lastValue));
                        lastValue = currentValue;
                    }

                    lastValue = 0;
                    for (String key : recoveredObject.keySet()) {
                        float currentValue = recoveredObject.get(key).getAsFloat();
                        recoveredList.add(new Historical(key, currentValue - lastValue));
                        lastValue = currentValue;
                    }

                    historicalCombined.setCases(casesList);
                    historicalCombined.setDeaths(deathsList);
                    historicalCombined.setRecovered(recoveredList);

                    data.setValue(historicalCombined);

                }, throwable -> {
                    Log.e(throwable.getMessage());
                    throwable.printStackTrace();
                }));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.PREFERENCE_COVID19_COUNTRY_SELECTED)) {
            final String countryString = PrefUtil.getString(getApplication(), Constants.PREFERENCE_COVID19_COUNTRY_SELECTED);
            final Country country = gson.fromJson(countryString, Country.class);
            fetchOnlineData2(country.getISO2());
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
