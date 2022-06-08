package com.example.harabazar.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.harabazar.R;
import com.example.harabazar.Service.response.GetOrdersResponseData;
import com.example.harabazar.Service.response.GetUsersResponseData;
import com.google.android.material.button.MaterialButton;

public class HandpickedFragment extends DialogFragment {
    private final GetOrdersResponseData getOrdersResponseData;
    private final GetUsersResponseData getUsersResponseData;

    MaterialButton mb_done;

    public HandpickedFragment(GetOrdersResponseData getOrdersResponseData, GetUsersResponseData getUsersResponseData) {
        this.getOrdersResponseData = getOrdersResponseData;
        this.getUsersResponseData = getUsersResponseData;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Material);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_handpicked, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mb_done=view.findViewById(R.id.mbCancel);
        methods();
        super.onViewCreated(view, savedInstanceState);
    }

    private void methods() {
        onHandpickedClick();

    }
    private void onHandpickedClick() {
        mb_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });
    }
}