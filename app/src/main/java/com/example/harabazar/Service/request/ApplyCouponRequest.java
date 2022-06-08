package com.example.harabazar.Service.request;

import com.example.harabazar.Service.response.WebResponse;

public class ApplyCouponRequest {
    String coupon_code;

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }
}
