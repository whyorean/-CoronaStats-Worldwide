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
import com.aurora.corona.world.model.covid19api.stats.CountryStats;
import com.aurora.corona.world.model.covid19api.stats.CountryStatsMerged;
import com.aurora.corona.world.task.NetworkTask;
import com.aurora.corona.world.util.PrefUtil;
import com.aurora.corona.world.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class Covid19StatsModel extends AndroidViewModel implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;
    private MutableLiveData<CountryStatsMerged> data = new MutableLiveData<>();

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

    public MutableLiveData<CountryStatsMerged> getConfirmedData() {
        return data;
    }

    public void fetchOnlineData(String countrySlug) {

        final Observable<String> ob1 = Observable.fromCallable(() -> new NetworkTask()
                .get("https://api.covid19api.com/country/" + countrySlug + "/status/confirmed/live"))
                .subscribeOn(Schedulers.io());

        final Observable<String> ob2 = Observable.fromCallable(() -> new NetworkTask()
                .get("https://api.covid19api.com/country/" + countrySlug + "/status/recovered/live"))
                .subscribeOn(Schedulers.io());

        final Observable<String> ob3 = Observable.fromCallable(() -> new NetworkTask()
                .get("https://api.covid19api.com/country/" + countrySlug + "/status/deaths/live"))
                .subscribeOn(Schedulers.io());

        disposable.add(Observable.merge(ob1, ob2, ob3)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rawResponseList -> {
                    final CountryStatsMerged countryStatsMerged = new CountryStatsMerged();
                    final Type type = new TypeToken<List<CountryStats>>() {
                    }.getType();

                    for (String rawResponse : rawResponseList) {
                        List<CountryStats> countryStatsList = gson.fromJson(rawResponse, type);

                        switch (countryStatsList.get(0).getStatus()) {
                            case "confirmed":
                                countryStatsMerged.setListConfirmed(countryStatsList);
                                break;
                            case "recovered":
                                countryStatsMerged.setListRecovered(countryStatsList);
                                break;
                            case "deaths":
                                countryStatsMerged.setListDeath(countryStatsList);
                                break;
                        }
                    }
                    countryStatsMerged.setSize(countryStatsMerged.getListConfirmed().size());
                    data.setValue(countryStatsMerged);
                }));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(Constants.PREFERENCE_COVID19_COUNTRY_SELECTED)) {
            final String countryString = PrefUtil.getString(getApplication(), Constants.PREFERENCE_COVID19_COUNTRY_SELECTED);
            final Country country = gson.fromJson(countryString, Country.class);
            fetchOnlineData(country.getSlug());
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
