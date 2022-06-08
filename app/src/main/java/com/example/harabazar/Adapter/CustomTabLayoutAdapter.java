package com.example.harabazar.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.harabazar.Fragment.ProductSearchFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.response.CartResponseData;
import com.example.harabazar.Service.response.CategoryResponseData;
import com.example.harabazar.Utilities.AnimationClass;

import java.util.List;

public class CustomTabLayoutAdapter extends FragmentStateAdapter {
    private final List<CategoryResponseData> data;
    private final EventHandler handler;
    private final CartResponseData cartResponseData;
    Context context;

    public CustomTabLayoutAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, List<CategoryResponseData> data, Context context,CartResponseData cartResponseData,EventHandler handler) {
        super(fragmentManager, lifecycle);
        this.data = data;
        this.handler = handler;
        this.context = context;
        this.cartResponseData = cartResponseData;
    }


    public View getTabView(int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_custom_tab, null);
        AnimationClass.setAnimationParent(view);

        TextView tvTabText = view.findViewById(R.id.tvTabText);
        tvTabText.setText(data.get(position).getName());

        return view;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new ProductSearchFragment(data.get(position),cartResponseData,handler);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
