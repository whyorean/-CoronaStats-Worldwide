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

package com.aurora.corona.world.model.item;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.aurora.corona.world.GlideApp;
import com.aurora.corona.world.R;
import com.aurora.corona.world.model.ninja.Country;
import com.aurora.corona.world.util.Util;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountryItem extends AbstractItem<CountryItem.ViewHolder> {

    private Country country;
    private String packageName;

    public CountryItem(Country country) {
        this.country = country;
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_country;
    }

    @Override
    public int getType() {
        return R.id.fastadapter_item;
    }

    public static class ViewHolder extends FastItemAdapter.ViewHolder<CountryItem> {
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.line1)
        AppCompatTextView line1;
        @BindView(R.id.line2)
        AppCompatTextView line2;
        @BindView(R.id.line3)
        AppCompatTextView line3;

        private Context context;

        ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
            context = view.getContext();
        }

        @Override
        public void bindView(@NotNull CountryItem item, @NotNull List<?> list) {
            final Country country = item.getCountry();
            line1.setText(country.getCountry());
            line2.setText(StringUtils.joinWith(" \u2022 ",
                    "Today : Reported " + Util.getFormattedString(country.getTodayCases()),
                    "Deaths " + Util.getFormattedString(country.getTodayDeaths())));
            line3.setText(StringUtils.joinWith(" : ",
                    "Last updated",
                    Util.millisToTime(country.getUpdated())));

            GlideApp
                    .with(context)
                    .load(country.getCountryInfo().getFlag())
                    .transition(new DrawableTransitionOptions().crossFade())
                    .transforms(new CenterCrop(), new RoundedCorners(20))
                    .into(img);
        }

        @Override
        public void unbindView(@NotNull CountryItem item) {
            line1.setText(null);
            line2.setText(null);
            line3.setText(null);
        }
    }
}
