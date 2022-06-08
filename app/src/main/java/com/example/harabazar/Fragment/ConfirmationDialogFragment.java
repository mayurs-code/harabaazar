package com.example.harabazar.Fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.response.GetOrdersResponseData;
import com.google.android.material.button.MaterialButton;

public class ConfirmationDialogFragment extends DialogFragment  {
    private final GetOrdersResponseData getOrdersResponseData;
    private final boolean fromMap;
    private EventHandler handler;
    private MaterialButton mbCancel;
    private MaterialButton mbConfirm;

    public ConfirmationDialogFragment(GetOrdersResponseData getOrdersResponseData, boolean fromMap, EventHandler handler) {
        this.getOrdersResponseData = getOrdersResponseData;
        this.fromMap = fromMap;
        this.handler = handler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_confirm, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mbCancel = view.findViewById(R.id.mbCancel);
        mbConfirm = view.findViewById(R.id.mbConfirm);
        methods();

    }

    private void methods() {
        listners();

    }

    private void listners() {
        mbConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.handle( );
                dismiss();
            }
        });
        mbCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }
}
