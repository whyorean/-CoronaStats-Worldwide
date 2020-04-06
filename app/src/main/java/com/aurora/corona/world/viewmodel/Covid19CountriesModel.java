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
import com.aurora.corona.world.model.covid19api.country.Country;
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

public class Covid19CountriesModel extends AndroidViewModel {

    private Gson gson = new Gson();
    private MutableLiveData<Boolean> data = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();

    public Covid19CountriesModel(@NonNull Application application) {
        super(application);
        fetchAllCountries();
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getData() {
        return data;
    }

    public void fetchAllCountries() {
        disposable.add(Observable.fromCallable(() -> new NetworkTask()
                .get("https://api.covid19api.com/countries"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::saveDataToPreferences, throwable -> Log.e(throwable.getMessage())));
    }

    private void saveDataToPreferences(String rawResponse) {
        try {
            final Type type = new TypeToken<List<Country>>() {
            }.getType();
            final List<Country> countryList = gson.fromJson(rawResponse, type);
            Collections.sort(countryList,(o1, o2) -> o1.getCountry().compareToIgnoreCase(o2.getCountry()));
            PrefUtil.putString(getApplication(), Constants.PREFERENCE_COVID19_COUNTRIES, gson.toJson(countryList));
            PrefUtil.putBoolean(getApplication(), Constants.PREFERENCE_COVID19_COUNTRIES_AVAILABLE, true);
            data.setValue(true);
        } catch (Exception e) {
            data.setValue(false);
            error.setValue(e.getMessage());
            e.printStackTrace();
        }
    }
}
