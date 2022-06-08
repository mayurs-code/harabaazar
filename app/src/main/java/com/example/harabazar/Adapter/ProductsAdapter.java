package com.example.harabazar.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.harabazar.Activity.ProductDetailActivity;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.AddRemoveProductRequest;
import com.example.harabazar.Service.response.AddRemoveProductResponse;
import com.example.harabazar.Service.response.CartResponseData;
import com.example.harabazar.Service.response.ProductListResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.card.MaterialCardView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> implements OnRequestResponseListener {
    private final EventHandler handler;
    private final CartResponseData cartResponseData;
    Context context;
    List<ProductListResponseData> productArrayList;
    String session;

    HashMap<String, Integer> count = new HashMap<String, Integer>();

    public ProductsAdapter(Context context, List<ProductListResponseData> productArrayList, CartResponseData cartResponseData, EventHandler handler) {
        this.context = context;
        this.productArrayList = productArrayList;
        this.handler = handler;
        this.cartResponseData = cartResponseData;
        session = AppSettings.getSessionKey(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product, parent, false));
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
                    holder.cvProductImageBack.setCardBackgroundColor(color);
                }
            });
        } catch (Exception e) {
            Glide.with(context).load(Constants.FILES_URL + productArrayList.get(position).getImage()).into(holder.ivProductImage);

        }
        AnimationClass.setAnimationParent(holder.itemView);
        AnimationClass.setAnimationChildZoom(holder.ivProductImage);
        for (int i = 0; i < cartResponseData.getProducts().size(); i++) {
            for (int j = 0; j < cartResponseData.getProducts().get(i).getVariants().size(); j++) {
                String key = cartResponseData.getProducts().get(i).getId() + "," + cartResponseData.getProducts().get(i).getVariants().get(j).getId();
//                Log.d("COUNTXX", "onBindViewHolder: " + ""+i+","+j +","+cartResponseData.getProducts().get(i).getVariants().size());

                if (count.containsKey(key)) {
                    cartResponseData.getProducts().get(i).getVariants().get(j).setCart_quantity(count.get(key) + "");
                }
                try {
                    count.put(key, Integer.parseInt(cartResponseData.getProducts().get(i).getVariants().get(j).getCart_quantity()));
                } catch (Exception e) {

                }
                Log.d("COUNTXX", "onBindViewHolder: " + Arrays.asList(count) + "===========" + key + "-" + count.get(key));
                if (productArrayList.get(position).getId().equals(cartResponseData.getProducts().get(i).getId()) &&
                        productArrayList.get(position).getVariants().get(0).getId().equals(cartResponseData.getProducts().get(i).getVariants().get(j).getId()))
                    holder.tvCartSize.setText("" + count.get(key));
            }

        }
        String key = productArrayList.get(position).getId() + "," + productArrayList.get(position).getVariants().get(0).getId();
        if ((count.get(key) == null)) holder.tvCartSize.setText("0");
        holder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initConnector(productArrayList.get(position), 0, "1");
                MediaPlayer mp = MediaPlayer.create(context, R.raw.alert1);
                mp.start();
                AnimationClass.setAnimationRotateSquare(holder.ivAdd);

                holder.tvCartSize.setVisibility(View.VISIBLE);
                if (!count.containsKey(key)) {
                    count.put(key, 0);
                }

                count.put(key, (count.get(key) + 1));
                Log.d("COUNTXX", "onBindViewHolder: " + Arrays.asList(count));

                holder.tvCartSize.setText((count.get(key)) + "");


//                Toast.makeText(context, ""+productArrayList.get(position).getVariants().get(0).getId(), Toast.LENGTH_SHORT).show();
            }
        });
        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initConnector(productArrayList.get(position), 0, "0");
                MediaPlayer mp = MediaPlayer.create(context, R.raw.alert1);
                mp.start();
                AnimationClass.setAnimationRotate360(holder.ivRemove);

                holder.tvCartSize.setVisibility(View.VISIBLE);
                if (!count.containsKey(key)) {
                    count.put(key, 0);
                }

                count.put(key, (count.get(key) - 1));
                if (count.get(key) < 0) count.put(key, 0);

                Log.d("COUNTXX", "onBindViewHolder: " + Arrays.asList(count));

                holder.tvCartSize.setText((count.get(key)) + "");


//                Toast.makeText(context, ""+productArrayList.get(position).getVariants().get(0).getId(), Toast.LENGTH_SHORT).show();
            }
        });


        holder.tvProductQty.setText(productArrayList.get(position).getVariants().get(0).getSize() + productArrayList.get(position).getVariants().get(0).getUnit());
        holder.tvOfferPrice.setText("₹" + productArrayList.get(position).getVariants().get(0).getPrice());
        holder.tvPrice.setText("₹" + productArrayList.get(position).getVariants().get(0).getDisplay_price());
        holder.tvProductName.setText(productArrayList.get(position).getName() + " " + holder.tvProductQty.getText());

        holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.cvProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, ProductDetailActivity.class);
//                intent.putExtra("product", productArrayList.get(position));
//                context.startActivity(intent);
            }
        });
        VariantSelectAdapter variantSelectAdapter = new VariantSelectAdapter(context, productArrayList.get(position).getVariants(), new EventHandler() {
            @Override
            public void handle() {

            }

            @Override
            public void handle(int i) {
                holder.tvProductQty.setText(productArrayList.get(position).getVariants().get(i).getSize() + productArrayList.get(position).getVariants().get(i).getUnit());
                holder.tvOfferPrice.setText("₹" + productArrayList.get(position).getVariants().get(i).getPrice());
                holder.tvProductName.setText(productArrayList.get(position).getName() + " " + holder.tvProductQty.getText());
                String key = productArrayList.get(position).getId() + "," + productArrayList.get(position).getVariants().get(i).getId();
                holder.tvCartSize.setText((count.get(key)) + "");
                Log.d("COUNTXX", "onBindViewHolder: " + Arrays.asList(count) + "=====" + key);

                if ((count.get(key) == null)) holder.tvCartSize.setText("0");
                holder.ivAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initConnector(productArrayList.get(position), i, "1");
                        MediaPlayer mp = MediaPlayer.create(context, R.raw.alert1);
                        mp.start();
                        AnimationClass.setAnimationRotateSquare(holder.ivAdd);

                        holder.tvCartSize.setVisibility(View.VISIBLE);
                        if (!count.containsKey(key)) {
                            count.put(key, 0);
                        }
                        count.put(key, (count.get(key) + 1));
                        Log.d("COUNTXX", "onBindViewHolder: " + Arrays.asList(count));

                        holder.tvCartSize.setText((count.get(key)) + "");


//                Toast.makeText(context, ""+productArrayList.get(position).getVariants().get(0).getId(), Toast.LENGTH_SHORT).show();
                    }
                });
                holder.ivRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initConnector(productArrayList.get(position), i, "0");
                        MediaPlayer mp = MediaPlayer.create(context, R.raw.alert1);
                        mp.start();
                        AnimationClass.setAnimationRotate360(holder.ivRemove);

                        holder.tvCartSize.setVisibility(View.VISIBLE);
                        if (!count.containsKey(key)) {
                            count.put(key, 0);
                        }
                        count.put(key, (count.get(key) - 1));
                        if (count.get(key) < 0) count.put(key, 0);

                        Log.d("COUNTXX", "onBindViewHolder: " + Arrays.asList(count));

                        holder.tvCartSize.setText((count.get(key)) + "");


//                Toast.makeText(context, ""+productArrayList.get(position).getVariants().get(0).getId(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void handle(String text) {

            }
        });
        holder.rvVariants.setAdapter(variantSelectAdapter);
        holder.rvVariants.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));


    }

    private void initConnector(ProductListResponseData productListResponseData, int i, String action) {
        Connector connector = new Connector();
        AddRemoveProductRequest request = new AddRemoveProductRequest();
        request.setAction(action);
        request.setProduct_id(productListResponseData.getId());
        request.setProduct_variant_id(productListResponseData.getVariants().get(i).getId());
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
                        try {
                            handler.handle();
                        } catch (Exception e) {

                        }

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
        TextView tvCartSize;
        MaterialCardView ivAdd;
        MaterialCardView ivRemove;
        CardView cvProduct;
        CardView cvProductImageBack;
        RecyclerView rvVariants;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rvVariants = itemView.findViewById(R.id.rvVariants);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductQty = itemView.findViewById(R.id.tvProductQty);
            ivRemove = itemView.findViewById(R.id.ivRemove);
            tvOfferPrice = itemView.findViewById(R.id.tvOfferPrice);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvCartSize = itemView.findViewById(R.id.tvCartSize);
            ivAdd = itemView.findViewById(R.id.ivAdd);
            cvProduct = itemView.findViewById(R.id.cvProduct);
            cvProductImageBack = itemView.findViewById(R.id.cvProductImageBack);
        }
    }
}
