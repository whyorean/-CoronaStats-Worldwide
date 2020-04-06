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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.corona.world.Constants;
import com.aurora.corona.world.R;
import com.aurora.corona.world.model.covid19api.country.Country;
import com.aurora.corona.world.util.PrefUtil;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.gson.Gson;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.listeners.ClickEventHook;
import com.mikepenz.fastadapter.select.SelectExtension;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CountryListItem extends AbstractItem<CountryListItem.ViewHolder> {

    private Country country;
    private boolean checked = false;

    public CountryListItem(Country country) {
        this.country = country;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_two_line_action;
    }

    @NotNull
    @Override
    public ViewHolder getViewHolder(@NotNull View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getType() {
        return R.id.fastadapter_item;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<CountryListItem> {
        @BindView(R.id.checkbox)
        MaterialCheckBox checkbox;
        @BindView(R.id.line1)
        TextView line1;
        @BindView(R.id.line2)
        TextView line2;

        private Context context;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.context = itemView.getContext();
        }

        @Override
        public void bindView(@NotNull CountryListItem item, @NotNull List<?> list) {
            final Country country = item.getCountry();
            final String rawCountry = PrefUtil.getString(context, Constants.PREFERENCE_COVID19_COUNTRY_SELECTED);
            final Country lastCountry = new Gson().fromJson(rawCountry, Country.class);
            line1.setText(country.getCountry());
            line2.setText(country.getISO2());
            if (lastCountry != null)
                checkbox.setChecked(item.isChecked() || country.getSlug().equals(lastCountry.getSlug()));
            checkbox.setClickable(false);
        }

        @Override
        public void unbindView(@NotNull CountryListItem item) {
            line1.setText(null);
            line2.setText(null);
        }
    }

    public static final class CheckBoxClickEvent extends ClickEventHook<CountryListItem> {
        @Nullable
        public View onBind(@NotNull RecyclerView.ViewHolder viewHolder) {
            return viewHolder instanceof ViewHolder
                    ? ((ViewHolder) viewHolder).itemView
                    : null;
        }

        @Override
        public void onClick(@NotNull View view, int position, @NotNull FastAdapter<CountryListItem> fastAdapter, @NotNull CountryListItem item) {
            SelectExtension<CountryListItem> selectExtension = fastAdapter.getExtension(SelectExtension.class);
            if (selectExtension != null) {
                selectExtension.toggleSelection(position);
                item.checked = !item.checked;
            }
        }
    }
}
