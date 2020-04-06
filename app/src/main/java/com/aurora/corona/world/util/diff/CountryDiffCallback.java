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

package com.aurora.corona.world.util.diff;

import com.aurora.corona.world.model.item.CountryItem;
import com.mikepenz.fastadapter.diff.DiffCallback;

import org.jetbrains.annotations.Nullable;

public class CountryDiffCallback implements DiffCallback<CountryItem> {

    @Override
    public boolean areContentsTheSame(CountryItem oldItem, CountryItem newItem) {
        return oldItem.getCountry().getUpdated().equals(newItem.getCountry().getUpdated());
    }

    @Override
    public boolean areItemsTheSame(CountryItem oldItem, CountryItem newItem) {
        return oldItem.getCountry().getUpdated().equals(newItem.getCountry().getUpdated());
    }

    @Nullable
    @Override
    public Object getChangePayload(CountryItem oldItem, int oldPosition, CountryItem newItem, int newPosition) {
        return null;
    }
}