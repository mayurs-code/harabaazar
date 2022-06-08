package com.example.harabazar.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;

import com.example.harabazar.Service.response.NotificationResponseData;
import com.example.harabazar.Utilities.CheckLocation;

import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.harabazar.Activity.AddressActivity;
import com.example.harabazar.Activity.CartActivity;
import com.example.harabazar.Adapter.CustomTabLayoutAdapter;
import com.example.harabazar.Adapter.ProductsAdapter;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.GetProfileRequest;
import com.example.harabazar.Service.request.ProductListRequest;
import com.example.harabazar.Service.response.CartResponse;
import com.example.harabazar.Service.response.CartResponseData;
import com.example.harabazar.Service.response.CategoryResponse;
import com.example.harabazar.Service.response.CategoryResponseData;
import com.example.harabazar.Service.response.GetProfileResponse;
import com.example.harabazar.Service.response.GetProfileResponseData;
import com.example.harabazar.Service.response.NotificationResponse;
import com.example.harabazar.Service.response.ProductListResponse;
import com.example.harabazar.Service.response.ProductListResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductsFragment extends Fragment implements OnRequestResponseListener, EventHandler {

    public static String tab_id;
    private final FragmentManager supportFragmentManager;
    ViewPager2 view_pager;
    ImageView ivCart;
    ImageView ivNotifications;
    TabLayout categoriesTabLayout;
    EditText tvSearch;
    RecyclerView rvProducts;
    TextView tvCartSize;

    TextView tvNotificationSize;
    TextView tvAddress;
    RelativeLayout rlCartBadge;
    RelativeLayout rlNotificationBadge;
    CustomTabLayoutAdapter customTabLayoutAdapter;
    Geocoder geocoder;
    List<Address> addresses;
    LoadingFragment loadingFragment = new LoadingFragment();
    NoInternetFragment dialogFragment = new NoInternetFragment();
    int getSize;
    CartResponseData cartResponseData;
    private String session;
    private ProductsAdapter productsAdapter;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private Context context;
    private boolean onHandle = false;

    public ProductsFragment(FragmentManager supportFragmentManager) {
        this.supportFragmentManager = supportFragmentManager;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        context = getActivity();
        view_pager = view.findViewById(R.id.view_pager);
        categoriesTabLayout = view.findViewById(R.id.categoriesTabLayout);
        ivCart = view.findViewById(R.id.ivCart);
        rlCartBadge = view.findViewById(R.id.rlCartBadge);
        tvNotificationSize = view.findViewById(R.id.tvNotificationSize);
        rlNotificationBadge = view.findViewById(R.id.rlNotificationBadge);
        tvCartSize = view.findViewById(R.id.tvCartSize);
        ivNotifications = view.findViewById(R.id.ivNotifications);
        tvAddress = view.findViewById(R.id.tvAddress);
        rvProducts = view.findViewById(R.id.rvProducts);
        tvSearch = view.findViewById(R.id.tvSearch);
        session = AppSettings.getSessionKey(getContext());
        System.out.println("ShowingDialogx onCreateView");
        loadingFragment.show(getChildFragmentManager(), "");

        boolean b= CheckLocation.isLocationEnabled(getContext());
        if(b==true)
        {
            //   Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Toast.makeText(getApplicationContext(), "disable", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        methods();

    }

    private void methods() {
        viewListners();
        onHandle = false;
        connectorCartInit();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("ShowingDialogx onPause");
        onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("ShowingDialogx onDestroy");

    }

    private void initiateSearch(List<ProductListResponseData> data) {

        tvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tvSearch.getText().toString().trim().isEmpty()) {
                    rvProducts.setVisibility(View.GONE);
                } else {
                    rvProducts.setVisibility(View.VISIBLE);
                }
                List<ProductListResponseData> temp = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getName().trim().toLowerCase().contains(tvSearch.getText().toString().toLowerCase().trim()))
                        temp.add(data.get(i));
                }
                productsAdapter = new ProductsAdapter(getContext(), temp, cartResponseData, ProductsFragment.this);
                rvProducts.setAdapter(productsAdapter);
                rvProducts.setLayoutManager(new GridLayoutManager(getActivity(), 1));
                System.out.println(tvSearch.getText().toString()+temp.size());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void viewListners() {
        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CartActivity.class);
                getActivity().startActivity(i);
            }
        });
        tvAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), AddressActivity.class);
                getActivity().startActivity(i);
            }
        });
        ivNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNotification();
            }
        });
    }

    private void openNotification() {
        NotificationDialogFragment dialogFragment = new NotificationDialogFragment(rlNotificationBadge);

        dialogFragment.show(getActivity().getSupportFragmentManager(), "NotificationDialogFragment");
    }

    private void connectorInit() {
        Connector connector = new Connector();
        ProductListRequest productListRequest=new ProductListRequest();
        productListRequest.setLimit("10000");

        ServerCommunicator.getProducts(connector,productListRequest , session);
        ServerCommunicator.getNotifications(connector, AppSettings.getSessionKey(getContext()));
        ServerCommunicator.getProfile(connector, new GetProfileRequest(AppSettings.getUId(getActivity())), session);
        connector.setOnRequestResponseListener(this);

    }

    private void connectorCategoryInit() {
        Connector connector = new Connector();
        ServerCommunicator.getCategory(connector, session);
        connector.setOnRequestResponseListener(this);

    }

    private void connectorCartInit() {
        Connector connector = new Connector();
        ServerCommunicator.getCartProductList(connector, session);

        connector.setOnRequestResponseListener(this);

    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof CategoryResponse) {
            final CategoryResponse responseBody = (CategoryResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingFragment.dismisss();
                        setCategoriesAdapter(responseBody.getData());
                    }
                });


            } else {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(getActivity(), "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof ProductListResponse) {
            final ProductListResponse responseBody = (ProductListResponse) webResponse;

            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!onHandle)
                            connectorCategoryInit();
                        setProductListAdapter(responseBody.getData());
                    }
                });


            } else {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(getActivity(), "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof CartResponse) {
            final CartResponse responseBody = (CartResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectorInit();
                        cartResponseData = responseBody.getData();

                        if (responseBody.getData().getProducts().size() > 0) {
                            rlCartBadge.setVisibility(View.VISIBLE);
                            AnimationClass.setAnimationAlert(rlCartBadge);
//                            AnimationClass.setAnimationRotate360(ivCart);

                        }
                        tvCartSize.setText("" + Math.min(9, responseBody.getData().getProducts().size()));
                    }
                });


            } else {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(getActivity(), "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof GetProfileResponse) {
            final GetProfileResponse responseBody = (GetProfileResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setAddress(responseBody.getData());
                    }
                });


            } else {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(getActivity(), "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof NotificationResponse) {
            final NotificationResponse responseBody = (NotificationResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseBody.getData().size() > 0) {
                            int count = 0;
                            for (NotificationResponseData i : responseBody.getData()) {
                                if (i.getRead_status().equals("0")) {
                                    count += 1;
                                }
                            }
                            if (count > 0) {
                                rlNotificationBadge.setVisibility(View.VISIBLE);
                                AnimationClass.setAnimationAlert(rlNotificationBadge);
                                AnimationClass.setAnimationRotate360(ivNotifications);
                            }
                            tvNotificationSize.setText("" + Math.min(9, count));
                        }


                    }
                });


            } else {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(getActivity(), "" + responseBody.getMessage());
                    }
                });
            }

        }


    }

    private void setAddress(GetProfileResponseData data) {
        try {
            if (data.getAddress().size() > 0)
                for (int i = 0; i < data.getAddress().size(); i++) {
                    if (data.getAddress().get(i).getIs_default() == 1)
                        tvAddress.setText(data.getAddress().get(i).getAddress());

                }
            else getDeviceLocation();


        } catch (Exception e) {
            tvAddress.setText("No Address Found...");
            getDeviceLocation();
        }

    }

    private void setProductListAdapter(List<ProductListResponseData> data) {

        initiateSearch(data);


    }

    private void getAddress(LatLng latLng) {
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            Log.d("getAddress", "onPlaceSelected: " + address + " " + city + " " + state + " " + country + " " + postalCode + " " + knownName);
            tvAddress.setText(address);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                getAddress(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));

                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        getAddress(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);

                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }


                        } else {
                            Toast.makeText(getActivity(), "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void setCategoriesAdapter(List<CategoryResponseData> data) {
        customTabLayoutAdapter = new CustomTabLayoutAdapter(supportFragmentManager, getLifecycle(), data, context, cartResponseData, ProductsFragment.this);
        view_pager.setAdapter(customTabLayoutAdapter);
        new TabLayoutMediator(categoriesTabLayout, view_pager, (tab, position) -> tab.setText("OBJECT " + (position + 1))).attach();
        for (int i = 0; i < categoriesTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = categoriesTabLayout.getTabAt(i);
            if (data.get(i).getId().equals(tab_id)) {
                categoriesTabLayout.selectTab(tab);
            }
            tab.setCustomView(customTabLayoutAdapter.getTabView(i));
        }
        categoriesTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                if (view != null) {
                    TextView tvTabText = view.findViewById(R.id.tvTabText);
                    View viewTabUnderline = view.findViewById(R.id.viewTabUnderline);
                    tvTabText.setTextColor(getResources().getColor(R.color.black, null));
                    viewTabUnderline.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                View view = tab.getCustomView();
                if (view != null) {
                    TextView tvTabText = view.findViewById(R.id.tvTabText);
                    View viewTabUnderline = view.findViewById(R.id.viewTabUnderline);
                    tvTabText.setTextColor(getResources().getColor(R.color.grey, null));
                    viewTabUnderline.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        TabLayout.Tab tab = categoriesTabLayout.getTabAt(categoriesTabLayout.getSelectedTabPosition());
        View view = tab.getCustomView();
        if (view != null) {
            TextView tvTabText = view.findViewById(R.id.tvTabText);
            View viewTabUnderline = view.findViewById(R.id.viewTabUnderline);
            tvTabText.setTextColor(getResources().getColor(R.color.black, null));
            viewTabUnderline.setVisibility(View.VISIBLE);
        }


        categoriesTabLayout.setSelectedTabIndicator(null);


    }

    @Override
    public void onUploadComplete(WebResponse webResponse) {

    }

    @Override
    public void onVFRClientException(WebErrorResponse edErrorData) {

        AppLogger.e(Utils.getTag(), edErrorData.getMessage());
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Utils.ShowToast(getActivity(), edErrorData.getMessage());
            }
        });
    }

    @Override
    public void onAuthException() {

    }

    @Override
    public void onNoConnectivityException(String message) {

        if (message.equals("-1")) {
            dialogFragment.show(getChildFragmentManager(), "" + Constants.incrementalID++);
        }
        if (message.equals("1")) {
            try {
                dialogFragment.dismiss();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onNoCachedDataAvailable() {

    }

    @Override
    public void handle() {
        connectorCartInit();
        onHandle = true;
    }

    @Override
    public void handle(int position) {

    }

    @Override
    public void handle(String text) {

    }
}
