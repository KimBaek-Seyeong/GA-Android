package com.goldax.goldax.base;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class MainBaseAdapter extends RecyclerView.Adapter<MainBaseViewHolder> {
    public void setListener(View.OnClickListener onClickListener) {
    }
}
