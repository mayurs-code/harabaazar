package com.example.harabazar.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.harabazar.R;
import com.example.harabazar.Utilities.AppSettings;
import com.google.android.material.button.MaterialButton;

public class HandpickBoarding extends DialogFragment {


    ImageView ivImage;
    MaterialButton mbNext;
    MaterialButton mbSkip;
    int i = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return inflater.inflate(R.layout.fragment_handpick_boarding, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Material);

    }

    private void methods() {
        mbNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i == 2) {
                    AppSettings.setOnboarding(getContext(), true);
                    dismiss();
                } else if (i == 1) {
                    ivImage.setImageResource(R.drawable.selling);
                    i++;

                } else if (i == 0) {
                    i++;
                    ivImage.setImageResource(R.drawable.hawkers_around_you);
                }
            }
        });
        mbSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSettings.setOnboarding(getContext(), true);
                dismiss();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mbNext = view.findViewById(R.id.mbNext);
        ivImage = view.findViewById(R.id.ivImage);
        mbSkip = view.findViewById(R.id.mbSkip);
        methods();
        super.onViewCreated(view, savedInstanceState);

    }


    public void dismisss() {
    }
}
