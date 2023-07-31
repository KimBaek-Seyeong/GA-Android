package com.goldax.goldax.ui.chat.DTO;

public class ChatDTO {
    private int userId;
    private String userName;
    private String message;

    public ChatDTO() {
    }

    public ChatDTO(int userId, String userName, String message) {
        this.userId = userId;
        this.userName = userName;
        this.message = message;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }
}
