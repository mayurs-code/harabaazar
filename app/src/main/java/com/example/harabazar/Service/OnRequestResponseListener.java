package com.example.harabazar.Service;


import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;

public interface OnRequestResponseListener {
    public void onAddMoreResponse(WebResponse webResponse);

    public void onHttpResponse(WebResponse webResponse);

    public void onUploadComplete(WebResponse webResponse);

    public void onVFRClientException(WebErrorResponse edErrorData);

    public void onAuthException();

    public void onNoConnectivityException(String message);

    public void onNoCachedDataAvailable();
}
