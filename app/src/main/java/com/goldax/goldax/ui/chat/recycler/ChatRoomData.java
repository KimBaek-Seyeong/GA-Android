package com.goldax.goldax.ui.chat.recycler;

import java.io.Serializable;

public class ChatRoomData implements Serializable {
    public String key; // 방 제목
    public String value; // 파싱 후 상대방 데이터
    public String lastChat; // 마지막 채팅 텍스트

    public ChatRoomData(String key, String value, String lastChat) {
        this.key = key;
        this.value = value;
        this.lastChat = lastChat;
    }
}
