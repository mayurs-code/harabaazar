package com.example.harabazar.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.Adapter.CartItemAdapter;
import com.example.harabazar.BottomSheet.ScheduleDeliveryBottomSheet;
import com.example.harabazar.Fragment.LoadingFragment;
import com.example.harabazar.Fragment.NoInternetFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.GetProfileRequest;
import com.example.harabazar.Service.response.ApplyCouponResponseData;
import com.example.harabazar.Service.response.CartResponse;
import com.example.harabazar.Service.response.CartResponseData;
import com.example.harabazar.Service.response.GetProfileResponse;
import com.example.harabazar.Service.response.GetProfileResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.CheckLocation;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.button.MaterialButton;

import java.io.Serializable;

public class CartActivity extends AppCompatActivity implements OnRequestResponseListener, Serializable, EventHandler {

    MaterialButton mb_proceedCheckout;
    MaterialButton mbChange;
    RecyclerView rv_OverviewItems;
    String session;
    TextView tvSubtotal;
    ImageView ivBack;
    LinearLayout llEmptyCart;
    TextView tvPromo;
    TextView tvDeliveryCharges;
    CheckBox cbSchedule;
    TextView tvTotal;
    TextView tvAddress;
    TextView tvTotalItem;
    TextView tvItemNo;
    private ApplyCouponResponseData applyCouponResponseData;
    private CartResponseData cartResponseData;
    private GetProfileResponseData getProfileResponseData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mb_proceedCheckout = findViewById(R.id.mb_proceedCheckout);
        ivBack = findViewById(R.id.ivBack);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        cbSchedule = findViewById(R.id.cbSchedule);
        tvAddress = findViewById(R.id.tvAddress);
        mbChange = findViewById(R.id.mbChange);
        tvPromo = findViewById(R.id.tvPromo);
        llEmptyCart = findViewById(R.id.llEmpty);

        tvDeliveryCharges = findViewById(R.id.tvDeliveryCharges);
        tvTotal = findViewById(R.id.tvTotal);
        tvTotalItem = findViewById(R.id.tvTotalItem);
        tvItemNo = findViewById(R.id.tvItemNo);
        rv_OverviewItems = findViewById(R.id.rv_OverviewItems);
        session = AppSettings.getSessionKey(this);
        boolean b= CheckLocation.isLocationEnabled(CartActivity.this);
        if(b==true)
        {
           // Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
           // Toast.makeText(getApplicationContext(), "disable", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
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
        connectorInit();
        connectorProfileInit();
    }
    LoadingFragment loadingFragment = new LoadingFragment();

    @Override
    public void onResume() {
        super.onResume();
        methods();
    }

    private void connectorProfileInit() {
        Connector connector = new Connector();

        GetProfileRequest getProfileRequest = new GetProfileRequest();
        getProfileRequest.setUser_id(AppSettings.getUId(this));
        ServerCommunicator.getProfile(connector, getProfileRequest, session);
        connector.setOnRequestResponseListener(this);

    }


    private void connectorInit() {
        if(!loadingFragment.isAdded())
        loadingFragment.show(getSupportFragmentManager(), getClass().getSimpleName());

        Connector connector = new Connector();
        ServerCommunicator.getCartProductList(connector, session);
        connector.setOnRequestResponseListener(this);
    }


    private void listners() {

        mb_proceedCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getProfileResponseData.getAddress().size() <= 0) {
                    Intent i = new Intent(CartActivity.this, AddressActivity.class);
                    startActivity(i);
                } else if (cbSchedule.isChecked()) {
                    ScheduleDeliveryBottomSheet scheduleDeliveryBottomSheet = new ScheduleDeliveryBottomSheet(applyCouponResponseData, cartResponseData);
                    scheduleDeliveryBottomSheet.show(getSupportFragmentManager(), "ScheduleDeliveryBottomSheet");

                } else {
                    Intent i = new Intent(CartActivity.this, CheckoutActivity.class);
                    if (applyCouponResponseData != null)
                        i.putExtra("applyCouponResponse", applyCouponResponseData);
                    if (cartResponseData != null)
                        i.putExtra("cartResponseData", cartResponseData);
                    startActivity(i);
                }
            }
        });
        mbChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(CartActivity.this,AddressActivity.class);
                startActivity(i);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof CartResponse) {
            loadingFragment.dismisss();
            final CartResponse responseBody = (CartResponse) webResponse;
            if (responseBody.getStatus()) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCartAdapter(responseBody.getData());
                        setData(responseBody.getData());
                    }
                });


            } else {
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(CartActivity.this, "" + responseBody.getMessage());
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
                        setData(responseBody.getData());

                    }
                });


            } else {
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(CartActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }


    }

    private void setData(GetProfileResponseData data) {
        getProfileResponseData = data;
        try {
            for (int i = 0; i < data.getAddress().size(); i++) {
                if (data.getAddress().get(i).getIs_default() == 1)
                    tvAddress.setText(data.getAddress().get(i).getAddress());

            }
        } catch (Exception e) {
            tvAddress.setText("No Address Found");

        }



    }

    private void setData(ApplyCouponResponseData data) {
        tvSubtotal.setText("Rs." + data.getSub_total());
        tvDeliveryCharges.setText("Rs." + data.getDelivery_charge());
        tvTotal.setText("Rs." + data.getTotal());
        tvPromo.setText("Rs." + data.getPromo_code());

        this.applyCouponResponseData = data;

    }

    private void setData(CartResponseData data) {
        this.cartResponseData = data;
        if (cartResponseData.getProducts().size() > 0) {
            llEmptyCart.setVisibility(View.GONE);
        } else llEmptyCart.setVisibility(View.VISIBLE);


        tvSubtotal.setText("Rs." + data.getSub_total());
        tvDeliveryCharges.setText("Rs." + data.getDelivery_charge());
        tvTotal.setText("Rs." + data.getTotal());
        tvPromo.setText("---");
        tvItemNo.setText("No of Products : " + data.getProducts().size());
        tvTotalItem.setText("Total(" + data.getProducts().size() + " items)");
        mb_proceedCheckout.setVisibility(View.VISIBLE);

        if (data.getProducts().size() == 0) {
            mb_proceedCheckout.setVisibility(View.GONE);
        }

    }

    private void setCartAdapter(CartResponseData data) {
        rv_OverviewItems.setAdapter(new CartItemAdapter(this, data, this));
        rv_OverviewItems.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public void onUploadComplete(WebResponse webResponse) {

    }

    @Override
    public void onVFRClientException(WebErrorResponse edErrorData) {
        AppLogger.e(Utils.getTag(), edErrorData.getMessage());
        this.runOnUiThread(new Runnable() {
            public void run() {
                Utils.ShowToast(CartActivity.this, edErrorData.getMessage());
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
        connectorInit();
    }

    @Override
    public void handle(int position) {

    }

    @Override
    public void handle(String text) {

    }
}
