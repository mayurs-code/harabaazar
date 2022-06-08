package com.example.harabazar.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.response.ProductVarients;
import com.example.harabazar.Utilities.AnimationClass;
import com.google.android.material.card.MaterialCardView;

import java.util.List;


public class VariantSelectAdapter extends RecyclerView.Adapter<VariantSelectAdapter.ViewHolder> {
    private final List<ProductVarients> data;
    Context context;
    private EventHandler handler;

    int selected=0;

    public VariantSelectAdapter(Context context, List<ProductVarients> data,EventHandler handler) {
        this.context = context;
        this.handler = handler;
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_varient_chip, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AnimationClass.setAnimationParent(holder.itemView);

        holder.tvVarient.setText(data.get(position).getSize() + data.get(position).getUnit());
        if(selected==position){
            holder.tvVarient.setTextColor(context.getResources().getColor(R.color.white,null));
            holder.mcVarient.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary,null));
        }else {
            holder.tvVarient.setTextColor(context.getResources().getColor(R.color.colorPrimary,null));
            holder.mcVarient.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryTrans,null));
        }
        holder.mcVarient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected=position;
                handler.handle(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvVarient;
        MaterialCardView mcVarient;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mcVarient = itemView.findViewById(R.id.mcVarient);
            tvVarient = itemView.findViewById(R.id.tvVarient);
        }
    }
}


