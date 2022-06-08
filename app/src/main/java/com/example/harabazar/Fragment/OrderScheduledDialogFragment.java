package com.example.harabazar.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.harabazar.Activity.MyOrdersActivity;
import com.example.harabazar.R;
import com.google.android.material.button.MaterialButton;

public class OrderScheduledDialogFragment extends DialogFragment {


    MaterialButton mbMyOrders;

    public OrderScheduledDialogFragment(String text) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return inflater.inflate(R.layout.fragment_scheduled, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mbMyOrders = view.findViewById(R.id.mbMyOrders);
        methods();
        super.onViewCreated(view, savedInstanceState);
    }

    private void methods() {
        onHandpickedClick();

    }

    private void onHandpickedClick() {
        mbMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), MyOrdersActivity.class);
                startActivity(i);
                dismiss();

            }
        });
    }
}