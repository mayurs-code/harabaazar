package com.example.harabazar.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.harabazar.R;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.CheckLocation;
import com.google.android.material.button.MaterialButton;

public class SignInSignUpActivity extends AppCompatActivity {

    MaterialButton btnLogin;
    MaterialButton btnSignUp;
    String flag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        String session = AppSettings.getSessionKey(this);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        boolean b= CheckLocation.isLocationEnabled(SignInSignUpActivity.this);
        if(b==true)
        {
            //   Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Toast.makeText(getApplicationContext(), "disable", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(SignInSignUpActivity.this);
            builder.setTitle("Please Enable Your Location")
                    .setMessage("To Continue Our Services...")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            }) ;
            AlertDialog alert = builder.create();
            alert.show();
        }

/*
        if(!session.isEmpty()){
            Intent intent = new Intent(SignInSignUpActivity.this, MainActivity.class);
            startActivity(intent);
        }
*/

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "login";
                Intent intent = new Intent(SignInSignUpActivity.this, NumberVerificationActivity.class);
                intent.putExtra("flag", flag);
                startActivity(intent);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = "signup";
                Intent intent = new Intent(SignInSignUpActivity.this, NumberVerificationActivity.class);
                intent.putExtra("flag", flag);
                startActivity(intent);
            }
        });
    }
}
