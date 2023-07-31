package com.goldax.goldax.layout;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.goldax.goldax.R;
import com.goldax.goldax.util.ImageLoader;

public class CircleView extends ConstraintLayout {

    protected Context mContext;
    public ImageView mIconView;

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    protected void init(Context context) {
        mContext = context;

        setFocusable(true);
        setFocusableInTouchMode(true);
        setBackgroundResource(R.drawable.circle_shape);

        mIconView = new ImageView(mContext);
        mIconView.setScaleType(ImageView.ScaleType.FIT_XY);
        ConstraintLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.startToStart = getId();
        layoutParams.endToEnd = getId();
        layoutParams.topToTop = getId();
        layoutParams.bottomToBottom = getId();

        addView(mIconView, layoutParams);
    }

    public void setIcon(int resource) {
        setBackground(null);
        mIconView.setBackground(null);

        mIconView.setBackgroundResource(resource);
    }

    public void setIcon(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            setBackground(null);
            mIconView.setBackground(null);

            ImageLoader.loadImage(mIconView, filePath, true);
        }
    }
}
