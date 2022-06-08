package com.example.harabazar.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.Utilities.CheckLocation;

import com.example.harabazar.Activity.MainActivity;
import com.example.harabazar.Adapter.OffersAdapter;
import com.example.harabazar.R;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.response.OffersResponse;
import com.example.harabazar.Service.response.OffersResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;

import java.util.List;

public class OffersFragment extends Fragment implements OnRequestResponseListener {
    RecyclerView rv_offers;
    ImageView ivBack;
    OffersAdapter offerAdapter;
    String session;
    LoadingFragment loadingFragment = new LoadingFragment();
    NoInternetFragment dialogFragment = new NoInternetFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offers, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rv_offers = view.findViewById(R.id.rv_offers);
        ivBack = view.findViewById(R.id.ivBack);
        session = AppSettings.getSessionKey(getContext());
        boolean b= CheckLocation.isLocationEnabled(getContext());
        if(b==true)
        {
            //   Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Toast.makeText(getApplicationContext(), "disable", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    @Override
    public void onResume() {
        super.onResume();
        loadingFragment.show(getChildFragmentManager(), "");
        methods();
    }

    private void methods() {

        setListners();
        connectorInit();
    }

    private void setListners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.bottomNavigationView.setSelectedItemId(R.id.bottom_home);
            }
        });
    }

    private void connectorInit() {
        Connector connector = new Connector();
        ServerCommunicator.getOffers(connector, session);
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        loadingFragment.dismisss();
        if (webResponse instanceof OffersResponse) {
            final OffersResponse responseBody = (OffersResponse) webResponse;
            AppLogger.e(Utils.getTag(), "Working");

            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        setOfferListAdapter(responseBody.getData());
                    }
                });


            } else {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(getActivity(), "" + responseBody.getMessage());
                    }
                });
            }

        }

    }

    private void setOfferListAdapter(List<OffersResponseData> data) {
        offerAdapter = new OffersAdapter(getContext(), data, null);
        rv_offers.setAdapter(offerAdapter);
        rv_offers.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onUploadComplete(WebResponse webResponse) {

    }

    @Override
    public void onVFRClientException(WebErrorResponse edErrorData) {

        AppLogger.e(Utils.getTag(), edErrorData.getMessage());
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Utils.ShowToast(getActivity(), edErrorData.getMessage());
                }
            });
    }

    @Override
    public void onAuthException() {

    }

    @Override
    public void onNoConnectivityException(String message) {

        if (message.equals("-1")) {
            dialogFragment.show(getChildFragmentManager(), "" + Constants.incrementalID++);
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
}
