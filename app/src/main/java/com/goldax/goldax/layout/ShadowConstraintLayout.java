package com.goldax.goldax.layout;

import android.content.Context;
import android.util.AttributeSet;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.goldax.goldax.R;

public class ShadowConstraintLayout extends ConstraintLayout {
    public ShadowConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        setBackground(context.getDrawable(R.drawable.box_border_bg));
    }
}
