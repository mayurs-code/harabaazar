package com.example.harabazar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.harabazar.Fragment.SabziwalaProfileFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.GetInventoryRequest;
import com.example.harabazar.Service.request.SaveUserRequest;
import com.example.harabazar.Service.response.GetInventoryResponse;
import com.example.harabazar.Service.response.GetInventoryResponseData;
import com.example.harabazar.Service.response.GetSavedUsersResponseData;
import com.example.harabazar.Service.response.GetUsersResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;

import java.util.List;

public class PreferredSabziwalaAdapter extends RecyclerView.Adapter<PreferredSabziwalaAdapter.ViewHolder> implements OnRequestResponseListener {

    private final List<GetSavedUsersResponseData> data;
    Context context;
    EventHandler handler;
    GetUsersResponseData user;

    public PreferredSabziwalaAdapter(Context context, List<GetSavedUsersResponseData> data, EventHandler handler) {
        this.context = context;
        this.data = data;
        this.handler = handler;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_preferred_sabziwala, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvHawkerName.setText(data.get(position).getFull_name());
        holder.tvHawkerId.setText("Fruit Cart ID" + data.get(position).getId());
        holder.tvHawkerRating.setText(data.get(position).getPhone_number());
        AnimationClass.setAnimationParent(holder.itemView);
        AnimationClass.setAnimationChildZoom(holder.ivHawker);

        try {
            Glide.with(context).load(Constants.FILES_URL + data.get(position).getProfile_image()).into(holder.ivHawker);
        } catch (Exception e) {

        }
        holder.mcHawkerRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserConnectorInit(data.get(position).getId());
                data.remove(position);
                notifyDataSetChanged();

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = new GetUsersResponseData();
                user.setFull_name(data.get(position).getFull_name());
                user.setId(data.get(position).getId());
                user.setProfile_image(data.get(position).getProfile_image());
                getInventoryConnectorInit(data.get(position).getId());

            }
        });
    }

    private void getInventoryConnectorInit(String uid) {
        Connector connector = new Connector();
        GetInventoryRequest request = new GetInventoryRequest();
        request.setUser_id(uid);
        request.setLimit("10000");

        ServerCommunicator.getInventory(connector, request, AppSettings.getSessionKey(context));
        connector.setOnRequestResponseListener(this);
    }

    private void openInventory(List<GetInventoryResponseData> data) {
        SabziwalaProfileFragment dialogFragment = new SabziwalaProfileFragment(data, user, true, handler, null, null, 1);
        dialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "SabziwalaProfileFragment");
    }

    private void saveUserConnectorInit(String uid) {
        Connector connector = new Connector();
        SaveUserRequest request = new SaveUserRequest();
        request.setUser_id(uid);
        ServerCommunicator.saveUser(connector, request, AppSettings.getSessionKey(context));
        connector.setOnRequestResponseListener(this);
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
        if (webResponse instanceof GetInventoryResponse) {
            final GetInventoryResponse responseBody = (GetInventoryResponse) webResponse;
            if (responseBody.getStatus()) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openInventory(responseBody.getData());
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

        TextView tvHawkerName;
        TextView tvHawkerId;
        TextView tvHawkerRating;
        ImageView ivHawker;
        CardView mcHawkerRemove;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHawkerName = itemView.findViewById(R.id.tvHawkerName);
            tvHawkerId = itemView.findViewById(R.id.tvHawkerId);
            tvHawkerRating = itemView.findViewById(R.id.tvHawkerRating);
            ivHawker = itemView.findViewById(R.id.ivHawker);
            mcHawkerRemove = itemView.findViewById(R.id.mcHawkerRemove);
        }
    }
}


