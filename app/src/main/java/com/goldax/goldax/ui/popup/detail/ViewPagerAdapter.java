package com.goldax.goldax.ui.popup.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.goldax.goldax.R;
import com.goldax.goldax.data.ImageData;
import com.goldax.goldax.util.ImageLoader;
import com.goldax.goldax.util.ImageRequest;
import com.goldax.goldax.util.Utils;

import java.util.ArrayList;

public class ViewPagerAdapter extends PagerAdapter {
    private static final String TAG = ViewPagerAdapter.class.getSimpleName();

    private Context mContext;
    private ArrayList<ImageData> mImageList;

    public ViewPagerAdapter(Context mContext, ArrayList<ImageData> imageList) {
        this.mContext = mContext;
        this.mImageList = imageList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.i_viewpager_layout, null);

        ImageView imageView = view.findViewById(R.id.i_viewpager_layout_image);
        ImageRequest imageRequest = new ImageRequest.Builder(imageView, mImageList.get(position).path)
                .setErrorResId(android.R.drawable.stat_notify_error) // default 이미지로 변경 필요
                .build();
        ImageLoader.loadImage(imageRequest);

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return Utils.isListEmpty(mImageList) ? 0 : mImageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View) object);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
