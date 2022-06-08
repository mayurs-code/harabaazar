package com.example.harabazar.Service.response;

public class GetSettingsResponse extends WebResponse{
    GetSettingsResponseData data;

    public GetSettingsResponseData getData() {
        return data;
    }

    public void setData(GetSettingsResponseData data) {
        this.data = data;
    }
}
