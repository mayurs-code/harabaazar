package com.example.harabazar.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.Adapter.AddressAdapter;
import com.example.harabazar.BottomSheet.ConfirmAddressBottomSheet;
import com.example.harabazar.Fragment.LoadingFragment;
import com.example.harabazar.Fragment.NoInternetFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.GetProfileRequest;
import com.example.harabazar.Service.response.GetProfileResponse;
import com.example.harabazar.Service.response.GetProfileResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.CheckLocation;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.button.MaterialButton;

public class AddressActivity extends AppCompatActivity implements OnRequestResponseListener, EventHandler {

    RecyclerView rv_address;
    MaterialButton mbAddAddress;
    MaterialButton mbAddAddressManual;
    ImageView ivBack;
    LinearLayout llEmpty;
    String session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        session = AppSettings.getSessionKey(this);
        boolean b= CheckLocation.isLocationEnabled(AddressActivity.this);
        if(b==true)
        {
            //Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddressActivity.this);
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
        initViews();
        listners();
    }
    LoadingFragment loadingFragment = new LoadingFragment();

    @Override
    public void onResume() {
        super.onResume();
        methods();
    }

    private void methods() {
        checkPermissions();
        connectorInit();

    }


    private void initViews() {
        rv_address = findViewById(R.id.rv_address);
        ivBack = findViewById(R.id.ivBack);
        llEmpty = findViewById(R.id.llEmpty);
        mbAddAddress = findViewById(R.id.mbAddAddress);
        mbAddAddressManual = findViewById(R.id.mbAddAddressManual);

    }

    private void listners() {
        mbAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddressActivity.this, AddAddressMapActivity.class);
                startActivity(i);


            }
        });
        mbAddAddressManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConfirmAddressBottomSheet bottomSheet = new ConfirmAddressBottomSheet();
                bottomSheet.show(getSupportFragmentManager(), "ConfirmAddressBottomSheet");


            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }

    private void checkPermissions() {
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (permissionGranted) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    private void connectorInit() {
        if(!loadingFragment.isAdded())
            loadingFragment.show(getSupportFragmentManager(), getClass().getSimpleName());
        Connector connector = new Connector();
        GetProfileRequest getProfileRequest = new GetProfileRequest();
        getProfileRequest.setUser_id(AppSettings.getUId(this));
        ServerCommunicator.getProfile(connector, getProfileRequest, session);
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof GetProfileResponse) {
            loadingFragment.dismisss();

            final GetProfileResponse responseBody = (GetProfileResponse) webResponse;
            if (responseBody.getStatus()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAddressAdapter(responseBody.getData());
                    }
                });

            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(AddressActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }
    }

    private void setAddressAdapter(GetProfileResponseData data) {
        if (data.getAddress().size() <= 0)
            llEmpty.setVisibility(View.VISIBLE);
        else llEmpty.setVisibility(View.GONE);
        rv_address.setLayoutManager(new LinearLayoutManager(this));
        rv_address.setAdapter(new AddressAdapter(this, data, getSupportFragmentManager(),this));

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
