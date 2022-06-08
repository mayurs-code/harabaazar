package com.example.harabazar.Adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.BottomSheet.ClaimNowBottomSheet;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.response.OffersResponseData;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.ViewHolder> {

    private final List<OffersResponseData> data;
    Context context;
    EventHandler handler;

    public OffersAdapter(Context context, List<OffersResponseData> data, EventHandler handler) {
        this.context = context;
        this.data = data;
        this.handler = handler;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_offer, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.OfferButton.setText(data.get(position).getCode());
        viewHolder.offerDateEnd.setText(data.get(position).getValid_to());
        AnimationClass.setAnimationParent(viewHolder.itemView);

        String offerText = "Get " + data.get(position).getDiscount_rate() + data.get(position).getDiscount_unit() + " Off on min cart amount of Rs." + data.get(position).getMin_cart_amount();
        viewHolder.offerDescription.setText(offerText);
        viewHolder.OfferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler != null) {
                    handler.handle(data.get(position).getCode());


                } else {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("null", data.get(position).getCode());
                    clipboard.setPrimaryClip(clip);
                    Utils.ShowToast(context, "Code Copied");
                }

            }
        });
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClaimNowBottomSheet bottomSheet = new ClaimNowBottomSheet(data.get(position), offerText,handler);
                bottomSheet.show(((FragmentActivity) context).getSupportFragmentManager(), "ClaimNowBottomSheet");

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView offerDescription;
        TextView offerDateEnd;
        MaterialButton OfferButton;

        public ViewHolder(View itemView) {
            super(itemView);
            offerDescription = itemView.findViewById(R.id.offerDescription);
            offerDateEnd = itemView.findViewById(R.id.offerDateEnd);
            OfferButton = itemView.findViewById(R.id.OfferButton);
        }
    }
}
