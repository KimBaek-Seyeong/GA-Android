package com.goldax.goldax.ui.lost.recycler;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.goldax.goldax.R;
import com.goldax.goldax.base.MainBaseViewHolder;
import com.goldax.goldax.data.ListResult;
import com.goldax.goldax.util.ImageLoader;
import com.goldax.goldax.util.ImageRequest;
import com.goldax.goldax.util.Utils;

import butterknife.BindView;

public class LostViewHolder extends MainBaseViewHolder {
    private static final String TAG = LostViewHolder.class.getSimpleName();

    @BindView(R.id.v_board_item_image_view)
    public ImageView mImage;
    @BindView(R.id.v_board_item_title)
    public TextView mTitle;
    @BindView(R.id.v_board_item_location)
    public TextView mLocation;
    @BindView(R.id.v_board_item_closed_mask)
    public TextView mClosedMask;

    public LostViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(int pos, Object obj) {
        Log.d(TAG, "bind() called. pos: " + pos + " | " + obj);

        if (obj instanceof ListResult.ResultData) {
            ListResult.ResultData item = (ListResult.ResultData) obj;

            String location = item.location;
            if (location != null) {
                String[] locations = location.split(", ");
                if (locations != null && locations.length >= 2) {
                    location = locations[1];
                }
            }

            mTitle.setText(item.title);
            mLocation.setText(location);

            String imageUrl = "";
            if (!TextUtils.isEmpty(item.image)) {
                imageUrl = item.image;
            } else if (!Utils.isListEmpty(item.images) && item.images.get(0) != null) {
                imageUrl = item.images.get(0).path;
            }

            if (!TextUtils.isEmpty(imageUrl)) {
                ImageRequest imageRequest = new ImageRequest.Builder(mImage, imageUrl)
                        .setErrorResId(android.R.drawable.stat_notify_error)
                        .build();
                ImageLoader.loadImage(imageRequest);
            } else {
                mImage.setBackgroundResource(android.R.drawable.stat_notify_error);
            }

            if (item.isClosed) {
                mClosedMask.setText("습득완료");
                mClosedMask.setVisibility(View.VISIBLE);
            } else {
                mClosedMask.setVisibility(View.GONE);
            }

            itemView.setTag(item);
            itemView.setTag(R.attr.key_list_data, item);
            itemView.setTag(R.attr.key_image_pos, pos);
        }
    }

    public void bindDummy(int pos, Object obj) {

    }
}
