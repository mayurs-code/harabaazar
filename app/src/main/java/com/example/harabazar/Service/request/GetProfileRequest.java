package com.example.harabazar.Service.request;

public class GetProfileRequest {
    String user_id;

    public GetProfileRequest() {

    }
    public GetProfileRequest(String user_id) {
        this.user_id=user_id;

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
