package com.example.harabazar.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.harabazar.BottomSheet.ConfirmAddressBottomSheet;
import com.example.harabazar.BottomSheet.OffersFragmentHandpickBottom;
import com.example.harabazar.Fragment.LoadingFragment;
import com.example.harabazar.Fragment.NoInternetFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.ApplyCouponRequest;
import com.example.harabazar.Service.request.GetProfileRequest;
import com.example.harabazar.Service.request.PlaceOrderRequest;
import com.example.harabazar.Service.response.ApplyCouponResponse;
import com.example.harabazar.Service.response.ApplyCouponResponseData;
import com.example.harabazar.Service.response.CartResponseData;
import com.example.harabazar.Service.response.GetProfileResponse;
import com.example.harabazar.Service.response.GetProfileResponseData;
import com.example.harabazar.Service.response.PlaceOrderResponse;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.CheckLocation;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CheckoutActivity extends AppCompatActivity implements OnRequestResponseListener , EventHandler {
    MaterialButton mb_paynow;
    Calendar calendar;
    String session;
    TextView tvSubtotal;
    TextView tvDeliveryCharges;
    TextView tvTotal;
    TextView tvPromo;
    TextView tvItemNo;
    MaterialButton mb_ApplyPromo;
    ImageView ivBack;
    EditText etPromoCode;
    MaterialCardView mcOffers;

    TextView tvTotalItem;
    private ApplyCouponResponseData applyCouponResponseData;
    private CartResponseData cartResponseData;
    private GetProfileResponseData getProfileResponseData;
    private String offer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        mb_paynow = findViewById(R.id.mb_paynow);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDeliveryCharges = findViewById(R.id.tvDeliveryCharges);
        tvTotal = findViewById(R.id.tvTotal);
        mb_ApplyPromo = findViewById(R.id.mb_ApplyPromo);
        etPromoCode = findViewById(R.id.etPromoCode);
        tvItemNo = findViewById(R.id.tvItemNo);
        mcOffers = findViewById(R.id.mcOffers);
        tvPromo = findViewById(R.id.tvPromo);
        ivBack = findViewById(R.id.ivBack);
        mb_paynow = findViewById(R.id.mb_paynow);
        tvTotalItem = findViewById(R.id.tvTotalItem);
        calendar = (Calendar) getIntent().getSerializableExtra("datetime");
        cartResponseData = (CartResponseData) getIntent().getSerializableExtra("cartResponseData");
        session = AppSettings.getSessionKey(this);
        boolean b= CheckLocation.isLocationEnabled(CheckoutActivity.this);
        if(b==true)
        {
         //   Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
           // Toast.makeText(getApplicationContext(), "disable", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutActivity.this);
            builder.setTitle("Please Enable Your Location")
                    .setMessage("To Continue Our Services...")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            }) ;
            AlertDialog alert = builder.create();
            alert.show();
        }


    }

    private void methods() {
        listners();
        getProfileConnector();
        setData(cartResponseData);
    }
    LoadingFragment loadingFragment = new LoadingFragment();

    @Override
    public void onResume() {
        super.onResume();
        methods();
    }

    private void getProfileConnector() {
        loadingFragment.show(getSupportFragmentManager(),getClass().getSimpleName());
        Connector connector = new Connector();
        GetProfileRequest getProfileRequest =new GetProfileRequest();
        getProfileRequest.setUser_id(AppSettings.getUId(this));
        ServerCommunicator.getProfile(connector, getProfileRequest, session);
        connector.setOnRequestResponseListener(this);
    }

    private void setData(ApplyCouponResponseData data) {
        tvSubtotal.setText("Rs." + data.getSub_total());
        tvDeliveryCharges.setText("Rs." + data.getDelivery_charge());
        tvTotal.setText("Rs." + data.getTotal());
        tvPromo.setText("Rs." + data.getPromo_code());

        this.applyCouponResponseData = data;

    }

    private void setData(CartResponseData data) {
        tvSubtotal.setText("Rs." + data.getSub_total());
        tvDeliveryCharges.setText("Rs." + data.getDelivery_charge());
        tvTotal.setText("Rs." + data.getTotal());
        tvPromo.setText("NIL");
        tvItemNo.setText("No of Products : " + data.getProducts().size());

    }

    private void connectorInit() {
        Connector connector = new Connector();
        PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest();
        placeOrderRequest.setAddress_id(getProfileResponseData.getAddress().get(0).getId());
        placeOrderRequest.setPayment_method("1");
        if (applyCouponResponseData != null) {
            placeOrderRequest.setOffer_id(applyCouponResponseData.getOffer_id());
        }
        if (calendar != null) {
            SimpleDateFormat time = new SimpleDateFormat("HH:mm:SS");
            SimpleDateFormat date = new SimpleDateFormat("y-M-d");
            placeOrderRequest.setSchedule_date(date.format(calendar.getTime()));
            placeOrderRequest.setSchedule_time(time.format(calendar.getTime()));
        }
        ServerCommunicator.placeOrder(connector, placeOrderRequest, session);
        GetProfileRequest getProfileRequest =new GetProfileRequest();
        getProfileRequest.setUser_id(AppSettings.getUId(this));
        ServerCommunicator.getProfile(connector, getProfileRequest, session);
        connector.setOnRequestResponseListener(this);
    }

    private void listners() {
        mb_paynow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!loadingFragment.isAdded()){
                    loadingFragment.show(getSupportFragmentManager(), getClass().getSimpleName() );
                    mb_paynow.setVisibility(View.GONE);
                }


                connectorInit();

            }
        });
        mb_ApplyPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!loadingFragment.isAdded())

                    loadingFragment.show(getSupportFragmentManager(), getClass().getSimpleName() );

                connectorInit(etPromoCode.getText().toString());
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mcOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 bottom = new OffersFragmentHandpickBottom(CheckoutActivity.this,2);
                bottom.show(getSupportFragmentManager(), CheckoutActivity.this.getClass().getSimpleName());
            }
        });
    }
    OffersFragmentHandpickBottom bottom  ;

    private void connectorInit(String couponCode) {
        Connector connector = new Connector();
        ApplyCouponRequest applyCouponRequest = new ApplyCouponRequest();
        applyCouponRequest.setCoupon_code(couponCode);
        ServerCommunicator.applyCouponCode(connector, applyCouponRequest, session);
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        mb_paynow.setVisibility(View.VISIBLE);
        if (webResponse instanceof PlaceOrderResponse) {
            final PlaceOrderResponse responseBody = (PlaceOrderResponse) webResponse;
            if (responseBody.getStatus()) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingFragment.dismisss();

                        gotoOrderPlacedActivity(responseBody);
                    }
                });


            } else {
                this.runOnUiThread(new Runnable() {
                    public void run() {

                        Utils.ShowToast(CheckoutActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof ApplyCouponResponse) {
            final ApplyCouponResponse responseBody = (ApplyCouponResponse) webResponse;
            if (responseBody.getStatus()) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingFragment.dismisss();

                        setData(responseBody.getData());

                    }
                });


            } else {
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        loadingFragment.dismisss();
                        etPromoCode.setText("");
                        Utils.ShowToast(CheckoutActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }if (webResponse instanceof GetProfileResponse) {
            final GetProfileResponse responseBody = (GetProfileResponse) webResponse;
            if (responseBody.getStatus()) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingFragment.dismiss();
                        getProfileResponseData =(responseBody.getData());

                    }
                });


            } else {
                this.runOnUiThread(new Runnable() {
                    public void run() {

                        Utils.ShowToast(CheckoutActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }

    }

    private void gotoOrderPlacedActivity(PlaceOrderResponse responseBody) {
        Intent I = new Intent(CheckoutActivity.this, OrderPlacedActivity.class);
        I.putExtra("order_id", responseBody.getOrder_id());
        startActivity(I);
        finish();

    }

    @Override
    public void onUploadComplete(WebResponse webResponse) {

    }

    @Override
    public void onVFRClientException(WebErrorResponse edErrorData) {
        AppLogger.e(Utils.getTag(), edErrorData.getMessage());
        this.runOnUiThread(new Runnable() {
            public void run() {
                Utils.ShowToast(CheckoutActivity.this, edErrorData.getMessage());
            }
        });
    }

    @Override
    public void onAuthException() {

    }

    NoInternetFragment dialogFragment=new NoInternetFragment();

    @Override
    public void onNoConnectivityException(String message) {

        if(message.equals("-1")){
            dialogFragment.show(getSupportFragmentManager(),""+ Constants.incrementalID++);
        }if(message.equals("1")){
            try{
                dialogFragment.dismiss();
            }catch (Exception e){

            }
        }
    }

    @Override
    public void onNoCachedDataAvailable() {

    }

    @Override
    public void handle() {

    }

    @Override
    public void handle(int position) {

    }

    @Override
    public void handle(String text) {
        this.offer = text ;
        etPromoCode.setText(text );
        if(bottom!=null){
            bottom.dismiss();
        }
    }
}
