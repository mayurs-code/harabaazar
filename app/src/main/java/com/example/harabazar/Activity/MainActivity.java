package com.example.harabazar.Activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.harabazar.Fragment.HandpickFragment;
import com.example.harabazar.Fragment.HomeFragment;
import com.example.harabazar.Fragment.IncomingNotificationFragment;
import com.example.harabazar.Fragment.LoadingFragment;
import com.example.harabazar.Fragment.OffersFragment;
import com.example.harabazar.Fragment.ProductsFragment;
import com.example.harabazar.Fragment.SettingFragment;
import com.example.harabazar.R;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {
    public static BottomNavigationView bottomNavigationView;
    RelativeLayout fabLocation;
    FrameLayout fragment_container;
    OffersFragment offersFragment;
    HandpickFragment handpickFragment;
    HomeFragment homeFragment;
    ProductsFragment productsFragment;
    SettingFragment settingFragment;
    Fragment selectedFragment = null;
    IncomingNotificationFragment incomingNotificationFragment;


    private final BroadcastReceiver notificationCountRefresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppLogger.i("notificationCountRefresh", " notificationCountRefresh BroadcastReceiver -----");
            if (intent != null) {
                if (intent.hasExtra(Constants.NOTIFICATION_DATA + "")) {
//                    Toast.makeText(context, intent.getStringExtra(Constants.NOTIFICATION_DATA + ""), Toast.LENGTH_SHORT).show();
                    String notification = intent.getStringExtra(Constants.NOTIFICATION_DATA);
                    incomingNotificationFragment = new IncomingNotificationFragment(notification);
                    if (!incomingNotificationFragment.isAdded()) {
                        incomingNotificationFragment.show(getSupportFragmentManager(), MainActivity.this.getClass().getSimpleName());
                    }
                    if (notification.contains("Accepted")) {
                        Intent i=new Intent(MainActivity.this, MyOrdersActivity.class);
                        startActivity(i);
                    }
                }
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationCountRefresh);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationCountRefresh, new IntentFilter(Constants.BROAD_REFRESH_NOTIFICATION_COUNT));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get Session key
        Log.d("SESSIONCHK", "onCreate: +" + AppSettings.getSessionKey(MainActivity.this));
        initView();
        methods();
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationCountRefresh, new IntentFilter(Constants.BROAD_REFRESH_NOTIFICATION_COUNT));

        Log.d("SESSIONCHK", "onCreate: +" +  AppSettings.getUserToken(MainActivity.this));
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                AppLogger.e("MESSAGE_RECIEVED","Token="+task.getResult());
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectedFragment = null;
                setFragments();
                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        selectedFragment = homeFragment;
                        break;
                    case R.id.bottom_products:
                        selectedFragment = productsFragment;
                        break;
                    case R.id.bottom_setting:
                        selectedFragment = settingFragment;
                        break;
                    case R.id.bottom_offers:

                        selectedFragment = offersFragment;
                        break;
                    case R.id.uncheckedItem:

                        selectedFragment = handpickFragment;
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commitAllowingStateLoss();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.uncheckedItem);

    }

    private void setFragments() {
        offersFragment = new OffersFragment();
        homeFragment = new HomeFragment();
        handpickFragment = new HandpickFragment();
        productsFragment = new ProductsFragment(getSupportFragmentManager());
        settingFragment = new SettingFragment();
    }

    private void methods() {
        checkPermissions();
        listners();
    }

    private void listners() {
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView.setSelectedItemId(R.id.uncheckedItem);
            }
        });
    }

    private void checkPermissions() {
        boolean permissionGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (permissionGranted) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
    }

    private void initView() {
        fabLocation = findViewById(R.id.fabLocation);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fragment_container = findViewById(R.id.fragment_container);

    }

    @Override
    public void onBackPressed() {
        try {
            if (bottomNavigationView.getSelectedItemId() == R.id.bottom_home)
                super.onBackPressed();
            else {
                MainActivity.bottomNavigationView.setSelectedItemId(R.id.bottom_home);
            }
        } catch (Exception e) {
            MainActivity.bottomNavigationView.setSelectedItemId(R.id.bottom_home);

        }

    }
}