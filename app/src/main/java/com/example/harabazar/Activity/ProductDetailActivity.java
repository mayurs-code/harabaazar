package com.example.harabazar.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.Adapter.ProductImagesSliderAdapter;
import com.example.harabazar.Adapter.VariantSelectAdapter;
import com.example.harabazar.Fragment.LoadingFragment;
import com.example.harabazar.Fragment.NoInternetFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.AddRemoveProductRequest;
import com.example.harabazar.Service.response.AddRemoveProductResponse;
import com.example.harabazar.Service.response.CartProductListResponseData;
import com.example.harabazar.Service.response.CartResponse;
import com.example.harabazar.Service.response.ImageResponse;
import com.example.harabazar.Service.response.ProductListResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.CheckLocation;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.smarteist.autoimageslider.SliderView;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity implements OnRequestResponseListener, EventHandler {

    ImageView ivBack;
    ImageView ivCart;
    ProductListResponseData productListResponseData;
    TextView tvName;
    TextView tvOfferPrice;
    TextView tvPrice;
    TextView tvdescription;
    TextView tvNutritions;
    TextView tvBenefits;
    MaterialButton mbAddToCart;
    MaterialButton mbGoToCart;
    TextView tvStorage;
    TextView tvVariants;
    TextView tvQty;
    TextView tvCartSize;
    ImageView ivMoreNutrition;
    ImageView ivMoreBenefits;
    ImageView ivMoreStorage;
    MaterialCardView cvAdd;
    RecyclerView rvVariants;
    MaterialCardView cvRemove;
    SliderView productSlider;
    ProductImagesSliderAdapter productImagesSliderAdapter;
    String session;
    LoadingFragment loadingFragment = new LoadingFragment();
    RelativeLayout rlCartBadge;
    NoInternetFragment dialogFragment = new NoInternetFragment();
    private int selectedVariant = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        productListResponseData = (ProductListResponseData) getIntent().getSerializableExtra("product");
        session = AppSettings.getSessionKey(this);
        boolean b= CheckLocation.isLocationEnabled(ProductDetailActivity.this);
        if(b==true)
        {
            //   Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Toast.makeText(getApplicationContext(), "disable", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(ProductDetailActivity.this);
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

        initView();


    }

    @Override
    public void onResume() {
        super.onResume();
        loadingFragment.show(getSupportFragmentManager(), "");
        methods();
    }

    private void methods() {
        setData(productListResponseData);
        setListners();
        setVarientData();
        connectorCartInit();

    }

    private void setProductImageSlider(List<ImageResponse> images) {
        productImagesSliderAdapter = new ProductImagesSliderAdapter(this, images);
        productSlider.setSliderAdapter(productImagesSliderAdapter);
    }

    private void initView() {
        rlCartBadge = findViewById(R.id.rlCartBadge);

        tvCartSize = findViewById(R.id.tvCartSize);
        ivBack = findViewById(R.id.ivBack);
        cvAdd = findViewById(R.id.cvAdd);
        mbGoToCart = findViewById(R.id.mbGoToCart);
        cvRemove = findViewById(R.id.cvRemove);
        rvVariants = findViewById(R.id.rvVariants);
        productSlider = findViewById(R.id.productSlider);
        ivCart = findViewById(R.id.ivCart);
        tvName = findViewById(R.id.tvName);
        tvOfferPrice = findViewById(R.id.tvOfferPrice);
        tvPrice = findViewById(R.id.tvPrice);
        tvStorage = findViewById(R.id.tvStorage);
        tvdescription = findViewById(R.id.tvdescription);
        tvVariants = findViewById(R.id.tvVariants);
        ivMoreStorage = findViewById(R.id.ivMoreStorage);
        tvNutritions = findViewById(R.id.tvNutritions);
        tvQty = findViewById(R.id.tvQty);
        tvBenefits = findViewById(R.id.tvBenefits);
        mbAddToCart = findViewById(R.id.mbAddToCart);
        ivMoreNutrition = findViewById(R.id.ivMoreNutrition);
        ivMoreBenefits = findViewById(R.id.ivMoreBenefits);

        ivMoreNutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvNutritions.getVisibility() == View.VISIBLE) {
                    tvNutritions.setVisibility(View.GONE);
                    ivMoreNutrition.setRotation(0);
                } else {
                    tvNutritions.setVisibility(View.VISIBLE);
                    ivMoreNutrition.setRotation(90);

                }

            }
        });
        ivMoreBenefits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvBenefits.getVisibility() == View.VISIBLE) {
                    tvBenefits.setVisibility(View.GONE);
                    ivMoreBenefits.setRotation(0);

                } else {
                    tvBenefits.setVisibility(View.VISIBLE);
                    ivMoreBenefits.setRotation(90);

                }

            }
        });
        ivMoreStorage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tvStorage.getVisibility() == View.VISIBLE) {
                    tvStorage.setVisibility(View.GONE);
                    ivMoreStorage.setRotation(0);

                } else {
                    tvStorage.setVisibility(View.VISIBLE);
                    ivMoreStorage.setRotation(90);

                }

            }
        });
    }

    private void setData(ProductListResponseData product) {
        tvName.setText(product.getName());
        tvOfferPrice.setText("₹ " + product.getVariants().get(selectedVariant).getPrice());
        tvPrice.setText("₹ " + product.getVariants().get(selectedVariant).getDisplay_price());
        tvVariants.setText("" + product.getVariants().get(selectedVariant).getSize() + product.getVariants().get(0).getUnit());
        tvdescription.setText(product.getProduct_detail());
        tvNutritions.setText(product.getNutritions());
        tvBenefits.setText(product.getBenifits());
        tvStorage.setText(product.getStorage_and_uses());
        tvPrice.setPaintFlags(tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        connectorCartInit();

        setProductImageSlider(product.getImages());


    }

    private void setVarientData() {
        cvRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvQty.setText("" + Math.max(1, (Integer.parseInt(tvQty.getText().toString()) - 1)));
            }
        });
        cvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvQty.setText("" + Math.min(10, (Integer.parseInt(tvQty.getText().toString()) + 1)));


            }
        });
        VariantSelectAdapter variantSelectAdapter = new VariantSelectAdapter(this, productListResponseData.getVariants(), this);
        rvVariants.setAdapter(variantSelectAdapter);
        rvVariants.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));

    }

    private void setListners() {
        ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProductDetailActivity.this, CartActivity.class);
                startActivity(i);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mbAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initConnector(productListResponseData, selectedVariant);

            }
        });
        mbGoToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProductDetailActivity.this, CartActivity.class);
                startActivity(i);
            }
        });
    }

    private void initConnector(ProductListResponseData productListResponseData, int position) {
        Connector connector = new Connector();
        AddRemoveProductRequest request = new AddRemoveProductRequest();
        request.setAction("1");
        request.setQuantity(tvQty.getText().toString());
        request.setProduct_id(productListResponseData.getId());
        request.setProduct_variant_id(productListResponseData.getVariants().get(position).getId());
        ServerCommunicator.addRemoveProducts(connector, request, session);
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        loadingFragment.dismisss();
        if (webResponse instanceof AddRemoveProductResponse) {
            final AddRemoveProductResponse responseBody = (AddRemoveProductResponse) webResponse;
            if (responseBody.getStatus()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectorCartInit();

                    }
                });


            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(ProductDetailActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }
        if (webResponse instanceof CartResponse) {
            final CartResponse responseBody = (CartResponse) webResponse;
            if (responseBody.getStatus()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseBody.getData().getProducts().size() > 0) {
                            rlCartBadge.setVisibility(View.VISIBLE);
                        }
                        tvCartSize.setText("" + Math.min(9, responseBody.getData().getProducts().size()));
                        checkInProduct(responseBody.getData().getProducts());
                    }
                });


            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(ProductDetailActivity.this, "" + responseBody.getMessage());
                    }
                });
            }

        }


    }

    private void checkInProduct(List<CartProductListResponseData> products) {
        mbAddToCart.setVisibility(View.VISIBLE);
        mbGoToCart.setVisibility(View.GONE);
        for (CartProductListResponseData product : products) {
            if (productListResponseData.getId().equals(product.getId())) {
                if (product.getVariants().get(selectedVariant).getCart_quantity() != null) {
                    mbAddToCart.setVisibility(View.GONE);
                    mbGoToCart.setVisibility(View.VISIBLE);
                }
                break;
            }
        }
    }

    private void connectorCartInit() {
        Connector connector = new Connector();
        ServerCommunicator.getCartProductList(connector, session);
        connector.setOnRequestResponseListener(this);

    }

    @Override
    public void onUploadComplete(WebResponse webResponse) {

    }

    @Override
    public void onVFRClientException(WebErrorResponse edErrorData) {
        AppLogger.e(Utils.getTag(), edErrorData.getMessage());
        runOnUiThread(new Runnable() {
            public void run() {
                Utils.ShowToast(ProductDetailActivity.this, edErrorData.getMessage());
            }
        });
    }

    @Override
    public void onAuthException() {

    }

    @Override
    public void onNoConnectivityException(String message) {

        if (message.equals("-1")) {
            dialogFragment.show(getSupportFragmentManager(), "" + Constants.incrementalID++);
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

    }

    @Override
    public void handle(int position) {
        selectedVariant = position;
        setData(productListResponseData);

    }

    @Override
    public void handle(String text) {

    }
}