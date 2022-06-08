package com.example.harabazar.Activity;

import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.Adapter.ItemDailyAdapter;
import com.example.harabazar.BottomSheet.ConfirmAddressBottomSheet;
import com.example.harabazar.Fragment.LoadingFragment;
import com.example.harabazar.Fragment.NoInternetFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.DailyEventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.GetInventoryRequest;
import com.example.harabazar.Service.request.UpdateInventoryRequest;
import com.example.harabazar.Service.response.GetInventoryResponse;
import com.example.harabazar.Service.response.GetInventoryResponseData;
import com.example.harabazar.Service.response.UpdateInventoryResponse;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class DailyListActivity extends AppCompatActivity implements OnRequestResponseListener, DailyEventHandler {

    int currentCall = 0;
    LoadingFragment loadingFragment = new LoadingFragment();
    NoInternetFragment dialogFragment = new NoInternetFragment();
    private List<GetInventoryResponseData> allList;
    private String selected;
    private RecyclerView rvInventory;
    private MaterialButton mbAdd;
    private CheckBox cbCheck;
    private ImageView ivBack;
    private LatLng latLng;
    private Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_list);
        latLng = new LatLng(Double.parseDouble(getIntent().getStringExtra("latLng")), Double.parseDouble(getIntent().getStringExtra("latLng")));
        address = getIntent().getStringExtra("address");
        rvInventory = findViewById(R.id.rvInventory);
        cbCheck = findViewById(R.id.cbCheck);
        ivBack = findViewById(R.id.ivBack);
        mbAdd = findViewById(R.id.mbAdd);
        currentCall = 0;

    }

    @Override
    public void onResume() {
        super.onResume();
        loadingFragment.show(getSupportFragmentManager(), "");
        methods();
    }

    private void methods() {
        getAllInventoryConnectorInit();
        listners();
    }

    private void listners() {
        mbAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInventoryConnectorInit();
                ConfirmAddressBottomSheet bottomSheet = new ConfirmAddressBottomSheet(address, latLng, "S " + currentUserData.getId(), handler);
                bottomSheet.show(getSupportFragmentManager(), "ConfirmAddressBottomSheet");
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void getAllInventoryConnectorInit() {
//        Connector connector = Connector.getConnector();
        Connector connector = new Connector();

        GetInventoryRequest request = new GetInventoryRequest();
        request.setUser_id(AppSettings.getUId(this));
        ServerCommunicator.getAllInventory(connector, request, AppSettings.getSessionKey(this));
        connector.setOnRequestResponseListener(this);
    }

    private void updateInventoryConnectorInit() {
//        Connector connector = Connector.getConnector();
        Connector connector = new Connector();
        UpdateInventoryRequest request = new UpdateInventoryRequest();
        request.setInventory_product_id(selected);
        ServerCommunicator.updateInventory(connector, request, AppSettings.getSessionKey(this));
        connector.setOnRequestResponseListener(this);
    }

    private void getDailyInventoryConnectorInit() {
        currentCall = 1;
//        Connector connector = Connector.getConnector();
        Connector connector = new Connector();
        GetInventoryRequest request = new GetInventoryRequest();
        request.setUser_id(AppSettings.getUId(this));
        request.setLimit("10000");

        ServerCommunicator.getInventory(connector, request, AppSettings.getSessionKey(this));
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof GetInventoryResponse) {
            final GetInventoryResponse responseBody = (GetInventoryResponse) webResponse;
            if (responseBody.getStatus()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingFragment.dismisss();
                        if (currentCall == 0) {
                            allList = responseBody.getData();
                            getDailyInventoryConnectorInit();
                        } else {
                            setList(allList, responseBody.getData());
                            cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                    if (b) {
                                        setList(allList, allList);
                                    } else {
                                        setList(allList, new ArrayList<>());

                                    }
                                }
                            });
                        }
                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(DailyListActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof UpdateInventoryResponse) {
            final UpdateInventoryResponse responseBody = (UpdateInventoryResponse) webResponse;
            if (responseBody.getStatus()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.ShowToast(DailyListActivity.this, "" + responseBody.getMessage());
                        onBackPressed();


                    }
                });
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(DailyListActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }
    }

    private void setList(List<GetInventoryResponseData> allList, List<GetInventoryResponseData> data) {
        currentCall = 0;
        ItemDailyAdapter adapter = new ItemDailyAdapter(this, allList, data, this);
        rvInventory.setAdapter(adapter);
        rvInventory.setLayoutManager(new LinearLayoutManager(this));

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

        if (message.equals("-1")) {
            dialogFragment.show(getSupportFragmentManager(), "" + Constants.incrementalID++);
        }
        if (message.equals("1")) {
            try {
                dialogFragment.dismiss();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onNoCachedDataAvailable() {

    }

    @Override
    public void setSelected(String str) {

        this.selected = str.substring(1, str.length() - 1);
    }
}