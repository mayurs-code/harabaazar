package com.example.harabazar.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.Adapter.OrderListAdapter;
import com.example.harabazar.Fragment.LoadingFragment;
import com.example.harabazar.Fragment.NoInternetFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.GetOrdersRequest;
import com.example.harabazar.Service.response.GetOrdersResponse;
import com.example.harabazar.Service.response.GetOrdersResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.CheckLocation;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;

import java.util.List;

public class MyOrdersActivity extends AppCompatActivity implements OnRequestResponseListener {

    ImageView ivback;
    RecyclerView rvOrderList;
    String session;
    LinearLayout llEmptyCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        rvOrderList = findViewById(R.id.rvOrderList);
        llEmptyCart = findViewById(R.id.llEmpty);
        session = AppSettings.getSessionKey(this);

        initViews();
        boolean b= CheckLocation.isLocationEnabled(MyOrdersActivity.this);
        if(b==true)
        {
            //   Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Toast.makeText(getApplicationContext(), "disable", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(MyOrdersActivity.this);
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
    }

    private void methods() {
        clickListener();
        initConnector();
    }

    private void initConnector() {
        Connector connector = new Connector();
        GetOrdersRequest getOrdersRequest = new GetOrdersRequest();
        getOrdersRequest.setRole("App User");
        ServerCommunicator.getOrdersList(connector, getOrdersRequest, session);
        connector.setOnRequestResponseListener(this);
    }

    private void clickListener() {

        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initViews() {
        ivback = findViewById(R.id.ivBack);
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof GetOrdersResponse) {
            final GetOrdersResponse responseBody = (GetOrdersResponse) webResponse;
            if (responseBody.getStatus()) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingFragment.dismisss();

                        setOrdersAdapter(responseBody.getData());
                    }

                });


            } else {
                this.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(MyOrdersActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }

    }

    private void setOrdersAdapter(List<GetOrdersResponseData> data) {
        if(data.size()<=0)
            llEmptyCart.setVisibility(View.VISIBLE);
        else llEmptyCart.setVisibility(View.GONE);
        OrderListAdapter adapter=new OrderListAdapter(this,data);
        rvOrderList.setAdapter(adapter);
        rvOrderList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onUploadComplete(WebResponse webResponse) {

    }

    @Override
    public void onVFRClientException(WebErrorResponse edErrorData) {
        AppLogger.e(Utils.getTag(), edErrorData.getMessage());
        this.runOnUiThread(new Runnable() {
            public void run() {
                Utils.ShowToast(MyOrdersActivity.this, edErrorData.getMessage());
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
