package com.example.harabazar.Service.response;

import java.util.List;

public class CartResponse extends WebResponse{

    CartResponseData data;

    public CartResponseData getData() {
        return data;
    }

    public void setData(CartResponseData data) {
        this.data = data;
    }
}
