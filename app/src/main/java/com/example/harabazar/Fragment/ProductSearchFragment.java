package com.example.harabazar.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.Adapter.ProductsAdapter;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.ProductListRequest;
import com.example.harabazar.Service.response.CartResponseData;
import com.example.harabazar.Service.response.CategoryResponseData;
import com.example.harabazar.Service.response.ProductListResponse;
import com.example.harabazar.Service.response.ProductListResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;

import java.util.List;

public class ProductSearchFragment extends Fragment implements OnRequestResponseListener {
    private final CategoryResponseData categoryResponseData;
    private final CartResponseData cartResponseData;
    private final EventHandler handler;
    RecyclerView rvProducts;
    ProductsAdapter productsAdapter;
    TextView test;
    int i = 0;
    private String session;
    private boolean alreadyLoaded=false;

    public ProductSearchFragment(CategoryResponseData categoryResponseData, CartResponseData cartResponseData,EventHandler handler) {
        System.out.println("axbyaxby onConstructor "+ i++ +" "+categoryResponseData.getId());

        this.categoryResponseData = categoryResponseData;
        this.cartResponseData = cartResponseData;
        this.handler = handler;
    }


    @Override
    public void onPause() {
        super.onPause();
        System.out.println("axbyaxby onPause "+ i++ +" "+categoryResponseData.getId());
//        onDestroy();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvProducts = view.findViewById(R.id.rvProducts);
        test = view.findViewById(R.id.test);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 1));
        session = AppSettings.getSessionKey(getContext());

        loadingFragment.show(getChildFragmentManager(), "");
        methods();

    }

    LoadingFragment loadingFragment = new LoadingFragment();

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!categoryResponseData.getId().equals(ProductsFragment.tab_id)) {
            try{
                loadingFragment.dismisss();
            }catch (Exception e){
                //ignore
            }
        }
    }

    private void methods() {
        connectorInit();
    }

    private void connectorInit() {

        Connector connector = new Connector();
        ProductListRequest productListRequest = new ProductListRequest();
        productListRequest.setLimit("1000");
        productListRequest.setCategory_id(categoryResponseData.getId());
        test.setText(categoryResponseData.getId());
        ServerCommunicator.getProducts(connector, productListRequest, session);
        connector.setOnRequestResponseListener(this);

    }

    private void setProductListAdapter(List<ProductListResponseData> data) {
        productsAdapter = new ProductsAdapter(getActivity(), data,cartResponseData, handler);
        rvProducts.setAdapter(productsAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_search, container, false);

    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }


    @Override
    public void onHttpResponse(WebResponse webResponse) {
        loadingFragment.dismisss();
        alreadyLoaded=true;

        if (webResponse instanceof ProductListResponse) {
            final ProductListResponse responseBody = (ProductListResponse) webResponse;

            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
    NoInternetFragment dialogFragment=new NoInternetFragment();

    @Override
    public void onNoConnectivityException(String message) {

        if(message.equals("-1")){
            dialogFragment.show(getChildFragmentManager(),""+ Constants.incrementalID++);
        }if(message.equals("1")){
            try{
                dialogFragment.dismiss();
            }catch (Exception e){

            }
        }
    }
    @Override
    public void onNoCachedDataAvailable() {

    }
}