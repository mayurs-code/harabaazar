package com.example.harabazar.Service.response;

import java.util.List;

public class GetHandpickOffersResponse extends WebResponse {
    List<GetHandpickOffersResponseData> data;

    public List<GetHandpickOffersResponseData> getData() {
        return data;
    }

    public void setData(List<GetHandpickOffersResponseData> data) {
        this.data = data;
    }
}
