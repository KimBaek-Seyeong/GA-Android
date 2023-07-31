package com.goldax.goldax.data;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;

public class ListResult {
    public boolean isSuccess;
    public int status;
    public ArrayList<ResultData> result;

    public class ResultData implements Serializable {
        public int id;
        public String title;
        public String contents;
        public String location;
        public String foundAt;
        public String lostAt;
        public String labels;
        public String image;
        public String category;
        public String createdAt;
        public String reward;
        public boolean isClosed; // 전달 완료 플래그.
        public boolean isDeleted;
        public int userId; // 작성자 index
        public UserData user;
        public ArrayList<ImageData> images;

        @NonNull
        @Override
        public String toString() {
            return "id: " + id + ", title: " + title
                    + ", contents: " + contents + ", location: " + location
                    + ", foundAt: " + foundAt + ", lostAt: " + lostAt
                    + ", image: " + image + ", category: " + category
                    + ", createAt: " + createdAt + ", reward: " + reward
                    + ", isClosed: " + isClosed + ", isDeleted: " + isDeleted
                    + ", userId: " + userId + ", images: " + images;
        }
    }
}
