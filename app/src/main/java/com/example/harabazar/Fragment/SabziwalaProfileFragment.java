package com.example.harabazar.Fragment;

import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.harabazar.Adapter.SabziwalaProductsAdapter;
import com.example.harabazar.BottomSheet.ConfirmAddressBottomSheet;
import com.example.harabazar.BottomSheet.ScheduleDeliveryBottomSheet;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.GetSettingsRequest;
import com.example.harabazar.Service.request.SaveUserRequest;
import com.example.harabazar.Service.response.GetInventoryResponse;
import com.example.harabazar.Service.response.GetInventoryResponseData;
import com.example.harabazar.Service.response.GetSettingsResponse;
import com.example.harabazar.Service.response.GetUsersResponseData;
import com.example.harabazar.Service.response.SaveUserResponse;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class SabziwalaProfileFragment extends DialogFragment implements OnRequestResponseListener {


    private final GetUsersResponseData currentUserData;
    private final List<GetInventoryResponseData> data;
    private final EventHandler handler;
    private final Address address;
    private final int checkOngoing;
    private final LatLng latLng;
    MaterialButton mb_requestNow;
    MaterialButton mb_schedule;
    MaterialCardView mcHawkerSave;
    RecyclerView rv_sabziwalaProducts;
    ImageView ivBack;
    ImageView ivSabziwala;
    TextView tvHawkerName;
    TextView tvHawkerRating;
    TextView infoText;
    TextView tvHawkerId;
    private boolean saved;

    public SabziwalaProfileFragment(List<GetInventoryResponseData> data, GetUsersResponseData currentUserData, boolean saved, EventHandler handler, Address address, LatLng latLng, int checkOngoing) {
        this.data = data;
        this.currentUserData = currentUserData;
        this.saved = saved;
        this.handler = handler;
        this.address = address;
        this.latLng = latLng;
        this.checkOngoing = checkOngoing;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Material);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sabziwala_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mb_requestNow = view.findViewById(R.id.mb_requestNow);
        mb_schedule = view.findViewById(R.id.mb_schedule);
        mcHawkerSave = view.findViewById(R.id.mcHawkerSave);
        tvHawkerName = view.findViewById(R.id.tvHawkerName);
        ivSabziwala = view.findViewById(R.id.ivSabziwala);
        ivBack = view.findViewById(R.id.ivBack);
        infoText = view.findViewById(R.id.infoText);
        tvHawkerRating = view.findViewById(R.id.tvHawkerRating);
        tvHawkerId = view.findViewById(R.id.tvHawkerId);
        rv_sabziwalaProducts = view.findViewById(R.id.rv_sabziwalaProducts);
        super.onViewCreated(view, savedInstanceState);
    }
    LoadingFragment loadingFragment = new LoadingFragment();

    @Override
    public void onResume() {
        super.onResume(); 
        methods();
    }


    private void methods() {
        setSabziwalaProducts();
        getUserSettings(currentUserData.getId());

        onRequestClick();
        setHawkerData();
        if (checkOngoing == -1) {

            onScheduldClick();
        } else {
            mb_requestNow.setVisibility(View.GONE);
            mb_schedule.setVisibility(View.GONE);
        }

        onSave();


    }

    private void setHawkerData() {
        tvHawkerName.setText(currentUserData.getFull_name());
        tvHawkerId.setText("Fruit Cart ID " + currentUserData.getId());
        if (currentUserData.getRating() != null)
            tvHawkerRating.setText(currentUserData.getRating() + " Stars");
        Glide.with(getActivity()).load(Constants.FILES_URL + currentUserData.getProfile_image()).placeholder(R.drawable.logo).into(ivSabziwala);
    }

    private void onSave() {
        if (saved) {
            mcHawkerSave.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary, null));


        } else {
            mcHawkerSave.setCardBackgroundColor(getResources().getColor(R.color.grey, null));

        }
        mcHawkerSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saved) {
                    mcHawkerSave.setCardBackgroundColor(getResources().getColor(R.color.grey, null));
                    saved = false;

                } else {
                    mcHawkerSave.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    saved = true;

                }
                saveUserConnectorInit(currentUserData.getId());
                handler.handle();


            }
        });
    }

    private void saveUserConnectorInit(String uid) {
        Connector connector = new Connector();
        SaveUserRequest request = new SaveUserRequest();
        request.setUser_id(uid);
        ServerCommunicator.saveUser(connector, request, AppSettings.getSessionKey(getActivity()));
        connector.setOnRequestResponseListener(this);
    }private void getUserSettings(String uid) {
        Connector connector = new Connector();
        GetSettingsRequest request = new GetSettingsRequest();
        request.setUser_id(uid);
        ServerCommunicator.getSettings(connector, request, AppSettings.getSessionKey(getActivity()));
        connector.setOnRequestResponseListener(this);
    }


    private void setSabziwalaProducts() {
        SabziwalaProductsAdapter adapter = new SabziwalaProductsAdapter(getActivity(), data);
        rv_sabziwalaProducts.setAdapter(adapter);
        rv_sabziwalaProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    private void onScheduldClick() {
        mb_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScheduleDeliveryBottomSheet scheduleDeliveryBottomSheet = new ScheduleDeliveryBottomSheet(currentUserData.getId(), address,latLng,handler);
                scheduleDeliveryBottomSheet.show(getActivity().getSupportFragmentManager(), "ScheduleDeliveryBottomSheet");
                dismiss();

            }
        });
    }

    private void onRequestClick() {
        mb_requestNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmAddressBottomSheet bottomSheet = new ConfirmAddressBottomSheet(address, latLng, "S " + currentUserData.getId(),handler);
                bottomSheet.show(getActivity().getSupportFragmentManager(), "ConfirmAddressBottomSheet");
                dismiss();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });

    }
//
//    private void openYourOrderHandpickedFragment() {
//        DialogFragment dialogFragment = new HandpickedFragment();
//        dialogFragment.show(getActivity().getSupportFragmentManager(), "Sabziwala2");
//    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        loadingFragment.dismisss();
        if (webResponse instanceof SaveUserResponse) {
            final GetInventoryResponse responseBody = (GetInventoryResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Utils.ShowToast(getActivity(), "" + responseBody.getMessage());
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
        if (webResponse instanceof GetSettingsResponse) {
            final GetSettingsResponse responseBody = (GetSettingsResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        infoText.setText("Handpick Delivery Charges: "+responseBody.getData().getHandpick_delivery_charge()+"Rs.");
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
            dialogFragment.show(getChildFragmentManager(),""+Constants.incrementalID++);
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