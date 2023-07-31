package com.goldax.goldax.ui.lost.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.goldax.goldax.R;
import com.goldax.goldax.base.MainBaseAdapter;
import com.goldax.goldax.base.MainBaseViewHolder;
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.data.ListResult;
import com.goldax.goldax.ui.home.recycler.DummyViewHolder;
import com.goldax.goldax.util.Utils;

import java.util.ArrayList;

import static com.goldax.goldax.data.DataConst.VIEW_TYPE_DUMMY;
import static com.goldax.goldax.data.DataConst.VIEW_TYPE_ITEM;

public class LostAdapter extends MainBaseAdapter {
    private static final String TAG = LostAdapter.class.getSimpleName();

    private ArrayList<ListResult.ResultData> mItemList;
    private View.OnClickListener mItemClickListener;

    private boolean mFromHome;

    public LostAdapter(ArrayList<ListResult.ResultData> itemList, View.OnClickListener itemClickListener) {
        this.mItemList = itemList;
        this.mItemClickListener = itemClickListener;
    }

    public void setItemList(ArrayList<ListResult.ResultData> list) {
        mItemList = list;

        notifyDataSetChanged();
    }

    public void setFromHome(boolean fromHome) {
        mFromHome = fromHome;
    }

    @NonNull
    @Override
    public MainBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == VIEW_TYPE_DUMMY) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_board_dummy, parent, false);
            DummyViewHolder holder = new DummyViewHolder(view);
            holder.itemView.setOnClickListener(mItemClickListener);
            return holder;
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_board_item, parent, false);
            LostViewHolder holder = new LostViewHolder(view);
            holder.itemView.setOnClickListener(mItemClickListener);
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MainBaseViewHolder holder, int position) {
        if (holder instanceof DummyViewHolder) {
            holder.bind(position, DataConst.FRAGMENT_TYPE.LOST);
        } else {
            ListResult.ResultData path = mItemList.get(position);
            holder.bind(position, path);
        }
    }

    @Override
    public int getItemCount() {
        int size = Utils.isListEmpty(mItemList) ? 0 : mItemList.size();
        return mFromHome ? size + 1 : size;
    }

    @Override
    public int getItemViewType(int position) {
        int lastPos = getItemCount() - 1;
        if (mFromHome && position == lastPos) {
            return VIEW_TYPE_DUMMY;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }
}
