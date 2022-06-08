package com.example.harabazar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.BottomSheet.ConfirmAddressBottomSheet;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.DeleteAddressRequest;
import com.example.harabazar.Service.request.UpdateAddressRequest;
import com.example.harabazar.Service.response.DeleteAddressResponse;
import com.example.harabazar.Service.response.GetProfileResponseData;
import com.example.harabazar.Service.response.UpdateAddressResponse;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Utils;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> implements OnRequestResponseListener {
    private final FragmentManager supportFragmentManager;
    Context context;
    GetProfileResponseData address;
    String session;
    UpdateAddressRequest updateAddressRequest = new UpdateAddressRequest();
    EventHandler eventHandler;

    public AddressAdapter(Context context, GetProfileResponseData address, FragmentManager supportFragmentManager, EventHandler eventHandler) {
        this.context = context;
        this.address = address;
        this.supportFragmentManager = supportFragmentManager;
        this.eventHandler = eventHandler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_address, null));

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        session = AppSettings.getSessionKey(context);
        AnimationClass.setAnimationParent(holder.itemView);

        holder.homeAddress.setText(address.getAddress().get(position).getHouse_number() + ", " + address.getAddress().get(position).getAddress());
        if (address.getAddress().get(position).getIs_default() == 1) {
            holder.tvAddress.setText("Address (Default)");
            holder.cbDefault.setChecked(true);
            holder.cbDefault.setText("Default");

        } else {
            holder.tvAddress.setText("Address ");
            holder.cbDefault.setChecked(false);
            holder.cbDefault.setText("Set Default");
        }
        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                connectorInit(position);
                address.getAddress().remove(position);
                notifyDataSetChanged();

            }
        });
        holder.buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmAddressBottomSheet bottomSheet = new ConfirmAddressBottomSheet(address.getAddress().get(position));
                bottomSheet.show(supportFragmentManager, "ConfirmAddressBottomSheet");

            }
        });
        holder.cbDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (holder.cbDefault.isChecked()) {
                    updateAddressRequest.setAddress_id(address.getAddress().get(position).getId());
                    updateAddressRequest.setAddress(address.getAddress().get(position).getAddress());
                    updateAddressRequest.setAddress_name(address.getAddress().get(position).getAddress_name());
                    updateAddressRequest.setCity(address.getAddress().get(position).getCity());
                    updateAddressRequest.setHouse_number(address.getAddress().get(position).getHouse_number());
                    updateAddressRequest.setLandmark(address.getAddress().get(position).getLandmark());
                    updateAddressRequest.setPincode(address.getAddress().get(position).getPincode());
                    updateAddressRequest.setState(address.getAddress().get(position).getState());
                    updateAddressRequest.setMobile(address.getAddress().get(position).getMobile());
                    initDefaultConnector();
                }
            }
        });

    }

    private void initDefaultConnector() {
        Connector connector = new Connector();
        updateAddressRequest.setMobile(AppSettings.getPhone(context));
        ServerCommunicator.addUpdateAddress(connector, updateAddressRequest, session);
        connector.setOnRequestResponseListener(this);
    }

    private void connectorInit(int position) {
        Connector connector = new Connector();
        DeleteAddressRequest deleteAddressRequest = new DeleteAddressRequest();
        deleteAddressRequest.setAddress_id(address.getAddress().get(position).getId());
        ServerCommunicator.deleteAddress(connector, deleteAddressRequest, session);
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public int getItemCount() {
        return address.getAddress().size();
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof DeleteAddressResponse) {
            final DeleteAddressResponse responseBody = (DeleteAddressResponse) webResponse;
            if (responseBody.getStatus()) {
                Log.d("status", "onHttpResponse: " + responseBody.getStatus());
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }
        if (webResponse instanceof UpdateAddressResponse) {
            final UpdateAddressResponse responseBody = (UpdateAddressResponse) webResponse;
            if (responseBody.getStatus()) {
                Log.d("status", "onHttpResponse: " + responseBody.getStatus());
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        eventHandler.handle();
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
        AppLogger.e(Utils.getTag(), edErrorData.getMessage());
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {
                Utils.ShowToast(((Activity) context), edErrorData.getMessage());
            }
        });
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView homeAddress;
        TextView tvAddress;
        CardView buttonDelete;
        CardView buttonEdit;
        RadioButton cbDefault;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            homeAddress = itemView.findViewById(R.id.homeAddress);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            cbDefault = itemView.findViewById(R.id.cbDefault);
        }
    }
}