package com.goldax.goldax.ui.chat.recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.goldax.goldax.R;
import com.goldax.goldax.base.MainBaseAdapter;
import com.goldax.goldax.base.MainBaseViewHolder;
import com.goldax.goldax.util.Utils;

import java.util.ArrayList;

public class ChatAdapter extends MainBaseAdapter {
    private static final String TAG = ChatAdapter.class.getSimpleName();

    private ArrayList<ChatRoomData> mChatList;
    private View.OnClickListener mItemClickListener;

    public ChatAdapter(ArrayList<ChatRoomData> mChatList, View.OnClickListener mItemClickListener) {
        this.mChatList = mChatList;
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public MainBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_chat_room, parent, false);
        ChatViewHolder holder = new ChatViewHolder(view);
        holder.itemView.setOnClickListener(mItemClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainBaseViewHolder holder, int position) {
        ChatRoomData roomData = mChatList.get(position);

        holder.bind(position, roomData);
    }

    @Override
    public int getItemCount() {
        return Utils.isListEmpty(mChatList) ? 0 : mChatList.size();
    }
}
