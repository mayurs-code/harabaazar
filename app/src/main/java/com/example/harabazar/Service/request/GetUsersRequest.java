package com.example.harabazar.Service.request;

public class GetUsersRequest {
    String limit;
    String is_near_by = "0";
    String latitude;
    String longitude;

    public String getIs_near_by() {
        return is_near_by;
    }

    public void setIs_near_by(String is_near_by) {
        this.is_near_by = is_near_by;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }
}
