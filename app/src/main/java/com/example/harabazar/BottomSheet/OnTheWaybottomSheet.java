package com.example.harabazar.BottomSheet;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.example.harabazar.Activity.OrderDetailsActivity;
import com.example.harabazar.Fragment.HandpickedFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.CancelOrderRequest;
import com.example.harabazar.Service.response.CancelOrderResponse;
import com.example.harabazar.Service.response.GetOrdersResponseData;
import com.example.harabazar.Service.response.GetUsersResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;


public class OnTheWaybottomSheet extends BottomSheetDialogFragment implements OnRequestResponseListener {
    private final GetOrdersResponseData getOrdersResponseData;
    private final GetUsersResponseData getUsersResponseData;
    BottomSheetBehavior bottomSheetBehavior;
    MaterialButton mb_cancel;
    ImageView ivUserProfile;
    TextView tvHawkerName;
    TextView tvHawkerId;
    TextView tvHawkerRating;
    TextView tvArriving;

    public OnTheWaybottomSheet(GetOrdersResponseData getOrdersResponseData, GetUsersResponseData getUsersResponseData) {
        this.getOrdersResponseData = getOrdersResponseData;
        this.getUsersResponseData = getUsersResponseData;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_arriving_driver, null);
        bottomSheet.setContentView(view);
        bottomSheetBehavior = BottomSheetBehavior.from((View) (view.getParent()));
        mb_cancel = view.findViewById(R.id.mbCancel);
        tvArriving = view.findViewById(R.id.tvArriving);
        tvHawkerName = view.findViewById(R.id.tvHawkerName);
        tvHawkerId = view.findViewById(R.id.tvHawkerId);
        tvHawkerRating = view.findViewById(R.id.tvHawkerRating);
        ivUserProfile = view.findViewById(R.id.ivUserProfile);
        methods();
        return bottomSheet;
    }

    private void methods() {
        onCheckInventory();
        setData();
    }

    private void setData() {
        tvHawkerName.setText(getUsersResponseData.getFull_name());
        tvHawkerId.setText("Fruit Cart ID " + getUsersResponseData.getId());
        tvHawkerRating.setText(getUsersResponseData.getRating() + " Star");
        tvArriving.setText("Arriving Shortly");
        Glide.with(getActivity()).load(Constants.FILES_URL + getUsersResponseData.getProfile_image()).into(ivUserProfile);
    }

    private void onCheckInventory() {
        mb_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                openYourOrderHandpickedFragment();
                CancelOrderRequest request=new CancelOrderRequest();
                request.setCancel_reason("Testing Cancel");
                request.setOrder_id(getOrdersResponseData.getOrder_id());
                connectorInit(request);
            }
        });
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    private void openYourOrderHandpickedFragment() {
        DialogFragment dialogFragment = new HandpickedFragment(getOrdersResponseData,getUsersResponseData);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "Sabziwala2");
    }
    private void connectorInit(CancelOrderRequest request) {
        Connector connector = new Connector();
        ServerCommunicator.cancelOrder(connector, request, AppSettings.getSessionKey(getActivity()));
        connector.setOnRequestResponseListener(this);
    }
    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof CancelOrderResponse) {
            final CancelOrderResponse responseBody = (CancelOrderResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.ShowToast(getActivity(), "" + responseBody.getMessage());
                        dismiss();
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

    @Override
    public void onNoConnectivityException(String message) {

    }

    @Override
    public void onNoCachedDataAvailable() {

    }
}
