package com.example.harabazar.Service.response;

public class CreateProfilePhotoResponse extends WebResponse
{
    private CreateProfilePhotoData data;

    public CreateProfilePhotoData getData() {
        return data;
    }

    public void setData(CreateProfilePhotoData data) {
        this.data = data;
    }
}
