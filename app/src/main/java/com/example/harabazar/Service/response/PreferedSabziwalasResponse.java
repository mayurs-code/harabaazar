package com.example.harabazar.Service.response;

import java.util.ArrayList;
import java.util.List;

public class PreferedSabziwalasResponse extends  WebResponse{
    List <PreferedSabziwalasResponseDat> data=new ArrayList<>();

    public List<PreferedSabziwalasResponseDat> getData() {
        return data;
    }

    public void setData(List<PreferedSabziwalasResponseDat> data) {
        this.data = data;
    }
}
