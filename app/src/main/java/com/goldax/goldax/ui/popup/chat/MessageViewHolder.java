package com.goldax.goldax.ui.popup.chat;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.goldax.goldax.R;
import com.goldax.goldax.base.MainBaseViewHolder;
import com.goldax.goldax.layout.CircleView;
import com.goldax.goldax.util.ImageLoader;
import com.goldax.goldax.util.ImageRequest;

import butterknife.BindView;

public class MessageViewHolder extends MainBaseViewHolder {
    private static final String TAG = MessageViewHolder.class.getSimpleName();

    @BindView(R.id.v_chat_message_profile_image)
    public CircleView mProfileImage;
    @BindView(R.id.v_chat_message_name)
    public TextView mName;
    @BindView(R.id.v_chat_message_text)
    public TextView mMessage;
    @BindView(R.id.v_chat_message_time)
    public TextView mTime;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void bind(int pos, Object obj) {
        Log.d(TAG, "bind() called. pos: " + pos + " | " + obj);
        if (obj instanceof MessageData) {
            MessageData messageData = (MessageData) obj;

            mMessage.setText(messageData.message);

            if (mProfileImage.getVisibility() == View.VISIBLE) {
                mName.setText(messageData.userName);

                Object urlObject = itemView.getTag(R.attr.key_image_path);
                String imageUrl = "";
                if (urlObject instanceof String) {
                    imageUrl = (String) itemView.getTag(R.attr.key_image_path);
                }
                if (!TextUtils.isEmpty(imageUrl)) {
                    ImageRequest imageRequest = new ImageRequest.Builder(mProfileImage.mIconView, imageUrl)
                            .setErrorResId(R.drawable.default_profile)
                            .setRound(true)
                            .build();
                    ImageLoader.loadImage(imageRequest);
                } else {
                    mProfileImage.setIcon(R.drawable.default_profile);
                }


            }
        }
    }
}
