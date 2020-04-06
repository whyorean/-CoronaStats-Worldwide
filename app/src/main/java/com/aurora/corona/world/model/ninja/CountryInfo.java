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
public class CountryInfo {
    @SerializedName("_id")
    @Expose
    public Integer id;
    @SerializedName("iso2")
    @Expose
    public String iso2;
    @SerializedName("iso3")
    @Expose
    public String iso3;
    @SerializedName("lat")
    @Expose
    public Double latitude;
    @SerializedName("long")
    @Expose
    public Double longitude;
    @SerializedName("flag")
    @Expose
    public String flag;
}
