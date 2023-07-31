package com.goldax.goldax.ui.popup.register;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.goldax.goldax.R;
import com.goldax.goldax.base.MainBaseAdapter;
import com.goldax.goldax.base.MainBaseViewHolder;
import com.goldax.goldax.util.Utils;

import java.util.ArrayList;

public class ImageAdapter extends MainBaseAdapter {
    private static final String TAG = ImageAdapter.class.getSimpleName();

    private ArrayList<String> mImagePaths;
    private View.OnClickListener mItemClickListener;

    public ImageAdapter(ArrayList<String> imagePaths, View.OnClickListener itemClickListener) {
        this.mImagePaths = imagePaths;
        this.mItemClickListener = itemClickListener;
    }

    public void addItemList(String imagePath) {
        int insertIdx = mImagePaths.size();

        mImagePaths.add(insertIdx, imagePath);
        notifyItemInserted(insertIdx);
    }

    public void removeItem(int position) {
        if (mImagePaths.size() > position) {
            mImagePaths.remove(position);
            notifyItemRangeRemoved(position, 1);
        }
    }

    @NonNull
    @Override
    public MainBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_register_image, parent, false);
        ImageViewHolder holder = new ImageViewHolder(view);
        holder.mRemoveBtn.setOnClickListener(mItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainBaseViewHolder holder, int position) {
        String path = mImagePaths.get(position);
        holder.bind(position, path);
    }

    @Override
    public int getItemCount() {
        return Utils.isListEmpty(mImagePaths) ? 0 : mImagePaths.size();
    }
}
