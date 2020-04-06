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
import com.aurora.corona.world.model.covid19api.summary.CountrySummary;
import com.aurora.corona.world.model.covid19api.summary.Covid19Summary;
import com.aurora.corona.world.model.covid19api.summary.Global;
import com.aurora.corona.world.task.NetworkTask;
import com.aurora.corona.world.util.Log;
import com.aurora.corona.world.util.PrefUtil;
import com.google.gson.Gson;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class Covid19SummaryModel extends AndroidViewModel {

    private Gson gson = new Gson();
    private MutableLiveData<Boolean> data = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private CompositeDisposable disposable = new CompositeDisposable();

    public Covid19SummaryModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getData() {
        return data;
    }

    public void fetchOnlineData() {
        disposable.add(Observable.fromCallable(() -> new NetworkTask()
                .get("https://api.covid19api.com/summary"))
                .subscribeOn(Schedulers.io())
                .map(rawJSON -> gson.fromJson(rawJSON, Covid19Summary.class))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::saveDataToPreferences, throwable -> Log.e(throwable.getMessage())));
    }

    private void saveDataToPreferences(Covid19Summary covid19Summary) {
        try {

            final Global global = covid19Summary.getGlobal();
            final List<CountrySummary> countrySummaryList = covid19Summary.getCountries();

            PrefUtil.putString(getApplication(), Constants.PREFERENCE_COVID19_SUMMARY_GLOBAL, gson.toJson(global));
            PrefUtil.putString(getApplication(), Constants.PREFERENCE_COVID19_SUMMARY_COUNTRIES, gson.toJson(countrySummaryList));
            PrefUtil.putLong(getApplication(), Constants.PREFERENCE_COVID19_SUMMARY_LAST_UPDATED, System.currentTimeMillis());
            PrefUtil.putString(getApplication(), Constants.PREFERENCE_COVID19_SUMMARY_GLOBAL_LAST_UPDATED, covid19Summary.getDate());
            PrefUtil.putBoolean(getApplication(), Constants.PREFERENCE_COVID19_SUMMARY_AVAILABLE, true);

            data.setValue(true);
        } catch (Exception e) {
            data.setValue(false);
            error.setValue(e.getMessage());
            e.printStackTrace();
        }
    }
}
