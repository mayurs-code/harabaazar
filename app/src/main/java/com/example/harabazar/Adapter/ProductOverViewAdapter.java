package com.example.harabazar.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.harabazar.R;
import com.example.harabazar.Service.response.CartResponseData;
import com.example.harabazar.Service.response.OrderDetailResponseData;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.Constants;

import java.util.List;

public class ProductOverViewAdapter extends RecyclerView.Adapter<ProductOverViewAdapter.ViewHolder> {
    private final OrderDetailResponseData data;
    Context context;

    public ProductOverViewAdapter(Context context, OrderDetailResponseData data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_overview_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(data.getOrder_products().get(position).getProduct_name());
        AnimationClass.setAnimationParent(holder.itemView);
        AnimationClass.setAnimationChildZoom(holder.ivImage);

        holder.tvVariant.setText(data.getOrder_products().get(position).getSize()+data.getOrder_products().get(position).getUnit()+" QTY: "+data.getOrder_products().get(position).getQuantity());
        holder.tvPrice.setText(data.getOrder_products().get(position).getPrice());
        try {
            Glide.with(context).load(Constants.FILES_URL+ data.getOrder_products().get(position).getImages().get(0).getImage()).into(holder.ivImage);
        }catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return data.getOrder_products().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvPrice;
        TextView tvVariant;
        TextView tvName;
        ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvVariant = itemView.findViewById(R.id.tvVariant);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}


