package com.example.harabazar.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.harabazar.R;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Utils;

public class SplashActivity extends AppCompatActivity {
    private long secondsRemaining;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        checkPermissions();

    }
    private void checkPermissions() {
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (permissionGranted) {
            createTimer(100);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==200){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                createTimer(100);
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            }
        }
    }

    private void createTimer(long seconds) {
      //  final TextView counterTextView = findViewById(R.id.timer);

        CountDownTimer countDownTimer =
                new CountDownTimer(seconds, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        secondsRemaining = ((millisUntilFinished / 1000) + 1);
                      //  counterTextView.setText("App is done loading in: " + secondsRemaining);
                    }

                    @Override
                    public void onFinish() {
                        secondsRemaining = 0;
                      //  counterTextView.setText("Done.");

                            if (Utils.isBlankOrNull(AppSettings.getUId(SplashActivity.this).trim())) {
                                Intent mainIntent = new Intent(SplashActivity.this, SignInSignUpActivity.class);
                                startActivity(mainIntent);
                                finish();
                            } else {
                                if (!AppSettings.isLogin(SplashActivity.this))
                                {
                                    Intent mainIntent = new Intent(SplashActivity.this, SignInSignUpActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                } else {
                                        startMainActivity();
                                        finish();
                                }
                            }
                        }

                };
        countDownTimer.start();
    }
    private void startMainActivity() {
         Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
         startActivity(mainIntent);
        finish();
    }

}
