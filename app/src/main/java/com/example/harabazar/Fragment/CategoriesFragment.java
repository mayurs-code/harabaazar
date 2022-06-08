package com.example.harabazar.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.harabazar.Adapter.CategoriesAdapter;
import com.example.harabazar.R;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.response.CategoryResponse;
import com.example.harabazar.Service.response.CategoryResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;

import java.util.List;

public class CategoriesFragment extends Fragment implements OnRequestResponseListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    CategoriesAdapter categoriesAdapter;
    ImageView ivBack;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);

    }

    RecyclerView rvCategories;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvCategories=view.findViewById(R.id.rvCategories);
        ivBack = view.findViewById(R.id.ivBack);
//        loadingFragment.show(getChildFragmentManager(),"");
        methods();

    }
    LoadingFragment loadingFragment = new LoadingFragment();

    @Override
    public void onResume() {
        super.onResume();
        loadingFragment.show(getChildFragmentManager(), "");
        methods();
    }

    private void methods() {
        connectorInit();
        listners();

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

        ServerCommunicator.getCategory(connector, AppSettings.getSessionKey(getContext()));
        connector.setOnRequestResponseListener(this);
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
    }

    private void setCategoriesAdapter(List<CategoryResponseData> data) {
        categoriesAdapter = new CategoriesAdapter(getContext(), data );
        AppLogger.i(Utils.getTag(), "ArrayList Size..." + data);
        rvCategories.setAdapter(categoriesAdapter);
        rvCategories.setLayoutManager(new GridLayoutManager(getActivity(),2));

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