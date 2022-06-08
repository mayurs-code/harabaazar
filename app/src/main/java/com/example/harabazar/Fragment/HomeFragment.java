package com.example.harabazar.Fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.Activity.CartActivity;
import com.example.harabazar.Activity.MainActivity;
import com.example.harabazar.Adapter.BannerSliderAdapter;
import com.example.harabazar.Adapter.CategoriesAdapter;
import com.example.harabazar.Adapter.ProductsAdapter;
import com.example.harabazar.Adapter.RecommendedAdapter;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.BannersRequest;
import com.example.harabazar.Service.request.ProductListRequest;
import com.example.harabazar.Service.response.BannersResponse;
import com.example.harabazar.Service.response.BannersResponseData;
import com.example.harabazar.Service.response.CartResponse;
import com.example.harabazar.Service.response.CategoryResponse;
import com.example.harabazar.Service.response.CategoryResponseData;
import com.example.harabazar.Service.response.NotificationResponse;
import com.example.harabazar.Service.response.NotificationResponseData;
import com.example.harabazar.Service.response.ProductListResponse;
import com.example.harabazar.Service.response.ProductListResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.CheckLocation;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.card.MaterialCardView;
import com.smarteist.autoimageslider.SliderView;

import java.util.List;

public class HomeFragment extends Fragment implements OnRequestResponseListener, EventHandler {
    //    ViewPager offers;
    SliderView bannerSlider;
    BannerSliderAdapter bannerSliderAdapter;
    RecyclerView rvCategories;
    RecyclerView rvRecommended;
    TextView tvName;
    MaterialCardView tvSearch;
    TextView tvCategoriesViewAll;
    TextView tvRecommendedViewAll;
    CategoriesAdapter categoriesAdapter;
    RecommendedAdapter recommendedAdapter;
    MaterialCardView cvHandpickService;
    MaterialCardView cvDeliveryService;
    TextView tvCartSize;
    TextView tvNotificationSize;
    RelativeLayout rlCartBadge;
    RelativeLayout rlNotificationBadge;
    ImageView ivCart;
    ImageView ivNotifications;
    int getSize;
    boolean b = false;
    LoadingFragment loadingFragment = new LoadingFragment();
    NoInternetFragment dialogFragment = new NoInternetFragment();
    private Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getActivity();
        initUi(view);
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


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadingFragment.show(getChildFragmentManager(), "");
        methods();
    }

    private void initUi(View view) {
        bannerSlider = view.findViewById(R.id.offers);
        rvCategories = view.findViewById(R.id.rvCategories);
        tvNotificationSize = view.findViewById(R.id.tvNotificationSize);
        rlNotificationBadge = view.findViewById(R.id.rlNotificationBadge);
        rvRecommended = view.findViewById(R.id.rvRecommended);
        cvHandpickService = view.findViewById(R.id.cvHandpickService);
        cvDeliveryService = view.findViewById(R.id.cvDeliveryService);
        tvRecommendedViewAll = view.findViewById(R.id.tvRecommendedViewAll);
        rlCartBadge = view.findViewById(R.id.rlCartBadge);
        tvCartSize = view.findViewById(R.id.tvCartSize);
        tvCategoriesViewAll = view.findViewById(R.id.tvCategoriesViewAll);
        tvSearch = view.findViewById(R.id.tvSearch);
        tvName = view.findViewById(R.id.tvName);
        ivCart = view.findViewById(R.id.ivCart);
        ivNotifications = view.findViewById(R.id.ivNotifications);
        rvCategories.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));

    }

    private void methods() {
        viewListners();
        connectorInit();
        connectorCartInit();
        setData();
    }

    private void openNotification() {
        b = true;
        NotificationDialogFragment dialogFragment = new NotificationDialogFragment(rlNotificationBadge);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "NotificationDialogFragment");
//        SharedPreferences sharedPreferences1= getContext().getSharedPreferences("size", Context.MODE_PRIVATE);
//        int count=sharedPreferences1.getInt("key",-1);
//        Log.d("counter", "openNotification: "+count+" "+getSize);
//        if(count== getSize)//jo value api return karegi
//        {
//            rlNotificationBadge.setVisibility(View.GONE);
//        }
//        else
//        {
//            rlNotificationBadge.setVisibility(View.VISIBLE);
//            //show notification
//        }
    }

    private void setData() {
        tvName.setText(AppSettings.getUserName(getActivity()).split(" ")[0]);
    }

    private void connectorInit() {
        Connector connector = new Connector();
        ServerCommunicator.getCategory(connector, AppSettings.getSessionKey(getContext()));
        ServerCommunicator.getBanner(connector, new BannersRequest("0"), AppSettings.getSessionKey(getContext()));
        ProductListRequest productListRequest = new ProductListRequest();
        productListRequest.setIs_recommended("0");
        productListRequest.setLimit("1000");
        ServerCommunicator.getProducts(connector, productListRequest, AppSettings.getSessionKey(getContext()));
        ServerCommunicator.getNotifications(connector, AppSettings.getSessionKey(getContext()));

        connector.setOnRequestResponseListener(this);
    }

    private void viewListners() {
        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), CartActivity.class);
                getActivity().startActivity(i);
            }
        });
        ivNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNotification();
            }
        });
        cvHandpickService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.bottomNavigationView.setSelectedItemId(R.id.uncheckedItem);


            }
        });
        cvDeliveryService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.bottomNavigationView.setSelectedItemId(R.id.bottom_products);
            }
        });
        tvCategoriesViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.bottomNavigationView.setSelectedItemId(R.id.bottom_products);
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new CategoriesFragment()).commit();
            }
        });

        tvRecommendedViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.bottomNavigationView.setSelectedItemId(R.id.bottom_products);
                getParentFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecommendedFragment()).commit();
            }
        });
        tvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.bottomNavigationView.setSelectedItemId(R.id.bottom_products);
            }
        });
    }

    private void initLists(List<ProductListResponseData> data) {
        recommendedAdapter = new RecommendedAdapter(context, data, this);
        rvRecommended.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRecommended.setAdapter(recommendedAdapter);

    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        loadingFragment.dismisss();
        if (webResponse instanceof CategoryResponse) {
            final CategoryResponse responseBody = (CategoryResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
        if (webResponse instanceof BannersResponse) {
            final BannersResponse responseBody = (BannersResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setBannerListAdapter(responseBody.getData());
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
                        initLists(responseBody.getData());
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
                        if (responseBody.getData().getProducts().size() > 0) {
                            rlCartBadge.setVisibility(View.VISIBLE);
                            AnimationClass.setAnimationAlert(rlCartBadge);
                            AnimationClass.setAnimationRotate360(ivCart);


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

    private void setCategoriesAdapter(List<CategoryResponseData> data) {
        categoriesAdapter = new CategoriesAdapter(getContext(), data);
        AppLogger.i(Utils.getTag(), "ArrayList Size..." + data);
        rvCategories.setAdapter(categoriesAdapter);

    }

    private void setBannerListAdapter(List<BannersResponseData> data) {
        bannerSliderAdapter = new BannerSliderAdapter(getContext(), data);
        bannerSlider.setSliderAdapter(bannerSliderAdapter);
        bannerSlider.startAutoCycle();
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

    private void connectorCartInit() {
        Connector connector = new Connector();
        ServerCommunicator.getCartProductList(connector, AppSettings.getSessionKey(getContext()));
        connector.setOnRequestResponseListener(this);

    }


    @Override
    public void handle() {
        connectorCartInit();

    }

    @Override
    public void handle(int position) {

    }

    @Override
    public void handle(String text) {

    }
}
