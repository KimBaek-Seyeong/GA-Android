package com.goldax.goldax.data;

public class SignUpResult {
    public boolean isSuccess;
    public String status;

    @Override
    public String toString() {
        return "SignUpResult{" +
                "isSuccess=" + isSuccess +
                ", status='" + status + '\'' +
                '}';
    }
}
