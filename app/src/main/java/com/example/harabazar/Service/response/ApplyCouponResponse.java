package com.example.harabazar.Service.response;

public class ApplyCouponResponse extends WebResponse{
    ApplyCouponResponseData data;

    public ApplyCouponResponseData getData() {
        return data;
    }

    public void setData(ApplyCouponResponseData data) {
        this.data = data;
    }
}
