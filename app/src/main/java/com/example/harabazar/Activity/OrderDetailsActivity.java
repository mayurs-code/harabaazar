package com.example.harabazar.Activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.Adapter.ProductOverViewAdapter;
import com.example.harabazar.Fragment.IncomingNotificationFragment;
import com.example.harabazar.Fragment.LoadingFragment;
import com.example.harabazar.Fragment.NoInternetFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.CancelOrderRequest;
import com.example.harabazar.Service.request.OrderDetailRequest;
import com.example.harabazar.Service.response.CancelOrderResponse;
import com.example.harabazar.Service.response.OrderDetailResponse;
import com.example.harabazar.Service.response.OrderDetailResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.CheckLocation;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.button.MaterialButton;


public class OrderDetailsActivity extends AppCompatActivity implements OnRequestResponseListener {
    RecyclerView rv_OverviewItems;
    MaterialButton mbCancel;
    String orderID;
    String session;
    TextView tvSubtotal;
    TextView tvDeliveryCharges;
    TextView tvTotal;
    TextView tvPromo;
    TextView tvItemNo;
    TextView tvTotalItem;
    TextView tvOrderId;
    TextView tvOrderDate;
    TextView tvBack;
    TextView tvAddress;
    ImageView ivOrderPlaced;
    ImageView ivOrderPacked;
    ImageView ivOrderDelivered;
    IncomingNotificationFragment incomingNotificationFragment;


    private final BroadcastReceiver notificationCountRefresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppLogger.i("notificationCountRefresh", " notificationCountRefresh BroadcastReceiver -----");
            if (intent != null) {
                if (intent.hasExtra(Constants.NOTIFICATION_DATA + "")) {
//                    Toast.makeText(context, intent.getStringExtra(Constants.NOTIFICATION_DATA + ""), Toast.LENGTH_SHORT).show();
                    String notification = intent.getStringExtra(Constants.NOTIFICATION_DATA);
                    incomingNotificationFragment = new IncomingNotificationFragment(notification);
                    if (!incomingNotificationFragment.isAdded()) {
                        incomingNotificationFragment.show(getSupportFragmentManager(), OrderDetailsActivity.this.getClass().getSimpleName());
                    }
                    methods();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        mbCancel = findViewById(R.id.mbCancel);
        tvAddress = findViewById(R.id.tvAddress);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        tvDeliveryCharges = findViewById(R.id.tvDeliveryCharges);
        tvTotal = findViewById(R.id.tvTotal);
        tvPromo = findViewById(R.id.tvPromo);
        tvItemNo = findViewById(R.id.tvItemNo);
        tvOrderId = findViewById(R.id.tvOrderId);
        ivOrderPlaced = findViewById(R.id.ivOrderPlaced);
        ivOrderPacked = findViewById(R.id.ivOrderPacked);
        ivOrderDelivered = findViewById(R.id.ivOrderDelivered);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        tvBack = findViewById(R.id.tvBack);
        tvTotalItem = findViewById(R.id.tvTotalItem);
        orderID = getIntent().getStringExtra("order_id");
        session = AppSettings.getSessionKey(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationCountRefresh, new IntentFilter(Constants.BROAD_REFRESH_NOTIFICATION_COUNT));

        rv_OverviewItems = findViewById(R.id.rv_OverviewItems);
        boolean b= CheckLocation.isLocationEnabled(OrderDetailsActivity.this);
        if(b==true)
        {
            //   Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Toast.makeText(getApplicationContext(), "disable", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailsActivity.this);
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
    LoadingFragment loadingFragment = new LoadingFragment();

    @Override
    public void onResume() {
        super.onResume();
        loadingFragment.show(getSupportFragmentManager(), "");
        methods();
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationCountRefresh, new IntentFilter(Constants.BROAD_REFRESH_NOTIFICATION_COUNT));

    }
    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationCountRefresh);

    }

    private void methods() {
        listners();
        connectorInit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
        finish();
    }

    private void connectorInit() {
        Connector connector = new Connector();
        OrderDetailRequest orderDetailRequest = new OrderDetailRequest();
        orderDetailRequest.setOrder_id(orderID);
        ServerCommunicator.giveOrderDetails(connector, orderDetailRequest, session);
        connector.setOnRequestResponseListener(this);
    }
    private void connectorInit(CancelOrderRequest request) {
        Connector connector = new Connector();
        ServerCommunicator.cancelOrder(connector, request, session);
        connector.setOnRequestResponseListener(this);
    }

    private void listners() {
        mbCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CancelOrderRequest request=new CancelOrderRequest();
                request.setCancel_reason("Testing Cancel");
                request.setOrder_id(orderID);
                connectorInit(request);
            }
        });
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderDetailsActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {


    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        loadingFragment.dismisss();
        if (webResponse instanceof OrderDetailResponse) {
            final OrderDetailResponse responseBody = (OrderDetailResponse) webResponse;
            if (responseBody.getStatus()) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setOrdersAdapter(responseBody.getData());
                        setData(responseBody.getData());
                    }
                });


            } else {
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(OrderDetailsActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }if (webResponse instanceof CancelOrderResponse) {
            final CancelOrderResponse responseBody = (CancelOrderResponse) webResponse;
            if (responseBody.getStatus()) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.ShowToast(OrderDetailsActivity.this, "" + responseBody.getMessage());
                        finish();
                    }
                });


            } else {
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(OrderDetailsActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }
    }

    private void setData(OrderDetailResponseData data) {
        tvOrderId.setText(data.getOrder_id());
        tvOrderDate.setText(data.getCreated_date());
        tvAddress.setText(data.getAddress());
        tvSubtotal.setText("Rs." + data.getSub_total());
        tvDeliveryCharges.setText("Rs." + data.getDelivery_charge());
        tvTotal.setText("Rs." + data.getTotal());
        tvPromo.setText("Rs." + data.getPromo_code_discount());
        tvItemNo.setText("No of Products : " + data.getOrder_products().size());
        tvTotalItem.setText("Total(" + data.getOrder_products().size() + " items)");
        switch (data.getOrder_status()) {
            case "1": {
                ivOrderPlaced.setVisibility(View.VISIBLE);
                break;
            }case "2": {
                ivOrderPlaced.setVisibility(View.VISIBLE);

                ivOrderPacked.setVisibility(View.VISIBLE);
                mbCancel.setVisibility(View.GONE);

                break;
            }case "3": {
                ivOrderPlaced.setVisibility(View.VISIBLE);

                ivOrderPacked.setVisibility(View.VISIBLE);

                ivOrderDelivered.setVisibility(View.VISIBLE);
                mbCancel.setVisibility(View.GONE);

                break;
            }case "4": {
                tvOrderId.setText(orderID+"\n(CANCELED)");
                mbCancel.setVisibility(View.GONE);
                break;
            }
        }
    }


    private void setOrdersAdapter(OrderDetailResponseData data) {
        rv_OverviewItems.setAdapter(new ProductOverViewAdapter(this, data));
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
                Utils.ShowToast(OrderDetailsActivity.this, edErrorData.getMessage());
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
}
