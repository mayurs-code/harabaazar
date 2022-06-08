package com.example.harabazar.Service.response;

import java.util.List;

public class GetUsersResponse extends WebResponse {
    List<GetUsersResponseData> data;

    public List<GetUsersResponseData> getData() {
        return data;
    }

    public void setData(List<GetUsersResponseData> data) {
        this.data = data;
    }
}
