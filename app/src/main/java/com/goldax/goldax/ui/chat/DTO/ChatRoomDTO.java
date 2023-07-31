package com.goldax.goldax.ui.chat.DTO;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class ChatRoomDTO implements Serializable {
    private String myName;
    private int myId;
    private String targetName;
    private int targetId;
    private String targetProfileUrl;
    private String lastChat;

    public ChatRoomDTO() {
    }

    public ChatRoomDTO(String myName, int myId, String targetName, int targetId, String targetProfileUrl) {
        this.myName = myName;
        this.myId = myId;
        this.targetName = targetName;
        this.targetId = targetId;
        this.targetProfileUrl = targetProfileUrl;
    }

    public void setMyName(String myName) {
        this.myName = myName;
    }

    public void setMyId(int myId) {
        this.myId = myId;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getMyName() {
        return myName;
    }

    public int getMyId() {
        return myId;
    }

    public String getTargetName() {
        return targetName;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetProfileUrl(String targetProfileUrl) {
        this.targetProfileUrl = targetProfileUrl;
    }

    public String getTargetProfileUrl() {
        return targetProfileUrl;
    }

    public void setLastChat(String lastChat) {
        this.lastChat = lastChat;
    }

    public String getLastChat() {
        return lastChat;
    }

    public String getParseData1() {
        if (myId >= targetId) {
            return myId + "^" + myName + "@" + targetId + "^" +targetName;
        } else {
            return targetId + "^" + targetName + "@" + myId + "^" + myName;
        }
    }

    // 크로스체크를 위함. 불필요할 수 있음.
    public String getParseData2() {
        if (myId < targetId) {
            return myId + "^" + myName + "@" + targetId + "^" +targetName;
        } else {
            return targetId + "^" + targetName + "@" + myId + "^" + myName;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "myId: " + myId + ", myName: " + myName
                + ", targetId: " + targetId + ", targetName: " + targetName
                + ", getTargetProfileUrl: " + targetProfileUrl;
    }
}
