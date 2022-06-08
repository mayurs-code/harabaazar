package com.example.harabazar.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.harabazar.Adapter.PreferredSabziwalaAdapter;
import com.example.harabazar.Fragment.LoadingFragment;
import com.example.harabazar.Fragment.NoInternetFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.response.GetSavedUsersResponse;
import com.example.harabazar.Service.response.GetSavedUsersResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.CheckLocation;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;

import java.util.List;

public class PreferredSabziwalaActivity extends AppCompatActivity implements OnRequestResponseListener , EventHandler {

    RecyclerView rvPreferedSabziwalas;
    ImageView ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferred_sabziwala);
        rvPreferedSabziwalas=findViewById(R.id.rvPreferedSabziwalas);
        boolean b= CheckLocation.isLocationEnabled(PreferredSabziwalaActivity.this);
        if(b==true)
        {
            //   Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Toast.makeText(getApplicationContext(), "disable", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(PreferredSabziwalaActivity.this);
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
        ivBack=findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getUserConnectorInit();
    }

    private void getUserConnectorInit() {
        Connector connector = new Connector();
        ServerCommunicator.getSavedUser(connector, AppSettings.getSessionKey(this));
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        loadingFragment.dismisss();
        if (webResponse instanceof GetSavedUsersResponse) {
            final GetSavedUsersResponse responseBody = (GetSavedUsersResponse) webResponse;
            if (responseBody.getStatus()) {
                 runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setPreferedSabziwalaAdapter(responseBody.getData());
                    }
                });


            } else {
                 runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(PreferredSabziwalaActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }

    }



    private void setPreferedSabziwalaAdapter(List<GetSavedUsersResponseData> data) {
        PreferredSabziwalaAdapter adapter=new PreferredSabziwalaAdapter(this,data,this);
        rvPreferedSabziwalas.setAdapter(adapter);
        rvPreferedSabziwalas.setLayoutManager(new LinearLayoutManager(this));
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
        getUserConnectorInit();
    }

    @Override
    public void handle(int position) {

    }

    @Override
    public void handle(String text) {

    }
}