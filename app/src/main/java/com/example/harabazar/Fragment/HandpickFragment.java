package com.example.harabazar.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.example.harabazar.Activity.DailyListActivity;
import com.example.harabazar.Activity.MyOrdersActivity;
import com.example.harabazar.Activity.OrderMapActivity;
import com.example.harabazar.Activity.SplashActivity;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.CancelOrderRequest;
import com.example.harabazar.Service.request.GetInventoryRequest;
import com.example.harabazar.Service.request.GetOrdersRequest;
import com.example.harabazar.Service.request.GetProfileRequest;
import com.example.harabazar.Service.request.GetUsersRequest;
import com.example.harabazar.Service.request.SaveUserRequest;
import com.example.harabazar.Service.request.UpdateCoordinatesRequest;
import com.example.harabazar.Service.request.UpdateOrderStatusRequest;
import com.example.harabazar.Service.response.GetInventoryResponse;
import com.example.harabazar.Service.response.GetInventoryResponseData;
import com.example.harabazar.Service.response.GetOrdersResponse;
import com.example.harabazar.Service.response.GetOrdersResponseData;
import com.example.harabazar.Service.response.GetProfileResponse;
import com.example.harabazar.Service.response.GetProfileResponseData;
import com.example.harabazar.Service.response.GetSavedUsersResponse;
import com.example.harabazar.Service.response.GetUsersResponse;
import com.example.harabazar.Service.response.GetUsersResponseData;
import com.example.harabazar.Service.response.SaveUserResponse;
import com.example.harabazar.Service.response.UpdateCoordinatesResponse;
import com.example.harabazar.Service.response.UpdateOrderStatusResponse;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.CheckLocation;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HandpickFragment extends Fragment implements OnRequestResponseListener, EventHandler {
    final Handler handler = new Handler();
    final int delay = 20000;
    GoogleMap mMap;
    List<Marker> markers;
    Marker currentMarker;
    ImageView ivUserImage;
    MaterialCardView mcCurrentLocation;
    MaterialCardView mcShowAll;
    MaterialCardView mcHawkerSave;
    MaterialCardView mcOngoingOrder;
    MaterialCardView mcHelp;
    TextView tvHawkerName;
    TextView tvHawkerId;
    TextView tvHawkerRating;
    ImageView ivHawker;
    MaterialCardView mcHawkerCard;
    MaterialButton mbCheckInventory;
    MaterialButton mbRequestNow;
    GetSavedUsersResponse savedUsersResponse;
    boolean saved = false;
    GetUsersResponseData currentUserData = null;
    Geocoder geocoder;
    List<Address> addresses;
    int checkOngoing = -1;
    LoadingFragment loadingFragment = new LoadingFragment();
    NoInternetFragment dialogFragment = new NoInternetFragment();
    IncomingNotificationFragment incomingNotificationFragment;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private LatLng latLng = new LatLng(22.55820000, 75.11760000);
    private boolean markerFlag = true;
    private List<GetUsersResponseData> usersResponseData;
    private String currentUserId;
    private Activity context;
    Runnable userRunnable = new Runnable() {
        @Override
        public void run() {
            {
                getUserConnectorInit(latLng);
                handler.postDelayed(this, delay);
            }
        }
    };
    private final BroadcastReceiver notificationCountRefresh = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AppLogger.i("notificationCountRefresh", " notificationCountRefresh BroadcastReceiver -----");
            if (intent != null) {
                if (intent.hasExtra(Constants.NOTIFICATION_DATA + "")) {
//                    Toast.makeText(context, intent.getStringExtra(Constants.NOTIFICATION_DATA + ""), Toast.LENGTH_SHORT).show();
                    String notification = intent.getStringExtra(Constants.NOTIFICATION_DATA);
                    methods();

                }
            }
        }
    };
    //    LoadingFragment loadingFragment = new LoadingFragment();
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(getContext());
            mMap = googleMap;
            mapFunctions();
        }
    };
    private GetOrdersResponseData currentOrder;

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(notificationCountRefresh);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_handpick, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        boolean b = CheckLocation.isLocationEnabled(context);
        if (b == true) {
            //  Toast.makeText(context, "Enabled", Toast.LENGTH_SHORT).show();

        } else {
            // Toast.makeText(context, "disable", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

        LocalBroadcastManager.getInstance(context).registerReceiver(notificationCountRefresh, new IntentFilter(Constants.BROAD_REFRESH_NOTIFICATION_COUNT));

        mcCurrentLocation = view.findViewById(R.id.mcCurrentLocation);
        mcShowAll = view.findViewById(R.id.mcShowAll);
        tvHawkerName = view.findViewById(R.id.tvHawkerName);
        tvHawkerId = view.findViewById(R.id.tvHawkerId);
        tvHawkerRating = view.findViewById(R.id.tvHawkerRating);
        mcHawkerCard = view.findViewById(R.id.mcHawkerCard);
        mcHawkerSave = view.findViewById(R.id.mcHawkerSave);
        ivUserImage = view.findViewById(R.id.ivUserImage);
        mcOngoingOrder = view.findViewById(R.id.mcOngoingOrder);
        mbRequestNow = view.findViewById(R.id.mbRequestNow);
        mcHelp = view.findViewById(R.id.mcHelp);
        mbCheckInventory = view.findViewById(R.id.mbCheckInventory);
        ivHawker = view.findViewById(R.id.ivHawker);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(notificationCountRefresh, new IntentFilter(Constants.BROAD_REFRESH_NOTIFICATION_COUNT));

        methods();

    }

    private void methods() {
        handler.postDelayed(userRunnable, delay);
        if (!AppSettings.getOnboarding(context))
            showHandpickOnboarding();
        getCurrentLocation();
        initiatePlaces();
        listners();


    }

    private void showHandpickOnboarding() {
        HandpickBoarding handpickBoarding = new HandpickBoarding();
        handpickBoarding.show(getChildFragmentManager(), getClass().getSimpleName());
    }

    private void mapFunctions() {
        mcHawkerCard.setVisibility(View.GONE);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng position) {
                if (mcHawkerCard.getVisibility() == View.VISIBLE) {
                    mcHawkerCard.setVisibility(View.GONE);

                } else {
                    latLng = position;
//                    loadingFragment.show(getChildFragmentManager(), "");
                    moveCurrentLocation();
                }


            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (!marker.getTag().equals("USER")) {
                    currentUserId = marker.getTag().toString();
                    showUserCard();

                }
                return false;
            }
        });
    }

    private void showUserCard() {
        for (int i = 0; i < usersResponseData.size(); i++) {
            if (usersResponseData.get(i).getId().equals(currentUserId)) {
                currentUserData = usersResponseData.get(i);
                break;
            }
        }
        if (savedUsersResponse.getData().size() > 0) {
            for (int i = 0; i < savedUsersResponse.getData().size(); i++) {
                if (currentUserData.getId().equals(savedUsersResponse.getData().get(i).getId())) {
                    mcHawkerSave.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    saved = true;
                    break;
                } else {
                    mcHawkerSave.setCardBackgroundColor(getResources().getColor(R.color.grey, null));
                    saved = false;

                }
            }
        } else {
            mcHawkerSave.setCardBackgroundColor(getResources().getColor(R.color.grey, null));
            saved = false;
        }
        if (saved) {
            mcHawkerSave.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary, null));

        } else {
            mcHawkerSave.setCardBackgroundColor(getResources().getColor(R.color.grey, null));

        }

        //setHawkerData in card
        mcHawkerCard.setVisibility(View.VISIBLE);
        tvHawkerName.setText(currentUserData.getFull_name());
        tvHawkerId.setText("Fruit Cart ID " + currentUserId);
        tvHawkerRating.setText(currentUserData.getRating() + " Star");
        try {
            Glide.with(context).load(Constants.FILES_URL + "" + currentUserData.getProfile_image()).into(ivHawker);
        } catch (Exception e) {
        }
        mcHawkerSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saved) {
                    mcHawkerSave.setCardBackgroundColor(getResources().getColor(R.color.grey, null));
                    saved = false;

                } else {
                    mcHawkerSave.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
                    saved = true;

                }

                saveUserConnectorInit(currentUserId);
            }
        });
        mbCheckInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInventoryConnectorInit(currentUserId);
            }
        });


    }

    private void listners() {
        mcCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocation();
            }
        });
        mcHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHandpickOnboarding();
            }
        });


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(userRunnable);
        try {
            mMap.clear();
            for (int i = 0; i < markers.size(); i++) {
                markers.get(i).remove();
            }
            currentMarker.remove();
        } catch (Exception e) {
            AppLogger.e(Utils.getTag(), e.getMessage());
        }
        markers = new ArrayList<>();
        markerFlag = true;


    }

    private void getInventoryConnectorInit(String uid) {
        Connector connector = new Connector();
        GetInventoryRequest request = new GetInventoryRequest();
        request.setUser_id(uid);
        request.setLimit("10000");
        ServerCommunicator.getInventory(connector, request, AppSettings.getSessionKey(context));
        connector.setOnRequestResponseListener(this);
    }

    private void saveUserConnectorInit(String uid) {
        Connector connector = new Connector();
        SaveUserRequest request = new SaveUserRequest();
        request.setUser_id(uid);
        ServerCommunicator.saveUser(connector, request, AppSettings.getSessionKey(context));
        connector.setOnRequestResponseListener(this);
    }

    private void getOrdersConnectorInit() {
//        loadingFragment.show(getChildFragmentManager(),""+Constants.incrementalID++);
        Connector connector = new Connector();
        GetOrdersRequest getOrdersRequest = new GetOrdersRequest();
        getOrdersRequest.setRole("App User");
        ServerCommunicator.getOrdersList(connector, getOrdersRequest, AppSettings.getSessionKey(context));
        connector.setOnRequestResponseListener(this);
    }

    private void getUserConnectorInit(LatLng latLng) {
        Connector connector = new Connector();
        try {
            loadingFragment.dismiss();
        } catch (Exception e) {
            e.printStackTrace();

        }
        GetUsersRequest request = new GetUsersRequest();
        request.setLatitude(latLng.latitude + "");
        request.setLongitude(latLng.longitude + "");

        ServerCommunicator.getUsers(connector, request, AppSettings.getSessionKey(context));
        ServerCommunicator.getSavedUser(connector, AppSettings.getSessionKey(context));
        connector.setOnRequestResponseListener(this);
    }

    private void getAddress(LatLng latLng) {
        geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            currentMarker.setSnippet(address);

            mbRequestNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(getActivity(), DailyListActivity.class);
                    i.putExtra("address",address);
                    i.putExtra("latLng",latLng.toString());
                    getContext().startActivity(i);

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectorGetUserProfile(String id) {
        //        Connector connector = Connector.getConnector();
        Connector connector = new Connector();
        GetProfileRequest getProfileRequest = new GetProfileRequest();
        getProfileRequest.setUser_id(id);
        ServerCommunicator.getProfile(connector, getProfileRequest, AppSettings.getSessionKey(context));
        connector.setOnRequestResponseListener(this);
    }

    private void updateCoordinatesConnectorInit(LatLng latLng) {
        Connector connector = new Connector();

        UpdateCoordinatesRequest request = new UpdateCoordinatesRequest();
        request.setLatitude(latLng.latitude + "");
        request.setLongitude(latLng.longitude + "");
        ServerCommunicator.updateCoordinates(connector, request, AppSettings.getSessionKey(context));
        connector.setOnRequestResponseListener(this);
    }

    private void initiatePlaces() {
        if (!Places.isInitialized()) {
            Places.initialize(context, getResources().getString(R.string.google_maps_key));
        }
        Places.createClient(context);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;
                latLng = new LatLng(latitude, longitude);
                moveCurrentLocation();


            }


            @Override
            public void onError(@NonNull Status status) {
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                AppLogger.e("CURRENT_LOCATION ", latLng + "");
                                moveCurrentLocation();
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
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);

                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }


                        } else {
                            Toast.makeText(context, "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void moveCurrentLocation() {
        loadingFragment.show(getChildFragmentManager(), getClass().getSimpleName());

        updateCoordinatesConnectorInit(latLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        mMap.clear();
        currentMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Current Search ☺")
                .snippet(AppSettings.getUserName(context).split(" ")[0])
                .icon(Utils.bitmapDescriptorFromVector(context, R.drawable.location_pin)));
        currentMarker.setTag("USER");
        currentMarker.setTitle(latLng.toString());
        getAddress(latLng);
        getUserConnectorInit(latLng);

    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }
//"latitude":"23.14969348942452","longitude":"79.8791429400444"

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof GetUsersResponse) {
            final GetUsersResponse responseBody = (GetUsersResponse) webResponse;
            if (responseBody.getStatus()) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingFragment.dismisss();
                        List<GetUsersResponseData> temp = new ArrayList<>();
                        for (int i = 0; i < responseBody.getData().size(); i++) {
                            if (responseBody.getData().get(i).getLatitude() != null) {
                                if (responseBody.getData().get(i).getStatus().equals("1")) {
                                    temp.add(responseBody.getData().get(i));
                                }
                            }
                        }
                        setMarkers(temp);
                        getOrdersConnectorInit();
                    }
                });


            } else {
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(context, "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof UpdateCoordinatesResponse) {
            final UpdateCoordinatesResponse responseBody = (UpdateCoordinatesResponse) webResponse;
            if (responseBody.getStatus()) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        AppLogger.i("UserLocation", "Updated " + latLng.latitude + " " + latLng.longitude);

                    }
                });


            } else {
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(context, "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof GetSavedUsersResponse) {
            final GetSavedUsersResponse responseBody = (GetSavedUsersResponse) webResponse;
            if (responseBody.getStatus()) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        savedUsersResponse = responseBody;
                    }
                });


            } else {
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(context, "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof GetInventoryResponse) {
            final GetInventoryResponse responseBody = (GetInventoryResponse) webResponse;
            if (responseBody.getStatus()) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openInventory(responseBody.getData());
                    }
                });
            } else {
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(context, "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof SaveUserResponse) {
            final SaveUserResponse responseBody = (SaveUserResponse) webResponse;
            if (responseBody.getStatus()) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });


            } else {
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(context, "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof GetOrdersResponse) {
            final GetOrdersResponse responseBody = (GetOrdersResponse) webResponse;
            if (responseBody.getStatus()) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingFragment.dismiss();
                        checkOrdersSummary(responseBody.getData());
                    }

                });


            } else {
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(context, "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof UpdateOrderStatusResponse) {
            final UpdateOrderStatusResponse responseBody = (UpdateOrderStatusResponse) webResponse;
            if (responseBody.getStatus()) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getOrdersConnectorInit();
                    }

                });


            } else {
                context.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(context, "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof GetProfileResponse) {
            final GetProfileResponse responseBody = (GetProfileResponse) webResponse;
            if (responseBody.getStatus()) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gotoOrderMapActivity(responseBody.getData());
                    }

                });


            } else {
                context.runOnUiThread(new Runnable() {
                    public void run() {

                        Utils.ShowToast(context, "" + responseBody.getMessage());
                    }
                });
            }

        }
    }

    private void gotoOrderMapActivity(GetProfileResponseData data) {

        mcOngoingOrder.setVisibility(View.VISIBLE);
        mcOngoingOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng latLng = new LatLng(Double.parseDouble(data.getLatitude()), Double.parseDouble(data.getLongitude()));
                Intent i = new Intent(context, OrderMapActivity.class);
                i.putExtra("GetProfileResponseData", data);
                i.putExtra("GetOrdersResponseData", currentOrder);
                startActivity(i);


            }
        });
    }

    private void connectorChangeStatusHandpickReject(String id) {
//        Connector connector = Connector.getConnector();
        Connector connector = new Connector();
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrder_id(id);
        request.setOrder_status("9");
        ServerCommunicator.updateOrderStatus(connector, request, AppSettings.getSessionKey(context));
        connector.setOnRequestResponseListener(this);
    }


    private void checkOrdersSummary(List<GetOrdersResponseData> data) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getOrder_status().equals("5")) {
                if (data.get(i).getSchedule_date() == null && data.get(i).getSchedule_time() == null)
                    if (Utils.getDifferenceSec(Utils.toDate(data.get(i).getCreated_date().split(" ")[0], data.get(i).getCreated_date().split(" ")[1])) > 300) {
                        connectorChangeStatusHandpickReject(data.get(i).getOrder_id());
                    }
            }
        }
        checkOngoing = -1;
        for (int i = 0; i < data.size(); i++) {

            if (data.get(i).getSchedule_date() == null && data.get(i).getSchedule_time() == null && data.get(i).getOrder_status().equals("5")) {

                checkOngoing = i;
                break;
            }
            if (data.get(i).getOrder_status().equals("6") || data.get(i).getOrder_status().equals("7")) {
                checkOngoing = i;
                break;
            }
        }
        if (checkOngoing != -1) {
            currentOrder = data.get(checkOngoing);
            connectorGetUserProfile(data.get(checkOngoing).getSabjiwala_id());
        } else {
            mcOngoingOrder.setVisibility(View.GONE);

        }


    }

    private void connectorCancelHandpick(String order_id) {
        CancelOrderRequest request = new CancelOrderRequest();
        request.setCancel_reason("Testing Cancel");
        request.setOrder_id(order_id);
        Connector connector = new Connector();
        ServerCommunicator.cancelOrder(connector, request, AppSettings.getSessionKey(context));
        connector.setOnRequestResponseListener(this);
    }

    private void openInventory(List<GetInventoryResponseData> data) {

        DialogFragment dialogFragment = new SabziwalaProfileFragment(data, currentUserData, saved, this, addresses.get(0), latLng, checkOngoing);

        dialogFragment.show(getChildFragmentManager(), "Sabziwala");
//        onDestroy();
    }

    private void setMarkers(List<GetUsersResponseData> data) {

        this.usersResponseData = data;
        markers = new ArrayList<>();
        currentMarker = null;

        for (int i = 0; i < data.size(); i++) {
            try {
                LatLng position = new LatLng((Double.parseDouble(data.get(i).getLatitude())), (Double.parseDouble(data.get(i).getLongitude())));
                markers.add(mMap.addMarker(new MarkerOptions()
                        .position(position)
                        .title(data.get(i).getFull_name())
                        .icon(Utils.bitmapDescriptorFromVector(context, R.drawable.rickshaw))));
                markers.get(i).setTag(data.get(i).getId());

            } catch (Exception e) {
                e.printStackTrace();

            }


        }
        mcShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAllHawkers(data);
            }
        });
    }


    private void showAllHawkers(List<GetUsersResponseData> data) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(latLng);
        try {
            for (int i = 0; i < data.size(); i++) {
                LatLng position = new LatLng((Double.parseDouble(data.get(i).getLatitude())), (Double.parseDouble(data.get(i).getLongitude())));
                if (markers.get(i) == null) {
                    markers.add(i, mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(data.get(i).getFull_name())
                            .icon(Utils.bitmapDescriptorFromVector(context, R.drawable.rickshaw))));
                } else {
                    markers.get(i).setIcon(Utils.bitmapDescriptorFromVector(context, R.drawable.rickshaw));
                    markers.get(i).setPosition(position);
                }
                builder.include(position);

            }
        } catch (Exception e) {

        }
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mMap.animateCamera(cu);
    }

    @Override
    public void onUploadComplete(WebResponse webResponse) {

    }

    @Override
    public void onVFRClientException(WebErrorResponse edErrorData) {
        AppLogger.e(Utils.getTag(), edErrorData.getMessage() + edErrorData.getStatus());
        context.runOnUiThread(new Runnable() {
            public void run() {
                Utils.ShowToast(context, edErrorData.getMessage());
            }
        });
    }

    @Override
    public void onAuthException() {

    }

    @Override
    public void onNoConnectivityException(String message) {

        if (message.equals("-1")) {
            try {
                dialogFragment.dismiss();
            } catch (Exception e) {

            }
            if (!dialogFragment.isAdded())
                dialogFragment.show(getChildFragmentManager(), getClass().getSimpleName());
        }
        if (message.equals("-2")) {
            try {
                AppSettings.clearPrefs(context);
                Intent intent = new Intent(context, SplashActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } catch (Exception e) {

            }

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
        saved = !saved;
        showUserCard();
        getOrdersConnectorInit();
    }

    @Override
    public void handle(int position) {

    }

    @Override
    public void handle(String text) {

    }
}
