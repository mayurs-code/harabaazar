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
import com.example.harabazar.Service.response.GetHandpickOffersResponseData;
import com.example.harabazar.Utilities.AnimationClass;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class OffersAdapterBottom extends RecyclerView.Adapter<OffersAdapterBottom.ViewHolder> {

    private final List<GetHandpickOffersResponseData> data;
    private final EventHandler eventHandler;
    private final EventHandler handler;
    Context context;

    public OffersAdapterBottom(Context context, List<GetHandpickOffersResponseData> data, EventHandler eventHandler, EventHandler handler) {
        this.context = context;
        this.data = data;
        this.eventHandler = eventHandler;
        this.handler = handler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_offer, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.offerDescription.setText(data.get(position).getDescription());
        viewHolder.offerDateEnd.setVisibility(View.GONE);
        viewHolder.OfferButton.setText(data.get(position).getCode());
        viewHolder.mcOfferCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventHandler.handle(data.get(position).getId()+":"+data.get(position).getCode());
                handler.handle();

            }
        });
        AnimationClass.setAnimationParent(viewHolder.itemView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView offerDescription;
        TextView offerDateEnd;
        MaterialButton OfferButton;
        MaterialCardView mcOfferCard;

        public ViewHolder(View itemView) {
            super(itemView);
            offerDescription = itemView.findViewById(R.id.offerDescription);
            offerDateEnd = itemView.findViewById(R.id.offerDateEnd);
            OfferButton = itemView.findViewById(R.id.OfferButton);
            mcOfferCard = itemView.findViewById(R.id.mcOfferCard);
        }
    }
}
