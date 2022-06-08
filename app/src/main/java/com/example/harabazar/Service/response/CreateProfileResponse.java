package com.example.harabazar.Service.response;

import java.util.List;

public class CreateProfileResponse extends WebResponse {

    private CreateProfileData data;

    public CreateProfileData getData() {
        return data;
    }

    public void setData(CreateProfileData data) {
        this.data = data;
    }
}
