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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.aurora.corona.world.Constants;
import com.aurora.corona.world.R;
import com.aurora.corona.world.model.covid19api.summary.CountrySummary;
import com.aurora.corona.world.util.Util;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountryWiseSheet extends BaseBottomSheet {

    public static final String TAG = "STATE_WISE_SHEET";

    @BindView(R.id.txt_all_total)
    TextView txtAllTotal;
    @BindView(R.id.txt_all_active)
    TextView txtAllActive;
    @BindView(R.id.txt_all_cured)
    TextView txtAllCured;
    @BindView(R.id.txt_all_deaths)
    TextView txtAllDeaths;
    @BindView(R.id.txt_title_state)
    AppCompatTextView txtTitleState;
    @BindView(R.id.txt_last_updated)
    AppCompatTextView txtLastUpdated;

    private CountrySummary countrySummary;

    public CountryWiseSheet() {
    }

    @Nullable
    @Override
    public View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_state_wise_detail, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onContentViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            final Bundle bundle = getArguments();
            stringExtra = bundle.getString(Constants.STRING_EXTRA);
            countrySummary = gson.fromJson(stringExtra, CountrySummary.class);
            populateData();
        } else {
            dismissAllowingStateLoss();
        }
    }

    private void populateData() {
        int active = countrySummary.getTotalConfirmed() - (countrySummary.getTotalRecovered() + countrySummary.getTotalDeaths());
        if (active < 0)
            active = countrySummary.getTotalConfirmed();
        txtTitleState.setText(String.valueOf(countrySummary.getCountry()));
        txtLastUpdated.setText(StringUtils.joinWith(" : ", "Last updated", Util.getTimeFromISOInstant(countrySummary.getDate())));
        txtAllTotal.setText(String.valueOf(countrySummary.getTotalConfirmed()));
        txtAllActive.setText(String.valueOf(active));
        txtAllCured.setText(String.valueOf(countrySummary.getTotalRecovered()));
        txtAllDeaths.setText(String.valueOf(countrySummary.getTotalDeaths()));
    }
}
