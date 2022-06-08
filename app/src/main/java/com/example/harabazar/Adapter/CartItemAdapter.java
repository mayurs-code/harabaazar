package com.example.harabazar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.RemoveFromCartRequest;
import com.example.harabazar.Service.response.CartResponseData;
import com.example.harabazar.Service.response.ProductVarients;
import com.example.harabazar.Service.response.RemoveFromCartResponse;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;

import java.util.ArrayList;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> implements OnRequestResponseListener {
    private final CartResponseData data;
    private final EventHandler handler;
    Context context;
    String session;

    public CartItemAdapter(Context context, CartResponseData data, EventHandler handler) {
        this.context = context;
        this.data = data;
        this.handler = handler;
        session = AppSettings.getSessionKey(context);

    }

    @NonNull
    @Override
    public CartItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CartItemAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(data.getProducts().get(position).getName());
        AnimationClass.setAnimationParent(holder.itemView);
        AnimationClass.setAnimationChildZoom(holder.ivImage);

        try {
            Glide.with(context).load(Constants.FILES_URL + data.getProducts().get(position).getImages().get(0).getImage()).into(holder.ivImage);
        } catch (Exception e) {
            Glide.with(context).load(Constants.FILES_URL + data.getProducts().get(position).getImage()).into(holder.ivImage);

        }

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initConnector(data.getProducts().get(position).getCart_product_id() );
                notifyItemRemoved(position);

            }
        });
        {
            List<ProductVarients> temp = new ArrayList<>();
            for (int i = 0; i < data.getProducts().get(position).getVariants().size(); i++) {

                if(data.getProducts().get(position).getVariants().get(i).getCart_quantity()!=null){
                    temp.add(data.getProducts().get(position).getVariants().get(i));
                }


            }data.getProducts().get(position).setVariants(temp);

            holder.rvVariantsCart.setAdapter(new CartVariantAdapter(context, handler, data.getProducts().get(position)));
            holder.rvVariantsCart.setLayoutManager(new LinearLayoutManager(context));

        }

    }


    private void initConnector(String cartProductId ) {
        Connector connector = new Connector();
        RemoveFromCartRequest removeFromCartRequest=new RemoveFromCartRequest();
        removeFromCartRequest.setCart_product_id(cartProductId);
        ServerCommunicator.removeFromCart(connector, removeFromCartRequest, session);
        connector.setOnRequestResponseListener(this);

    }

    @Override
    public int getItemCount() {
        return data.getProducts().size();
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {

        if (webResponse instanceof RemoveFromCartResponse) {
            final RemoveFromCartResponse responseBody = (RemoveFromCartResponse) webResponse;
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

        TextView tvName;
        ImageView ivImage;
        ImageView ivDelete;
        RecyclerView rvVariantsCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rvVariantsCart = itemView.findViewById(R.id.rvVariantsCart);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            tvName = itemView.findViewById(R.id.tvName);
        }
    }
}