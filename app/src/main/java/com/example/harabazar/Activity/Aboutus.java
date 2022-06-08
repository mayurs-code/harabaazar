package com.example.harabazar.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.harabazar.R;

public class Aboutus extends AppCompatActivity {

    ImageView ivback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initViews();
        clickListener();
    }

    private void initViews()
    {
        ivback=findViewById(R.id.ivBack);
    }


    private void clickListener()
    {

        ivback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
