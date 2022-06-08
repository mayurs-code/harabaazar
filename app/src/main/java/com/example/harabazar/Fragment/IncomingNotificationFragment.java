package com.example.harabazar.Fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.harabazar.R;
import com.example.harabazar.Utilities.AnimationClass;


public class IncomingNotificationFragment extends DialogFragment {
    private final String text;
    TextView tvText;
    TextView mbDismiss;

    public IncomingNotificationFragment(String text) {
        this.text = text;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.dialog_notification, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AnimationClass.setAnimationChildZoom(view);
        tvText = view.findViewById(R.id.tvText);
        mbDismiss = view.findViewById(R.id.mbDismiss);
        methods();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               try {
                   dismiss();
               }catch (Exception e){
                   e.printStackTrace();
               }
            }
        }, 59000);


    }

    private void methods() {
        listners();
        tvText.setText("" + text);

    }

    private void listners() {
        mbDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
