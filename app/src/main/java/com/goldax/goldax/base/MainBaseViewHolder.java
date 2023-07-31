package com.goldax.goldax.base;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.ButterKnife;

public abstract class MainBaseViewHolder extends RecyclerView.ViewHolder {

    public MainBaseViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public abstract void bind(int pos, Object obj);
}
