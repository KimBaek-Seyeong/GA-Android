package com.goldax.goldax.data;

public class SignUpRequest {
    public String email;
    public String password;
    public String name;
    public String classId;

    public SignUpRequest(String email, String password, String name, String classId) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.classId = classId;
    }

    @Override
    public String toString() {
        return "SignUpRequest{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", classId='" + classId + '\'' +
                '}';
    }
}
