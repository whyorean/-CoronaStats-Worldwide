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

package com.aurora.corona.world.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.aurora.corona.world.Constants;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class Util {

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(
                Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public static StringBuilder millisToTime(long millis) {
        millis = Calendar.getInstance().getTimeInMillis() - millis;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60;
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        String hh = hours >= 1 ? hours + " hr" : "";
        return new StringBuilder()
                .append(hh.isEmpty() ? "" : hh)
                .append(StringUtils.SPACE)
                .append(minutes)
                .append(StringUtils.SPACE)
                .append(minutes >= 1 ? "minutes ago" : "minute ago");
    }

    public static boolean isNinjaCountriesAvailable(Context context) {
        return PrefUtil.getBoolean(context, Constants.PREFERENCE_NINJA_COUNTRIES_AVAILABLE);
    }

    public static boolean isNinjaGlobalAvailable(Context context) {
        return PrefUtil.getBoolean(context, Constants.PREFERENCE_NINJA_GLOBAL_AVAILABLE);
    }

    public static String getTimeFromISOInstant(String isoDate) {
        try {
            String subDate = isoDate.substring(0, isoDate.indexOf('T'));
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            Date date = simpleDateFormat.parse(subDate);
            return Util.millisToTime(date.getTime()).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return isoDate;
        }
    }

    public static String getDateFromISOInstant(String isoDate) {
        try {
            String subDate = isoDate.substring(0, isoDate.indexOf('T'));
            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            Date date = simpleDateFormat.parse(subDate);
            calendar.setTime(date);

            return StringUtils.joinWith("/", calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        } catch (Exception e) {
            e.printStackTrace();
            return isoDate;
        }
    }
}
