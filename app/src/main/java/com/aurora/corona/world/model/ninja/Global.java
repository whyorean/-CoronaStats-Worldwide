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

package com.aurora.corona.world.model.ninja;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class Global {
    @SerializedName("updated")
    @Expose
    public Long updated;
    @SerializedName("cases")
    @Expose
    public Integer cases;
    @SerializedName("todayCases")
    @Expose
    public Integer todayCases;
    @SerializedName("deaths")
    @Expose
    public Integer deaths;
    @SerializedName("todayDeaths")
    @Expose
    public Integer todayDeaths;
    @SerializedName("recovered")
    @Expose
    public Integer recovered;
    @SerializedName("active")
    @Expose
    public Integer active;
    @SerializedName("critical")
    @Expose
    public Integer critical;
    @SerializedName("casesPerOneMillion")
    @Expose
    public Integer casesPerOneMillion;
    @SerializedName("deathsPerOneMillion")
    @Expose
    public Double deathsPerOneMillion;
    @SerializedName("tests")
    @Expose
    public Integer tests;
    @SerializedName("testsPerOneMillion")
    @Expose
    public Integer testsPerOneMillion;
    @SerializedName("affectedCountries")
    @Expose
    public Integer affectedCountries;
}
