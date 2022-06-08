package com.example.harabazar.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.harabazar.Fragment.LoadingFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.GetProfileRequest;
import com.example.harabazar.Service.request.OrderDetailRequest;
import com.example.harabazar.Service.response.GetProfileResponse;
import com.example.harabazar.Service.response.GetProfileResponseData;
import com.example.harabazar.Service.response.OrderDetailResponse;
import com.example.harabazar.Service.response.OrderDetailResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.button.MaterialButton;

public class RateActivity extends AppCompatActivity implements OnRequestResponseListener {

    ImageView ivBack;
    ImageView star1;
    ImageView star2;
    ImageView star3;
    ImageView star4;
    ImageView star5;
    ImageView ivImage;
    TextView orderID;
    TextView orderDate;
    TextView tvID;
    TextView tvName;
    TextView tvReviews;
    MaterialButton mbDone;
    String notification;
    LoadingFragment loadingFragment = new LoadingFragment();
    int rating = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_handpicked);
        loadingFragment.show(getSupportFragmentManager(), getClass().getSimpleName());
        notification = getIntent().getStringExtra("NotificationMessage");
        if (notification != null) {
            connectorInit(notification.split(":")[1]);
        }
        initViews();
        clickListener();
    }

    private void initViews() {
        star1 = findViewById(R.id.star1);
        star2 = findViewById(R.id.star2);
        star3 = findViewById(R.id.star3);
        star4 = findViewById(R.id.star4);
        star5 = findViewById(R.id.star5);
        ivBack = findViewById(R.id.ivBack);
        mbDone = findViewById(R.id.mbDone);
        ivImage = findViewById(R.id.ivImage);
        orderID = findViewById(R.id.orderID);
        orderDate = findViewById(R.id.orderDate);
        tvID = findViewById(R.id.tvID);
        tvName = findViewById(R.id.tvName);
        tvReviews = findViewById(R.id.tvReviews);
    }

    private void clickListener() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mbDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star2.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                star3.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                star4.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                star5.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                rating=1;
            }
        });star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star2.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star3.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                star4.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                star5.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                rating=2;
            }
        });star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star2.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star3.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star4.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                star5.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                rating=3;
            }
        });star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star2.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star3.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star4.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star5.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                rating=4;
            }
        });star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                star1.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star2.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star3.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star4.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                star5.setColorFilter(ContextCompat.getColor(RateActivity.this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                rating=5;
            }
        });
    }

    private void connectorInit(String orderId) {
        Connector connector = new Connector();
        OrderDetailRequest orderDetailRequest = new OrderDetailRequest();
        orderDetailRequest.setOrder_id(orderId);
        ServerCommunicator.giveOrderDetails(connector, orderDetailRequest, AppSettings.getSessionKey(this));
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    private void connectorGetUserProfile(String id) {
        //        Connector connector = Connector.getConnector();
        Connector connector = new Connector();
        GetProfileRequest getProfileRequest = new GetProfileRequest();
        getProfileRequest.setUser_id(id);
        ServerCommunicator.getProfile(connector, getProfileRequest, AppSettings.getSessionKey(this));
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof OrderDetailResponse) {
            final OrderDetailResponse responseBody = (OrderDetailResponse) webResponse;
            if (responseBody.getStatus()) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setData(responseBody.getData());
                        connectorGetUserProfile(responseBody.getData().getSabjiwala_id());
                    }
                });


            } else {
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(RateActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof GetProfileResponse) {
            final GetProfileResponse responseBody = (GetProfileResponse) webResponse;
            if (responseBody.getStatus()) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingFragment.dismisss();

                        setUserData(responseBody.getData());
                    }
                });


            } else {
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(RateActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }
    }

    private void setUserData(GetProfileResponseData data) {
        tvName.setText(data.getFull_name());
        tvID.setText(data.getUser_id());
        tvReviews.setText(data.getRating() + " STAR");

        try {
            Glide.with(this).load(Constants.FILES_URL + "" + data.getProfile_image()).into(ivImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData(OrderDetailResponseData data) {
        orderID.setText(data.getOrder_id());
        orderDate.setText("Order Date: " + data.getCreated_date());
    }

    @Override
    public void onUploadComplete(WebResponse webResponse) {

    }

    @Override
    public void onVFRClientException(WebErrorResponse edErrorData) {

    }

    @Override
    public void onAuthException() {

    }

    @Override
    public void onNoConnectivityException(String message) {

    }

    @Override
    public void onNoCachedDataAvailable() {

    }
}