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
import com.example.harabazar.Service.response.GetInventoryResponseData;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.Constants;

import java.util.List;

public class SabziwalaProductsAdapter extends RecyclerView.Adapter<SabziwalaProductsAdapter.ViewHolder> {

    private final List<GetInventoryResponseData> data;
    Context context;

    public SabziwalaProductsAdapter(Context context, List<GetInventoryResponseData> data) {
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
        AnimationClass.setAnimationParent(holder.itemView);
        AnimationClass.setAnimationChildZoom(holder.ivImage);

        holder.tvName.setText(data.get(position).getName());
        holder.tvVariant.setText(data.get(position).getCategory_name());
        holder.tvPrice.setText("₹"+data.get(position).getMin_amount() + "- ₹" + data.get(position).getMax_amount());
        try {
            Glide.with(context).load(Constants.FILES_URL + data.get(position).getImage()).into(holder.ivImage);
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return data.size();
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


