package com.example.harabazar.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.Adapter.ProductsAdapter;
import com.example.harabazar.R;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.ProductListRequest;
import com.example.harabazar.Service.response.CartResponse;
import com.example.harabazar.Service.response.CartResponseData;
import com.example.harabazar.Service.response.ProductListResponse;
import com.example.harabazar.Service.response.ProductListResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AnimationClass;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;

import java.util.List;

public class RecommendedFragment extends Fragment implements OnRequestResponseListener {
    ProductsAdapter categoriesAdapter;
    RecyclerView rvCategories;
    ImageView ivBack;
    private List<ProductListResponseData> cartResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvCategories = view.findViewById(R.id.rvCategories);
        ivBack = view.findViewById(R.id.ivBack);

    }
    LoadingFragment loadingFragment = new LoadingFragment();

    @Override
    public void onResume() {
        super.onResume();
        loadingFragment.show(getChildFragmentManager(), "");
        methods();
    }

    private void methods() {
        connectorCartInit();

        listners();
    }
    private void connectorCartInit() {
        Connector connector = new Connector();
        ServerCommunicator.getCartProductList(connector,  AppSettings.getSessionKey(getContext()));

        connector.setOnRequestResponseListener(this);

    }

    private void listners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void connectorInit() {
        Connector connector = new Connector();
        ProductListRequest productListRequest = new ProductListRequest();
        productListRequest.setIs_recommended("0");
        ServerCommunicator.getProducts(connector, productListRequest, AppSettings.getSessionKey(getContext()));

        connector.setOnRequestResponseListener(this);
    }


    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }
    CartResponseData cartResponseData;

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        loadingFragment.dismisss();
        if (webResponse instanceof ProductListResponse) {
            final ProductListResponse responseBody = (ProductListResponse) webResponse;
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
        if (webResponse instanceof CartResponse) {
            final CartResponse responseBody = (CartResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        connectorInit();
                        cartResponseData = responseBody.getData();
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

    private void setCategoriesAdapter(List<ProductListResponseData> data) {
        categoriesAdapter = new ProductsAdapter(getContext(),   data,cartResponseData,null);
        AppLogger.i(Utils.getTag(), "ArrayList Size..." + data);
        rvCategories.setAdapter(categoriesAdapter);
        rvCategories.setLayoutManager(new GridLayoutManager(getActivity(), 1));

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