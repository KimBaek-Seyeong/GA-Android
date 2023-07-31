package com.goldax.goldax.ui.popup.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.goldax.goldax.R;
import com.goldax.goldax.base.MainBaseAdapter;
import com.goldax.goldax.base.MainBaseViewHolder;
import com.goldax.goldax.data.DataConst;
import com.goldax.goldax.ui.chat.recycler.ChatRoomData;
import com.goldax.goldax.util.Utils;

import java.util.ArrayList;

public class MessageAdapter extends MainBaseAdapter {
    private static final String TAG = MessageAdapter.class.getSimpleName();

    private ArrayList<MessageData> mMessageList;
    private ChatRoomData mRoomData;

    public MessageAdapter(ArrayList<MessageData> messageList, ChatRoomData roomData) {
        this.mMessageList = messageList;
        this.mRoomData = roomData;
    }

    @NonNull
    @Override
    public MainBaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == DataConst.CHAT_VIEW_TYPE_MY){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_chat_my_message, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.v_chat_target_message, parent, false);
            if (mRoomData != null) {
                String roomName = mRoomData.value;
                String targetImageUrl = Utils.getChatUserImageUrl(roomName);
                view.setTag(R.attr.key_image_path, targetImageUrl);
            }
        }
        MessageViewHolder holder = new MessageViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainBaseViewHolder holder, int position) {
        MessageData messageData = mMessageList.get(position);

        holder.bind(position, messageData);
    }

    @Override
    public int getItemViewType(int position) {
        MessageData data = mMessageList.get(position);
        if (data.isMy) {
            return DataConst.CHAT_VIEW_TYPE_MY;
        } else {
            return DataConst.CHAT_VIEW_TYPE_TARGET;
        }
    }

    @Override
    public int getItemCount() {
        return Utils.isListEmpty(mMessageList) ? 0 : mMessageList.size();
    }
}
