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

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aurora.corona.world.Constants;
import com.aurora.corona.world.model.item.CountryItem;
import com.aurora.corona.world.model.ninja.Country;
import com.aurora.corona.world.task.NetworkTask;
import com.aurora.corona.world.util.Log;
import com.aurora.corona.world.util.PrefUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class NinjaCountriesModel extends AndroidViewModel {

    private Gson gson = new Gson();
    private CompositeDisposable disposable = new CompositeDisposable();
    private MutableLiveData<List<CountryItem>> data = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private Type type = new TypeToken<List<Country>>() {
    }.getType();

    public NinjaCountriesModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public LiveData<List<CountryItem>> getData() {
        return data;
    }

    public void fetchDataOnline() {
        disposable.add(Observable.fromCallable(() -> new NetworkTask()
                .get("https://corona.lmao.ninja/countries?sort=country"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::parseData, throwable -> Log.e(throwable.getMessage())));
    }

    private void parseData(String rawResponse) {
        try {
            final List<Country> countryList = gson.fromJson(rawResponse, type);
            Collections.sort(countryList, (o1, o2) -> o1.getCountry().compareToIgnoreCase(o2.getCountry()));
            populateAsFastItems(countryList);
            saveDataToPreference(countryList);
        } catch (Exception e) {
            error.setValue(e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveDataToPreference(List<Country> countryList) {
        PrefUtil.putString(getApplication(), Constants.PREFERENCE_NINJA_COUNTRIES, gson.toJson(countryList));
        PrefUtil.putBoolean(getApplication(), Constants.PREFERENCE_NINJA_COUNTRIES_AVAILABLE, true);
        PrefUtil.putLong(getApplication(), Constants.PREFERENCE_NINJA_COUNTRIES_LAST_UPDATED, System.currentTimeMillis());
    }

    public void fetchDataFromPreferences() {
        final String rawNinjaCountries = PrefUtil.getString(getApplication(), Constants.PREFERENCE_NINJA_COUNTRIES);
        if (!rawNinjaCountries.isEmpty()) {
            final List<Country> countryList = gson.fromJson(rawNinjaCountries, type);
            if (!countryList.isEmpty()) {
                populateAsFastItems(countryList);
            }
        }
    }

    private void populateAsFastItems(List<Country> countryList) {
        disposable.add(Observable.fromIterable(countryList)
                .subscribeOn(Schedulers.io())
                .map(CountryItem::new)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(countryItems -> data.setValue(countryItems), throwable -> {
                    Log.e(throwable.getMessage());
                }));
    }

    @Override
    protected void onCleared() {
        try {
            disposable.dispose();
        } catch (Exception ignored) {
        }
        super.onCleared();
    }
}
