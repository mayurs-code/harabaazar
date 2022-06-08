package com.example.harabazar.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.harabazar.Activity.Aboutus;
import com.example.harabazar.Activity.AddressActivity;
import com.example.harabazar.Activity.MainActivity;
import com.example.harabazar.Activity.MyOrdersActivity;
import com.example.harabazar.Activity.PreferredSabziwalaActivity;
import com.example.harabazar.Activity.ProfileCreateActivity;
import com.example.harabazar.Activity.SplashActivity;
import com.example.harabazar.R;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.GetProfileRequest;
import com.example.harabazar.Service.response.GetProfileResponse;
import com.example.harabazar.Service.response.GetProfileResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;

public class SettingFragment extends Fragment implements OnRequestResponseListener {

    LinearLayout myOrders;
    LinearLayout about;
    LinearLayout prefSabziwala;
    LinearLayout myAddress;
    LinearLayout myOffers;
    LinearLayout logOut;
    TextView tvUserName;
    TextView tvEmail;
    ImageView ivEdit;
    ImageView ivProfile;
    private Activity context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        initViews(view);
        context=getActivity();
        clickListener();
        methods();

        return view;
    }

    private void initViews(View view) {
        myOrders = view.findViewById(R.id.myOrders);
        myAddress = view.findViewById(R.id.myAddress);
        about = view.findViewById(R.id.about);
        tvUserName = view.findViewById(R.id.tvUserName);
        ivEdit = view.findViewById(R.id.ivEdit);
        tvEmail = view.findViewById(R.id.tvEmail);
        myOffers = view.findViewById(R.id.myOffers);
        prefSabziwala = view.findViewById(R.id.prefSabziwala);
        logOut = view.findViewById(R.id.logOut);
        ivProfile = view.findViewById(R.id.ivProfile);
    }

    private void methods() {
        connectorProfileInit();
        setData();
    }

    private void setData() {
        tvEmail.setText(AppSettings.getUserEmail(getActivity()));
        tvUserName.setText(AppSettings.getUserName(getActivity()));
    }

    private void clickListener() {
        myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), MyOrdersActivity.class);
                startActivity(i);
            }
        });
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), Aboutus.class);
                startActivity(i);
            }
        });
        prefSabziwala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), PreferredSabziwalaActivity.class);
                startActivity(i);
            }
        });
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), ProfileCreateActivity.class);
                i.putExtra("activityName", "Update Profile");
                startActivity(i);
            }
        });
        myAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AddressActivity.class);
                startActivity(i);
            }
        });
        myOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.bottomNavigationView.setSelectedItemId(R.id.bottom_offers);
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppSettings.clearPrefs(getActivity());
                Intent intent = new Intent(getActivity(), SplashActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
    private void setData(GetProfileResponseData data) {
        tvEmail.setText(data.getEmail());
        tvUserName.setText(data.getFull_name());
        Log.d("constasdf", "setData: "+Constants.FILES_URL + data.getProfile_image());

        try {
            Glide.with(context).load(Constants.FILES_URL + data.getProfile_image()).into(ivProfile);
            Log.d("const", "setData: "+Constants.FILES_URL + data.getProfile_image());
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    private void connectorProfileInit() {
        Connector connector = new Connector();

        GetProfileRequest getProfileRequest = new GetProfileRequest();
        getProfileRequest.setUser_id(AppSettings.getUId(context));
        ServerCommunicator.getProfile(connector, getProfileRequest, AppSettings.getSessionKey(getActivity()));
        connector.setOnRequestResponseListener(this);

    }


    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof GetProfileResponse) {
            final GetProfileResponse responseBody = (GetProfileResponse) webResponse;
            if (responseBody.getStatus()) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setData(responseBody.getData());

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

    @Override
    public void onUploadComplete(WebResponse webResponse) {

    }

    @Override
    public void onVFRClientException(WebErrorResponse edErrorData) {

    }

    @Override
    public void onAuthException() {

    }

    NoInternetFragment dialogFragment=new NoInternetFragment();

    @Override
    public void onNoConnectivityException(String message) {

        if(message.equals("-1")){
            dialogFragment.show(getChildFragmentManager(),""+Constants.incrementalID++);
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
