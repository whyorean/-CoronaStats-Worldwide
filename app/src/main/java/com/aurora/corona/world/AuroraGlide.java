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

package com.aurora.corona.world;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;

import java.io.InputStream;

import okhttp3.OkHttpClient;

import static com.bumptech.glide.load.DecodeFormat.PREFER_ARGB_8888;

@GlideModule
public class AuroraGlide extends AppGlideModule {

    private static RequestOptions requestOptions(Context context) {
        return new RequestOptions()
                .signature(new ObjectKey(System.currentTimeMillis() / (24 * 60 * 60 * 1000)))
                .centerCrop()
                .encodeFormat(Bitmap.CompressFormat.PNG)
                .encodeQuality(100)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .format(PREFER_ARGB_8888)
                .skipMemoryCache(false);
    }

    private static OkHttpClient getOkHttpClient(Context context) {
        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        return builder.build();
    }

    @Override
    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
        int memoryCacheSizeBytes = 1024 * 1024 * 50;
        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, memoryCacheSizeBytes));
        builder.setDefaultRequestOptions(requestOptions(context));
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        final OkHttpClient okHttpClient = getOkHttpClient(context);
        final OkHttpUrlLoader.Factory okHttpUrlLoader = new OkHttpUrlLoader.Factory(okHttpClient);
        registry.replace(GlideUrl.class, InputStream.class, okHttpUrlLoader);
    }
}
