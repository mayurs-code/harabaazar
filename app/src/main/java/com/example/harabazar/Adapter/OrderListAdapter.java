package com.example.harabazar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.Activity.OrderDetailsActivity;
import com.example.harabazar.Activity.OrderMapActivity;
import com.example.harabazar.R;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.GetProfileRequest;
import com.example.harabazar.Service.response.GetOrdersResponseData;
import com.example.harabazar.Service.response.GetProfileResponse;
import com.example.harabazar.Service.response.GetProfileResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Utils;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder> implements OnRequestResponseListener {
    private final List<GetOrdersResponseData> data;
    Context context;
    int current = -1;

    public OrderListAdapter(Context context, List<GetOrdersResponseData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order_list, parent, false));
    }

    private void connectorGetUserProfile(String id) {
        //        Connector connector = Connector.getConnector();
        Connector connector = new Connector();
        GetProfileRequest getProfileRequest = new GetProfileRequest();
        getProfileRequest.setUser_id(id);
        ServerCommunicator.getProfile(connector, getProfileRequest, AppSettings.getSessionKey(context));
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvOrderNo.setText("Order ID: " + data.get(position).getOrder_id());

        AnimationClass.setAnimationParent(holder.itemView);

        switch (data.get(position).getOrder_status()) {
            case "1": {
                holder.tvTitle.setText("Order Placed");

                break;
            }
            case "2": {
                holder.tvTitle.setText("Order Packed");

                break;
            }
            case "3": {
                holder.tvTitle.setText("Order Delivered");

                break;
            }
            case "4": {
                holder.tvTitle.setText("Order Canceled");

                break;
            }
        }
        holder.tvStatus.setText("Status: " + data.get(position).getStatus_name());
        if (data.get(position).getOrder_type().equals("2")) {
            holder.ivOrderImage.setImageResource(R.drawable.rickshaw);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    current = (position);
                    connectorGetUserProfile(data.get(position).getSabjiwala_id());

                }
            });

        } else {
            holder.ivOrderImage.setImageResource(R.drawable.cart_bottom);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent I = new Intent(context, OrderDetailsActivity.class);
                    I.putExtra("order_id", data.get(position).getOrder_id());
                    context.startActivity(I);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof GetProfileResponse) {
            final GetProfileResponse responseBody = (GetProfileResponse) webResponse;
            if (responseBody.getStatus()) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (current != -1)
                            gotoOrderMapActivity(responseBody.getData());
                    }
                });


            } else {
                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(context, "" + responseBody.getMessage());
                    }
                });
            }
        }
    }

    private void gotoOrderMapActivity(GetProfileResponseData getProfileResponseData) {
        Intent i = new Intent(context, OrderMapActivity.class);
        i.putExtra("GetProfileResponseData", getProfileResponseData);
        i.putExtra("GetOrdersResponseData", data.get(current));
        current=-1;
        context.startActivity(i);

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

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvStatus;
        TextView tvOrderNo;
        TextView tvTitle;
        ImageView ivOrderImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvOrderNo = itemView.findViewById(R.id.tvOrderNo);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            ivOrderImage = itemView.findViewById(R.id.ivOrderImage);
        }
    }
}


