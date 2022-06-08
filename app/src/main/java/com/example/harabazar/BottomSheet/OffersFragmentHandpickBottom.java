package com.example.harabazar.BottomSheet;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.Adapter.OffersAdapter;
import com.example.harabazar.Adapter.OffersAdapterBottom;
import com.example.harabazar.Fragment.LoadingFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.response.GetHandpickOffersResponse;
import com.example.harabazar.Service.response.GetHandpickOffersResponseData;
import com.example.harabazar.Service.response.OffersResponse;
import com.example.harabazar.Service.response.OffersResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class OffersFragmentHandpickBottom extends BottomSheetDialogFragment implements OnRequestResponseListener, EventHandler {
    BottomSheetBehavior bottomSheetBehavior;
    EventHandler handler;
    int i;
    RecyclerView rv_offers;
    LoadingFragment loadingFragment = new LoadingFragment();

    public OffersFragmentHandpickBottom(EventHandler handler, int i) {
        this.handler = handler;
        this.i = i;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_offers, null);
        bottomSheet.setContentView(view);
        bottomSheetBehavior = BottomSheetBehavior.from((View) (view.getParent()));
        rv_offers = view.findViewById(R.id.rv_offers);
        methods();
        return bottomSheet;
    }

    private void methods() {
        loadingFragment.show(getChildFragmentManager(), getClass().getSimpleName());
        if (i == 1) {
            connectorInit1();

        } else {
            connectorInit2();

        }
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    private void connectorInit1() {
        Connector connector = new Connector();
        ServerCommunicator.getOffersHandpick(connector, AppSettings.getSessionKey(getContext()));
        connector.setOnRequestResponseListener(this);
    }

    private void connectorInit2() {
        Connector connector = new Connector();
        ServerCommunicator.getOffers(connector, AppSettings.getSessionKey(getContext()));
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        loadingFragment.dismisss();
        if (webResponse instanceof GetHandpickOffersResponse) {
            final GetHandpickOffersResponse responseBody = (GetHandpickOffersResponse) webResponse;

            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setOfferListAdapter1(responseBody.getData());

                    }
                });


            } else {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(getActivity(), "" + responseBody.getMessage());
                    }
                });
            }

        }if (webResponse instanceof OffersResponse) {
            final OffersResponse responseBody = (OffersResponse) webResponse;

            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setOfferListAdapter2(responseBody.getData());

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

    private void setOfferListAdapter1(List<GetHandpickOffersResponseData> data) {
        OffersAdapterBottom offerAdapter = new OffersAdapterBottom(getContext(), data, handler, this);
        rv_offers.setAdapter(offerAdapter);
        rv_offers.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void setOfferListAdapter2(List<OffersResponseData> data) {
        OffersAdapter offerAdapter = new OffersAdapter(getContext(), data, handler );
        rv_offers.setAdapter(offerAdapter);
        rv_offers.setLayoutManager(new LinearLayoutManager(getContext()));
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

    @Override
    public void handle() {
        dismiss();
    }

    @Override
    public void handle(int position) {

    }

    @Override
    public void handle(String text) {
        dismiss();

    }
}
