package com.example.harabazar.Service.response;

public class GetSettingsResponseData {
    String delivery_charge;

    public String getDelivery_charge() {
        return delivery_charge;
    }

    public void setDelivery_charge(String delivery_charge) {
        this.delivery_charge = delivery_charge;
    }

    public String getHandpick_delivery_charge() {
        return handpick_delivery_charge;
    }

    public void setHandpick_delivery_charge(String handpick_delivery_charge) {
        this.handpick_delivery_charge = handpick_delivery_charge;
    }

    String handpick_delivery_charge;
}
