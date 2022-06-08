package com.example.harabazar.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.harabazar.Activity.MainActivity;
import com.example.harabazar.R;
import com.example.harabazar.Service.response.BannersResponseData;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.card.MaterialCardView;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class BannerSliderAdapter extends SliderViewAdapter<BannerSliderAdapter.ViewHolder> {

    private final List<BannersResponseData> data;
    Context context;

    public BannerSliderAdapter(Context context, List<BannersResponseData> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_banner_slider, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.ivBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.bottomNavigationView.setSelectedItemId(R.id.bottom_offers);

            }
        });
        AnimationClass.setAnimationParent(viewHolder.itemView);

        Glide.with(context).load(Constants.FILES_URL+data.get(position).getBanner()).into(viewHolder.ivBanner);
        Glide.with(context).asBitmap().load(Constants.FILES_URL+data.get(position).getBanner()).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                int color = Utils.getDominantColor(resource);
                if (ColorUtils.calculateLuminance(color) < 0.6)
                    color = ColorUtils.blendARGB(color, Color.WHITE, .6f);
                viewHolder.cvBanner.setStrokeColor(color);
            }
        });

    }

    @Override
    public int getCount() {
        return data.size();
    }

    public class ViewHolder extends SliderViewAdapter.ViewHolder {
        ImageView ivBanner;
        MaterialCardView cvBanner;

        public ViewHolder(View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.ivBanner);
            cvBanner = itemView.findViewById(R.id.cvBanner);
        }

    }
}
