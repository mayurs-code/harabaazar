package com.example.harabazar.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.harabazar.R;
import com.example.harabazar.Service.response.ImageResponse;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.Constants;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class ProductImagesSliderAdapter extends SliderViewAdapter<ProductImagesSliderAdapter.ViewHolder> {

    private final List<ImageResponse> images;
    Context context;

    public ProductImagesSliderAdapter(Context context, List<ImageResponse> images) {
        this.context=context;
        this.images=images;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product_image,null));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        AnimationClass.setAnimationParent(viewHolder.itemView);

        try {
            Glide.with(context).load(Constants.FILES_URL+images.get(position).getImage()).into(viewHolder.ivImage);
        }catch (Exception e){
        }

    }

    @Override
    public int getCount() {
        return images.size();
    }

    public class ViewHolder extends SliderViewAdapter.ViewHolder {
        ImageView ivImage;
        public ViewHolder(View itemView) {
            super(itemView);
            ivImage=itemView.findViewById(R.id.ivImage);
        }
    }
}
