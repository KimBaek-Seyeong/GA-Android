package com.goldax.goldax.ui.home.recycler;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.goldax.goldax.R;
import com.goldax.goldax.base.MainBaseViewHolder;

import butterknife.BindView;

public class DummyViewHolder extends MainBaseViewHolder {
    private static final String TAG = DummyViewHolder.class.getSimpleName();

    @BindView(R.id.v_board_dummy_text)
    public TextView mText;
    @BindView(R.id.v_board_dummy_icon)
    public TextView mIcon;

    public DummyViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(int pos, Object obj) {
        itemView.setTag(R.attr.key_landing_type, obj);
        itemView.setTag(R.attr.key_image_pos, pos);
    }
}
