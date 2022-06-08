package com.example.harabazar.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.harabazar.Adapter.NotificationAdapter;
import com.example.harabazar.R;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.response.NotificationResponse;
import com.example.harabazar.Service.response.NotificationResponseData;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;

import java.util.List;

public class NotificationDialogFragment extends DialogFragment implements OnRequestResponseListener {

    LinearLayout llEmpty;
    RecyclerView rvNotifications;
    ImageView ivBack;
    RelativeLayout notify;



    public NotificationDialogFragment(RelativeLayout notify)
    {
        this.notify=notify;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);

    }
    LoadingFragment loadingFragment = new LoadingFragment();

    @Override
    public void onResume() {
        super.onResume();
        loadingFragment.show(getChildFragmentManager(), "");
        methods();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        llEmpty = view.findViewById(R.id.llEmpty);
        ivBack = view.findViewById(R.id.ivBack);
        rvNotifications = view.findViewById(R.id.rvNotifications);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Material);
    }

    private void methods() {
        connectorInit();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                notify.setVisibility(View.GONE);
            }
        });


    }

    private void connectorInit() {
        Connector connector = new Connector();
        ServerCommunicator.getNotifications(connector, AppSettings.getSessionKey(getContext()));
        ServerCommunicator.readNotification(connector, AppSettings.getSessionKey(getContext()));
        connector.setOnRequestResponseListener(this);
    }


    @Override
    public void onAddMoreResponse(WebResponse webResponse) {


    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        loadingFragment.dismisss();
        if (webResponse instanceof NotificationResponse) {
            final NotificationResponse responseBody = (NotificationResponse) webResponse;
            if (responseBody.getStatus()) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(responseBody.getData().size()<=0){

                            llEmpty.setVisibility(View.VISIBLE);

                        }else {
                            llEmpty.setVisibility(View.GONE);
                            setAdapter(responseBody.getData());
                            Log.d("response", "run: "+responseBody.getData().size());
                            int x=responseBody.getData().size();
                            SharedPreferences sharedPreferences= getContext().getSharedPreferences("size", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor=sharedPreferences.edit();
                            editor.putInt("key",x);
                            editor.commit();
                            notify.setVisibility(View.GONE);

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

    private void setAdapter(List<NotificationResponseData> data) {
        NotificationAdapter adapter=new NotificationAdapter(getActivity(),data);
        rvNotifications.setAdapter(adapter);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getActivity()));

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
