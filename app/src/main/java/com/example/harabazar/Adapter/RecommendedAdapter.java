package com.example.harabazar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.harabazar.Activity.MainActivity;
import com.example.harabazar.Fragment.RecommendedFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.AddRemoveProductRequest;
import com.example.harabazar.Service.response.AddRemoveProductResponse;
import com.example.harabazar.Service.response.ProductListResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class RecommendedAdapter extends RecyclerView.Adapter<RecommendedAdapter.ViewHolder> implements OnRequestResponseListener {
    private final EventHandler handler;
    Context context;
    List<ProductListResponseData> productArrayList;
    String session;

    public RecommendedAdapter(Context context, List<ProductListResponseData> productArrayList, EventHandler handler) {
        this.context = context;
        this.productArrayList = productArrayList;
        session = AppSettings.getSessionKey(context);
        this.handler = handler;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_recommended_products, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Glide.with(context).load(Constants.FILES_URL + productArrayList.get(position).getImages().get(0).getImage()).into(holder.ivProductImage);
            Glide.with(context).asBitmap().load(Constants.FILES_URL + productArrayList.get(position).getImages().get(0).getImage()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    int color = Utils.getDominantColor(resource);
                    if (ColorUtils.calculateLuminance(color) < 0.6)
                        color = ColorUtils.blendARGB(color, Color.WHITE, .7f);
                    holder.cvProduct.setCardBackgroundColor(color);
                }
            });


        } catch (Exception e) {
            Glide.with(context).load(Constants.FILES_URL + productArrayList.get(position).getImage()).into(holder.ivProductImage);

        }

        AnimationClass.setAnimationParent(holder.itemView);
        AnimationClass.setAnimationChildZoom(holder.cvProductImage);
        holder.tvProductName.setText(productArrayList.get(position).getName());
        holder.tvProductQty.setText(productArrayList.get(position).getVariants().get(0).getSize() + productArrayList.get(position).getVariants().get(0).getUnit());
        holder.tvOfferPrice.setText("₹" + productArrayList.get(position).getVariants().get(0).getPrice());
        holder.tvPrice.setText("₹" + productArrayList.get(position).getVariants().get(0).getDisplay_price());
        holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initConnector(productArrayList.get(position));
//                Toast.makeText(context, ""+productArrayList.get(position).getVariants().get(0).getId(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.cvProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ProductDetailActivity.class);
//                intent.putExtra("product",  productArrayList.get(position));
//                context.startActivity(intent);
                MainActivity.bottomNavigationView.setSelectedItemId(R.id.bottom_products);
                ((FragmentActivity) context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecommendedFragment()).commit();

            }
        });

    }

    private void initConnector(ProductListResponseData productListResponseData) {
        Connector connector = new Connector();
        AddRemoveProductRequest request = new AddRemoveProductRequest();
        request.setAction("1");
        request.setProduct_id(productListResponseData.getId());
        request.setProduct_variant_id(productListResponseData.getVariants().get(0).getId());
        ServerCommunicator.addRemoveProducts(connector, request, session);
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
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
        ImageView ivProductImage;
        TextView tvProductName;
        TextView tvProductQty;
        TextView tvOfferPrice;
        TextView tvPrice;
        MaterialCardView ivAdd;
        CardView cvProduct;
        CardView cvProductImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductQty = itemView.findViewById(R.id.tvProductQty);
            tvOfferPrice = itemView.findViewById(R.id.tvOfferPrice);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivAdd = itemView.findViewById(R.id.ivAdd);
            cvProductImage = itemView.findViewById(R.id.cvProductImage);
            cvProduct = itemView.findViewById(R.id.cvProduct);
        }
    }
}
