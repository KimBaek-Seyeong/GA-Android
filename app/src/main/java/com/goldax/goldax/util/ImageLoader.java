package com.goldax.goldax.util;

import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

/**
 * Glide 이미지 요청을 위한 Loader
 */
public class ImageLoader {
    private static final String TAG = ImageLoader.class.getSimpleName();

    /**
     * 이미지 요청
     *
     * @param info ImageRequest
     */
    public static void loadImage(@NonNull final ImageRequest info) {
        if(TextUtils.isEmpty(info.url)){
            Log.d(TAG, "loadImage() called. url is empty");
            return;
        }

        RequestBuilder request = Glide.with(info.view.getContext()).load(info.url).thumbnail(0.2f);
        RequestOptions options = new RequestOptions();

        if (info.errorResId != 0) {
            options.error(info.errorResId);
        }

        Log.d(TAG, "loadImage() called. url: " + info.url);

        request.listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target
                    , boolean isFirstResource) {
                Log.d(TAG, "onLoadFailed() called.");
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target
                    , DataSource dataSource, boolean isFirstResource) {
                Log.d(TAG, "onResourceReady() called.");
                return false;
            }
        });

        if (info.isRound) {
            options.circleCrop();
        } else {
            options.transform(new FitCenter());
        }
        request.apply(options);
        request.into((ImageView) info.view);
    }

    /**
     * 이미지 요청
     *
     * @param filePath String filePath
     */
    public static void loadImage(@NonNull final ImageView targetView, @NonNull final String filePath, boolean isRound) {
        if(TextUtils.isEmpty(filePath)){
            Log.d(TAG, "loadImage() called. filePath is empty");
            return;
        }

        RequestBuilder request = Glide.with(targetView.getContext()).load(filePath).thumbnail(0.2f);
        RequestOptions options = new RequestOptions();

        Log.d(TAG, "loadImage() called. filePath: " + filePath);

        request.listener(new RequestListener() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target target
                    , boolean isFirstResource) {
                Log.d(TAG, "onLoadFailed() called.");
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target
                    , DataSource dataSource, boolean isFirstResource) {
                Log.d(TAG, "onResourceReady() called.");
                return false;
            }
        });

        if (isRound) {
            options.circleCrop();
        } else {
            options.transform(new FitCenter());
        }
        request.apply(options);
        request.into((ImageView) targetView);
    }
}
