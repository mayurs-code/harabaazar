package com.example.harabazar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.AddRemoveProductRequest;
import com.example.harabazar.Service.response.AddRemoveProductResponse;
import com.example.harabazar.Service.response.CartProductListResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.card.MaterialCardView;

public class CartVariantAdapter extends RecyclerView.Adapter<CartVariantAdapter.ViewHolder> implements OnRequestResponseListener {
    private final Context context;
    private final EventHandler handler;
    private final CartProductListResponseData data;

    public CartVariantAdapter(Context context, EventHandler handler, CartProductListResponseData cartProductListResponseData) {
        this.context = context;
        this.handler = handler;
        this.data = cartProductListResponseData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_varient_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(data.getVariants().get(position).getCart_quantity()==null){
            holder.itemView.setVisibility(View.GONE);
        }
        AnimationClass.setAnimationParent(holder.itemView);

        holder.tvQty.setText(data.getVariants().get(position).getCart_quantity());
        holder.tvVariant.setText(data.getVariants().get(position).getSize() + data.getVariants().get(position).getUnit() + " QTY: " + data.getVariants().get(position).getCart_quantity());
        holder.tvPrice.setText(data.getVariants().get(position).getPrice());
        holder.cvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.getVariants().get(position).setCart_quantity("" + (Integer.parseInt(data.getVariants().get(position).getCart_quantity()) - 1));

                initConnector(data.getId(), data.getVariants().get(position).getId(),"0");
                if (data.getVariants().get(position).getCart_quantity().equals("0")) {
                    data.getVariants().remove(position);
                }

                notifyDataSetChanged();
            }
        });
        holder.cvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.getVariants().get(position).setCart_quantity("" + (Integer.parseInt(data.getVariants().get(position).getCart_quantity()) + 1));
                initConnector(data.getId(), data.getVariants().get(position).getId(), "1");
                notifyDataSetChanged();

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.getVariants().size();
    }

    private void initConnector(String pid, String id, String action) {
        Connector connector = new Connector();
        AddRemoveProductRequest request = new AddRemoveProductRequest();
        request.setAction(action);
        request.setProduct_id(pid);
        request.setProduct_variant_id(id);
        ServerCommunicator.addRemoveProducts(connector, request, AppSettings.getSessionKey(context));
        connector.setOnRequestResponseListener(this);

    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof AddRemoveProductResponse) {
            final AddRemoveProductResponse responseBody = (AddRemoveProductResponse) webResponse;
            if (responseBody.getStatus()) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handler.handle();
                    }
                });


            } else {
                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(((Activity) context), "" + responseBody.getMessage());

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

        TextView tvVariant;
        TextView tvPrice;
        TextView tvQty;
        MaterialCardView cvRemove;
        MaterialCardView cvAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvRemove = itemView.findViewById(R.id.cvRemove);
            cvAdd = itemView.findViewById(R.id.cvAdd);
            tvVariant = itemView.findViewById(R.id.tvVariant);
            tvQty = itemView.findViewById(R.id.tvQty);

            tvPrice = itemView.findViewById(R.id.tvPrice);


        }
    }
}
