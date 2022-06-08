package com.example.harabazar.BottomSheet;

import android.app.Dialog;
import android.location.Address;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.harabazar.Fragment.LoadingFragment;
import com.example.harabazar.Fragment.OrderScheduledDialogFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.GetProfileRequest;
import com.example.harabazar.Service.request.HandpickRequest;
import com.example.harabazar.Service.request.UpdateAddressRequest;
import com.example.harabazar.Service.response.GetProfileAddressResponse;
import com.example.harabazar.Service.response.GetProfileResponse;
import com.example.harabazar.Service.response.GetProfileResponseData;
import com.example.harabazar.Service.response.HandpickResponse;
import com.example.harabazar.Service.response.UpdateAddressResponse;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ConfirmAddressBottomSheet extends BottomSheetDialogFragment implements OnRequestResponseListener, TextWatcher, EventHandler {

    private final Address address;
    BottomSheetBehavior bottomSheetBehavior;
    MaterialButton mbConfirmLocation;
    TextView tvAddress;
    EditText etAddress;
    EditText etHouseNo;
    LinearLayout llOffers;
    EditText etLandmark;
    EditText etPincode;
    MaterialCardView mcOffers;
    EditText etPromoCode;
    EditText etCity;
    EditText etState;
    EditText etMobile;
    EventHandler handler = null;
    UpdateAddressRequest updateAddressRequest = new UpdateAddressRequest();
    LoadingFragment loadingFragment = new LoadingFragment();
    private Calendar myCalendar;
    private String type = "D";
    private String session;
    private String offer;

    public ConfirmAddressBottomSheet(Address address, LatLng latLng, String type, EventHandler handler) {
        this.address = address;
        this.type = type;
        this.handler = handler;

        updateAddressRequest = new UpdateAddressRequest();
        updateAddressRequest.setAddress_id("");
        updateAddressRequest.setLatitude("" + latLng.latitude);
        updateAddressRequest.setLongitude("" + latLng.longitude);

    }

    public ConfirmAddressBottomSheet(GetProfileAddressResponse updateAddressResponse) {
        address = null;

        this.updateAddressRequest.setAddress_id(updateAddressResponse.getId());
        updateAddressRequest.setAddress(updateAddressResponse.getAddress());
        updateAddressRequest.setAddress_name(updateAddressResponse.getAddress_name());
        updateAddressRequest.setCity(updateAddressResponse.getCity());
        updateAddressRequest.setHouse_number(updateAddressResponse.getHouse_number());
        updateAddressRequest.setLandmark(updateAddressResponse.getLandmark());
        updateAddressRequest.setPincode(updateAddressResponse.getPincode());
        updateAddressRequest.setState(updateAddressResponse.getState());
        updateAddressRequest.setMobile(updateAddressResponse.getMobile());

    }

    public ConfirmAddressBottomSheet() {
        address = null;
        updateAddressRequest.setAddress_id("");

    }


    public ConfirmAddressBottomSheet(Address address, LatLng latLng, String type, Calendar myCalendar, EventHandler handler) {
        this.address = address;
        this.type = type;
        this.handler = handler;
        this.myCalendar = myCalendar;
        updateAddressRequest = new UpdateAddressRequest();
        updateAddressRequest.setAddress_id("");
        updateAddressRequest.setLatitude("" + latLng.latitude);
        updateAddressRequest.setLongitude("" + latLng.longitude);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_confirm_address, null);
        bottomSheet.setContentView(view);
        bottomSheetBehavior = BottomSheetBehavior.from((View) (view.getParent()));
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        mbConfirmLocation = view.findViewById(R.id.mbConfirmLocation);
        etAddress = view.findViewById(R.id.etAddress);
        etHouseNo = view.findViewById(R.id.etHouseNo);
        etLandmark = view.findViewById(R.id.etLandmark);
        etPincode = view.findViewById(R.id.etPincode);
        etCity = view.findViewById(R.id.etCity);
        etPromoCode = view.findViewById(R.id.etPromoCode);
        mcOffers = view.findViewById(R.id.mcOffers);
        llOffers = view.findViewById(R.id.llOffers);
        etCity = view.findViewById(R.id.etCity);
        etState = view.findViewById(R.id.etState);
        etMobile = view.findViewById(R.id.etMobile);
        tvAddress = view.findViewById(R.id.tvAddress);
        session = AppSettings.getSessionKey(getActivity());
        return bottomSheet;
    }

    @Override
    public void onResume() {
        super.onResume();
        methods();
    }

    private void methods() {
        if (type.startsWith("S")) {
            setUneditableFields();
        }
        if (address != null) {
            setDataToRequest();
            setDataToFields();

        } else if (updateAddressRequest != null) {
            setDataToFields();
        }
        getDataFromFields();
        listners();
    }

    private void setUneditableFields() {
//        etAddress.setEnabled(false);
//        etCity.setEnabled(false);
//        etPincode.setEnabled(false);
//        etState.setEnabled(false);
//        etMobile.setEnabled(false);
        llOffers.setVisibility(View.VISIBLE);
//        etAddress.setAlpha(0.5f);
//        etCity.setAlpha(0.5f);
//        etPincode.setAlpha(0.5f);
//        etState.setAlpha(0.5f);
//        etMobile.setAlpha(0.5f);
        mbConfirmLocation.setText("Confirm Location");
    }

    private void setDataToRequest() {
        updateAddressRequest.setAddress(address.getAddressLine(0));
        updateAddressRequest.setAddress_name("ADDRESS");
        updateAddressRequest.setCity(address.getSubAdminArea());
        updateAddressRequest.setHouse_number("");
        updateAddressRequest.setLandmark(address.getFeatureName());
        updateAddressRequest.setPincode(address.getPostalCode());
        updateAddressRequest.setState(address.getAdminArea());
        updateAddressRequest.setMobile(AppSettings.getPhone(getActivity()));
    }

    private void setDataToFields() {
        etAddress.setText(updateAddressRequest.getAddress());
        etCity.setText(updateAddressRequest.getCity());
        etHouseNo.setText(updateAddressRequest.getHouse_number());
        etLandmark.setText(updateAddressRequest.getLandmark());
        etPincode.setText(updateAddressRequest.getPincode());
        etState.setText(updateAddressRequest.getState());
        etMobile.setText(updateAddressRequest.getMobile());
    }

    private void getDataFromFields() {
        updateAddressRequest.setAddress(etAddress.getText().toString().trim());
        updateAddressRequest.setAddress_name("ADDRESS");
        updateAddressRequest.setCity(etCity.getText().toString().trim());
        updateAddressRequest.setHouse_number(etHouseNo.getText().toString().trim());
        updateAddressRequest.setLandmark(etLandmark.getText().toString().trim());
        updateAddressRequest.setPincode(etPincode.getText().toString().trim());
        updateAddressRequest.setState(etState.getText().toString().trim());
        updateAddressRequest.setMobile(etMobile.getText().toString().trim());
        tvAddress.setText(updateAddressRequest.getHouse_number() + ", " +
                updateAddressRequest.getAddress() + ", " +
                updateAddressRequest.getCity() + ", " +
                updateAddressRequest.getState());

    }

    private void initConnector(String address) {
        Connector connector = new Connector();
        updateAddressRequest.setMobile(AppSettings.getPhone(getActivity()));
        ServerCommunicator.addUpdateAddress(connector, updateAddressRequest, session);
        connector.setOnRequestResponseListener(this);
    }

    private void listners() {
        etAddress.addTextChangedListener(this);
        etHouseNo.addTextChangedListener(this);
        etLandmark.addTextChangedListener(this);
        etPincode.addTextChangedListener(this);
        etCity.addTextChangedListener(this);
        etState.addTextChangedListener(this);
        etMobile.addTextChangedListener(this);
        mbConfirmLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etAddress.getText().toString().trim().isEmpty()) {
                    Utils.ShowToast(getActivity(), "Empty Address Field");
                } /*else if (etHouseNo.getText().toString().trim().isEmpty()) {
                    Utils.ShowToast(getActivity(), "Empty House No Field");
                } else if (etLandmark.getText().toString().trim().isEmpty()) {
                    Utils.ShowToast(getActivity(), "Empty Landmark Field");
                }*/ else if (etPincode.getText().toString().trim().isEmpty()) {
                    Utils.ShowToast(getActivity(), "Empty Pincode Field");
                } else if (etCity.getText().toString().trim().isEmpty()) {
                    Utils.ShowToast(getActivity(), "Empty City Field");
                } else if (etState.getText().toString().trim().isEmpty()) {
                    Utils.ShowToast(getActivity(), "Empty State Field");
                } else if (etMobile.getText().toString().trim().isEmpty()) {
                    Utils.ShowToast(getActivity(), "Empty Mobile Field");
                } else if (etMobile.getText().toString().length() != 10) {
                    Utils.ShowToast(getActivity(), "Wrong Mobile Number ");
                } else if (!tvAddress.getText().toString().trim().isEmpty()) {
                    loadingFragment.show(getChildFragmentManager(), "");

                    if (type.startsWith("S")) {
                        requestHandPickConnectorInit(tvAddress.getText().toString());

                    } else {
                        initConnector(tvAddress.getText().toString());



                    }

                } else {
                    Utils.ShowToast(getActivity(), "Empty Address Field");
                }
            }
        });
        mcOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OffersFragmentHandpickBottom bottom = new OffersFragmentHandpickBottom(ConfirmAddressBottomSheet.this,1);
                bottom.show(getChildFragmentManager(), ConfirmAddressBottomSheet.this.getClass().getSimpleName());
            }
        });
    }

    private void getProfileData() {
        getProfileConnectorInit();
    }

    private void getProfileConnectorInit() {
        Connector connector = new Connector();
        GetProfileRequest getProfileRequest = new GetProfileRequest();
        getProfileRequest.setUser_id(AppSettings.getUId(getActivity()));
        ServerCommunicator.getProfile(connector, getProfileRequest, session);
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        loadingFragment.dismiss();
        if (webResponse instanceof UpdateAddressResponse) {
            final UpdateAddressResponse responseBody = (UpdateAddressResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (type.startsWith("S")) {

                            getProfileData();
                        }else {
                            dismiss();
                            getActivity().onBackPressed();
                        }
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


        if (webResponse instanceof GetProfileResponse) {
            final GetProfileResponse responseBody = (GetProfileResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        getDefaltAddress(responseBody.getData());
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
        if (webResponse instanceof HandpickResponse) {
            final HandpickResponse responseBody = (HandpickResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Utils.ShowToast(getActivity(), type.split(" ")[0] + "" + responseBody.getMessage());

                        if (type.split(" ")[0].length() > 1) {
                            handler.handle();

                            openOrderScheduled(responseBody.getOrder_id());
                            dismiss();
                        } else {
                            handler.handle();
                            openOrderScheduled(responseBody.getOrder_id());

                            dismiss();
                        }
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

    private void openOrderScheduled(String text) {
        OrderScheduledDialogFragment dialogFragment = new OrderScheduledDialogFragment(text);

        dialogFragment.show(getActivity().getSupportFragmentManager(), "OrderScheduledDialogFragment");

    }

    private void getDefaltAddress(GetProfileResponseData data) {
        for (int i = 0; i < data.getAddress().size(); i++) {
            GetProfileAddressResponse userAddress = data.getAddress().get(i);
            if (userAddress.getIs_default() == 1) {
                requestHandPickConnectorInit(userAddress.getId());
                break;
            }
        }
    }

    private void requestHandPickConnectorInit(String address) {
        Connector connector = new Connector();
        HandpickRequest handpickRequest = new HandpickRequest();
        handpickRequest.setUser_id(type.split(" ")[1]);
        handpickRequest.setAddress(address);
        if (updateAddressRequest != null) {
            handpickRequest.setLag(updateAddressRequest.getLongitude());
            handpickRequest.setLat(updateAddressRequest.getLatitude());
        }
        if (myCalendar != null) {
            SimpleDateFormat time = new SimpleDateFormat("HH:mm:SS");
            SimpleDateFormat date = new SimpleDateFormat("y-M-d");
            handpickRequest.setSchedule_date(date.format(myCalendar.getTime()));
            handpickRequest.setSchedule_time(time.format(myCalendar.getTime()));
        }
        if (etPromoCode.getText() != null) {
            if (!etPromoCode.getText().toString().isEmpty()) {
                handpickRequest.setOffer_id(offer);
            }
        }
        ServerCommunicator.requestHandpick(connector, handpickRequest, session);
        connector.setOnRequestResponseListener(this);
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        getDataFromFields();

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void handle() {

    }

    @Override
    public void handle(int position) {

    }

    @Override
    public void handle(String text) {
        this.offer = text.split(":")[0];
        etPromoCode.setText(text.split(":")[1]);
    }
}
