package com.goldax.goldax.ui.chat;

import com.goldax.goldax.data.UserData;
import com.goldax.goldax.ui.chat.DTO.ChatRoomDTO;
import com.google.firebase.database.ChildEventListener;

import java.util.ArrayList;

public class ChatContract {
    public interface View {
        void initChatList();

        void addChatList(String key, String value, String lastChat, String imageUrl);

        void openChat(ChatRoomDTO roomDTO);

        void addRoom(ChatRoomDTO roomDTO);

        void updateLastChat(String roomName, String lastChat);
    }

    public interface Presenter {
        void setUserData(int userId, String userName);

        UserData getUserData();

        void requestTargetProfileImage(String key, String value, String lastChat);

        void addList(String key, String value, String lastChat, String imageUrl);
    }
}
