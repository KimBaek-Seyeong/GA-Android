package com.goldax.goldax.data;

public class UsersResult {
    public boolean isSuccess;
    public int status;
    public ResultData result;

    public class ResultData {
        public UserData user;
        public int foundCount;
        public int lostCount;
    }
}
