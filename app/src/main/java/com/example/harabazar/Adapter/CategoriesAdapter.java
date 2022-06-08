package com.example.harabazar.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.harabazar.Activity.MainActivity;
import com.example.harabazar.Fragment.ProductsFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.response.CategoryResponseData;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.Constants;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {

    Context context;
    List<CategoryResponseData> categoriesArrayList;

    public CategoriesAdapter(Context context, List<CategoryResponseData> categoriesArrayList ) {
        this.context = context;
        this.categoriesArrayList = categoriesArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_categories,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(categoriesArrayList.get(position).getName());
        AnimationClass.setAnimationParent(holder.itemView);
        AnimationClass.setAnimationChildZoom(holder.ivImage);
        Glide.with(context).load(Constants.FILES_URL+categoriesArrayList.get(position).getImage()).placeholder(R.drawable.logo).into(holder.ivImage);
        holder.cvCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.bottomNavigationView.setSelectedItemId(R.id.bottom_products);
                ProductsFragment.tab_id=categoriesArrayList.get(position).getId();
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoriesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView ivImage;
        TextView tvName;
        CardView  cvCategories;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvName = itemView.findViewById(R.id.tvName);
            cvCategories = itemView.findViewById(R.id.cvCategories);
        }
    }
}
