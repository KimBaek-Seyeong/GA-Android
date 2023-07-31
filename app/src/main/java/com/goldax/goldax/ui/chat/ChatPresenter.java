package com.goldax.goldax.ui.chat;

import android.text.TextUtils;

import com.goldax.goldax.data.UserData;
import com.google.firebase.database.ChildEventListener;

public class ChatPresenter implements ChatContract.Presenter {
    private static final String TAG = ChatPresenter.class.getSimpleName();

    private ChatModel mModel;
    private ChatContract.View mView;

    public ChatPresenter(ChatContract.View view) {
        mView = view;
        mModel = new ChatModel(this);
    }

    @Override
    public void setUserData(int userId, String userName) {
        mModel.setUserData(userId, userName);
    }

    @Override
    public UserData getUserData() {
        return mModel.getUserData();
    }

    @Override
    public void requestTargetProfileImage(String key, String value, String lastChat) {
        if (TextUtils.isEmpty(value)) {
            return;
        }

        mModel.requestTargetProfileImage(key, value, lastChat);
    }

    @Override
    public void addList(String key, String value, String lastChat, String imageUrl) {
        mView.addChatList(key, value, lastChat, imageUrl);
    }


}
