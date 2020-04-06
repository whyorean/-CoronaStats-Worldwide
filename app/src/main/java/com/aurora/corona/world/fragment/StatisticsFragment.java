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

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.aurora.corona.world.Constants;
import com.aurora.corona.world.R;
import com.aurora.corona.world.model.covid19api.country.Country;
import com.aurora.corona.world.model.covid19api.stats.CountryStats;
import com.aurora.corona.world.model.covid19api.stats.CountryStatsMerged;
import com.aurora.corona.world.model.covid19api.summary.Global;
import com.aurora.corona.world.sheet.CountrySelectorSheet;
import com.aurora.corona.world.util.PrefUtil;
import com.aurora.corona.world.util.Util;
import com.aurora.corona.world.viewmodel.Covid19StatsModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StatisticsFragment extends Fragment {

    @BindView(R.id.coordinator)
    CoordinatorLayout coordinator;
    @BindView(R.id.chart_pie)
    PieChart chartPie;
    @BindView(R.id.chart_bar_confirmed)
    BarChart chart;
    @BindView(R.id.txt_country_selector)
    AppCompatTextView txtCountrySelector;

    private Gson gson = new Gson();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final String countryString = PrefUtil.getString(requireContext(), Constants.PREFERENCE_COVID19_COUNTRY_SELECTED);
        final Country country = gson.fromJson(countryString, Country.class);
        final Covid19StatsModel model = new ViewModelProvider(this).get(Covid19StatsModel.class);

        model.getConfirmedData().observe(getViewLifecycleOwner(), countryStatsMerged -> {
            updateCountry();
            setupBarChart(countryStatsMerged);
        });

        if (country == null) {
            showCountrySelector();
            Toast.makeText(requireContext(), "Select a country first", Toast.LENGTH_SHORT).show();
        } else {
            txtCountrySelector.setText(StringUtils.joinWith(":",
                    getString(R.string.action_change), country.getCountry()));
            model.fetchOnlineData(country.getSlug());
        }
    }

    @OnClick(R.id.txt_country_selector)
    public void showAllCountryList() {
        showCountrySelector();
    }

    private void showCountrySelector() {
        final FragmentManager fragmentManager = getChildFragmentManager();
        if (fragmentManager.findFragmentByTag(CountrySelectorSheet.TAG) == null) {
            final CountrySelectorSheet sheet = new CountrySelectorSheet();
            sheet.show(fragmentManager, CountrySelectorSheet.TAG);
        }
    }

    private void updateCountry() {
        final String countryString = PrefUtil.getString(requireContext(), Constants.PREFERENCE_COVID19_COUNTRY_SELECTED);
        final Country country = gson.fromJson(countryString, Country.class);
        txtCountrySelector.setText(StringUtils.joinWith(" : ",
                getString(R.string.action_change), country.getCountry()));
    }

    private void setupBarChart(CountryStatsMerged countryStatsMerged) {
        chart.invalidate();
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        final Description description = chart.getDescription();
        description.setEnabled(false);

        final XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setDrawLabels(true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return Util.getDateFromISOInstant(countryStatsMerged.getListConfirmed().get((int) value).getDate());
            }
        });

        chart.getAxisRight().setEnabled(false);

        final YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setDrawAxisLine(false);
        yAxis.setTextColor(Color.WHITE);

        final Legend legend = chart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        legend.setDrawInside(true);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(8f);
        legend.setTextSize(10f);
        legend.setYOffset(12f);
        legend.setTextColor(Color.WHITE);

        final ArrayList<BarEntry> valuesTotal = new ArrayList<>();
        final ArrayList<BarEntry> valuesRecovered = new ArrayList<>();
        final ArrayList<BarEntry> valuesDeaths = new ArrayList<>();

        int i = 0;
        for (CountryStats countryStats : countryStatsMerged.getListConfirmed()) {
            valuesTotal.add(new BarEntry(i++, countryStats.getCases()));
        }

        i = 0;
        for (CountryStats countryStats : countryStatsMerged.getListRecovered()) {
            valuesRecovered.add(new BarEntry(i++, countryStats.getCases()));
        }

        i = 0;
        for (CountryStats countryStats : countryStatsMerged.getListDeath()) {
            valuesDeaths.add(new BarEntry(i++, countryStats.getCases()));
        }

        //Setup Pie chart
        float totalConfirmed = valuesTotal.get(i - 1).getY();
        float totalDeaths = valuesDeaths.get(i - 1).getY();
        float totalRecovered = valuesRecovered.get(i - 1).getY();

        setupPieChart(totalConfirmed, totalRecovered, totalDeaths);

        //Continue with BarChart
        final BarDataSet set1 = new BarDataSet(valuesTotal, "New Cases");
        set1.setColor(getResources().getColor(R.color.colorBlue));
        set1.setValueTextColor(Color.WHITE);

        final BarDataSet set2 = new BarDataSet(valuesRecovered, "Recovered");
        set2.setColor(getResources().getColor(R.color.colorGreen));
        set2.setValueTextColor(Color.WHITE);

        final BarDataSet set3 = new BarDataSet(valuesDeaths, "Deaths");
        set3.setColor(getResources().getColor(R.color.colorRed));
        set3.setValueTextColor(Color.WHITE);

        chart.setData(new BarData(set1, set2, set3));
        chart.animateY(1400, Easing.Linear);
    }

    private void setupPieChart(float totalConfirmed, float totalRecovered, float totalDeaths) {
        chartPie.setUsePercentValues(true);
        chartPie.getDescription().setEnabled(false);
        chartPie.setExtraOffsets(5, 10, 5, 5);

        chartPie.setDragDecelerationFrictionCoef(0.95f);

        chartPie.setDrawHoleEnabled(true);
        chartPie.setHoleColor(Color.TRANSPARENT);

        chartPie.setTransparentCircleColor(Color.WHITE);
        chartPie.setTransparentCircleAlpha(110);

        chartPie.setHoleRadius(10f);
        chartPie.setTransparentCircleRadius(15f);

        chartPie.setDrawCenterText(true);

        chartPie.setRotationAngle(0);
        chartPie.setRotationEnabled(true);
        chartPie.setHighlightPerTapEnabled(true);

        chartPie.animateY(1400, Easing.EaseInOutQuad);

        final Legend legend = chartPie.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setTextColor(Color.WHITE);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);

        final ArrayList<PieEntry> entries = new ArrayList<>();
        final String rawGobal = PrefUtil.getString(requireContext(), Constants.PREFERENCE_COVID19_SUMMARY_GLOBAL);
        final Global global = gson.fromJson(rawGobal, Global.class);

        if (global != null) {
            entries.add(new PieEntry(totalRecovered, "Recovered"));
            entries.add(new PieEntry(totalDeaths, "Deaths"));
            entries.add(new PieEntry(totalConfirmed - (totalRecovered + totalDeaths), "Active"));
        }

        final PieDataSet dataSet = new PieDataSet(entries, "Percentage Stats");

        dataSet.setDrawIcons(true);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        final ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        dataSet.setColors(colors);
        dataSet.setSelectionShift(0f);

        final PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chartPie));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.WHITE);

        chartPie.setData(data);
        chartPie.highlightValues(null);
        chartPie.invalidate();
        chartPie.setEntryLabelColor(Color.WHITE);
        chartPie.setEntryLabelTextSize(12f);
    }
}
