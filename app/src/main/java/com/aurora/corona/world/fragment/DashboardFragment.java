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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aurora.corona.world.Constants;
import com.aurora.corona.world.R;
import com.aurora.corona.world.model.ninja.Global;
import com.aurora.corona.world.util.PrefUtil;
import com.aurora.corona.world.util.Util;
import com.aurora.corona.world.viewmodel.NinjaGlobalModel;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.txt_today_total)
    TextView txtNewCases;
    @BindView(R.id.txt_today_deaths)
    TextView txtDeaths;
    @BindView(R.id.txt_all_total)
    TextView txtAllTotal;
    @BindView(R.id.txt_all_active)
    TextView txtAllActive;
    @BindView(R.id.txt_all_cured)
    TextView txtAllCured;
    @BindView(R.id.txt_all_deaths)
    TextView txtAllDeaths;
    @BindView(R.id.layout_bottom)
    ConstraintLayout layoutBottom;
    @BindView(R.id.txt_today_last_updated)
    AppCompatTextView txtTodayLastUpdated;
    @BindView(R.id.txt_critical_cases)
    TextView txtCriticalCases;
    @BindView(R.id.txt_affect_countries)
    TextView txtAffectCountries;

    private Gson gson = new Gson();
    private SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = Util.getPrefs(requireContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final NinjaGlobalModel ninjaGlobalModel = new ViewModelProvider(this).get(NinjaGlobalModel.class);
        ninjaGlobalModel.getData().observe(getViewLifecycleOwner(), result -> {
            Toast.makeText(requireContext(), "Database Updated", Toast.LENGTH_SHORT).show();
            swipeLayout.setRefreshing(false);
        });

        ninjaGlobalModel.getError().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(requireContext(), "Failed to retrieve new data", Toast.LENGTH_SHORT).show();
            swipeLayout.setRefreshing(false);
        });

        if (Util.isNinjaGlobalAvailable(requireContext())) {
            updateDashboardData();
        } else {
            ninjaGlobalModel.fetchOnlineData();
        }

        swipeLayout.setOnRefreshListener(ninjaGlobalModel::fetchOnlineData);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case Constants.PREFERENCE_NINJA2_GLOBAL_LAST_UPDATED:
                updateDashboardData();
                break;
        }
    }

    @Override
    public void onPause() {
        swipeLayout.setRefreshing(false);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        try {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        } catch (Exception ignored) {

        }
        super.onDestroy();
    }

    private void updateDashboardData() {

        final String rawNinjaGlobal = PrefUtil.getString(requireContext(), Constants.PREFERENCE_NINJA2_GLOBAL);
        final Global global = gson.fromJson(rawNinjaGlobal, Global.class);
        
        txtTodayLastUpdated.setText(StringUtils.joinWith(" : ", "Last updated", Util.millisToTime(global.getUpdated())));
        txtNewCases.setText(Util.getFormattedString(global.getTodayCases()));
        txtDeaths.setText(Util.getFormattedString(global.getTodayDeaths()));
        txtAllTotal.setText(Util.getFormattedString(global.getCases()));
        txtAllActive.setText(Util.getFormattedString(global.getActive()));
        txtAllCured.setText(Util.getFormattedString(global.getRecovered()));
        txtAllDeaths.setText(Util.getFormattedString(global.getDeaths()));
        txtCriticalCases.setText(Util.getFormattedString(global.getCritical()));
        txtAffectCountries.setText(Util.getFormattedString(global.getAffectedCountries()));
    }
}
