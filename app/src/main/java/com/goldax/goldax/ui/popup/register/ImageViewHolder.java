package com.goldax.goldax.ui.popup.register;

import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.goldax.goldax.R;
import com.goldax.goldax.application.ApplicationController;
import com.goldax.goldax.base.MainBaseViewHolder;

import butterknife.BindView;

public class ImageViewHolder extends MainBaseViewHolder {
    private static final String TAG = ImageViewHolder.class.getSimpleName();

    @BindView(R.id.v_view)
    public View mBgView;
    @BindView(R.id.v_remove_btn)
    public ImageView mRemoveBtn;

    public ImageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(int pos, Object obj) {
        Log.d(TAG, "bind() called. pos: " + pos + " | " + obj);

        if (obj instanceof String) {
            BitmapDrawable bDrawable = new BitmapDrawable(ApplicationController.getInstance().getResources(), (String) obj);
            if (bDrawable != null) {
                mBgView.setBackground(bDrawable);
            }

            itemView.setTag(R.attr.key_image_path, (String) obj);
            itemView.setTag(R.attr.key_image_pos, pos);

            mRemoveBtn.setTag(R.attr.key_image_path, (String) obj);
            mRemoveBtn.setTag(R.attr.key_image_pos, pos);
        }
    }

}
