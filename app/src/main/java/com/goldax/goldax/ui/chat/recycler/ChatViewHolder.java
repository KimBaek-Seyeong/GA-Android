package com.goldax.goldax.ui.chat.recycler;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.goldax.goldax.R;
import com.goldax.goldax.base.MainBaseViewHolder;
import com.goldax.goldax.layout.CircleView;
import com.goldax.goldax.util.ImageLoader;
import com.goldax.goldax.util.ImageRequest;
import com.goldax.goldax.util.Utils;

import butterknife.BindView;

public class ChatViewHolder extends MainBaseViewHolder {
    private static final String TAG = ChatViewHolder.class.getSimpleName();

    @BindView(R.id.v_chat_room_target_profile_image)
    public CircleView mProfileImage;
    @BindView(R.id.v_chat_room_target_user_name_text)
    public TextView mUserNameText;
    @BindView(R.id.v_chat_room_target_message_text)
    public TextView mMessageText;

    public ChatViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(int pos, Object obj) {
        Log.d(TAG, "bind() called. pos: " + pos + " | " + obj);
        if (obj instanceof ChatRoomData) {
            ChatRoomData data = (ChatRoomData) obj;
            String roomName = data.value;
            String targetName = Utils.getChatUsername(roomName);
            String targetImageUrl = Utils.getChatUserImageUrl(roomName);
            String lastChat = data.lastChat;

            mUserNameText.setText(targetName);

            if (!TextUtils.isEmpty(targetImageUrl)) {
                ImageRequest imageRequest = new ImageRequest.Builder(mProfileImage.mIconView, targetImageUrl)
                        .setErrorResId(R.drawable.default_profile)
                        .setRound(true)
                        .build();
                ImageLoader.loadImage(imageRequest);
            } else {
                mProfileImage.setIcon(R.drawable.default_profile);
            }
            mMessageText.setText(lastChat);

            itemView.setTag(data);
            itemView.setTag(R.attr.key_image_pos, pos);
        }
    }
}
