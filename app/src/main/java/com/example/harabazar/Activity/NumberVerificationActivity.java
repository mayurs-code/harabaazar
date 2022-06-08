package com.example.harabazar.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.harabazar.Fragment.NoInternetFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.OTPRequest;
import com.example.harabazar.Service.response.OTPResponseBody;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.CheckLocation;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.button.MaterialButton;

public class NumberVerificationActivity extends AppCompatActivity implements OnRequestResponseListener {
    MaterialButton btnLogin;
    EditText etNumber;
    ImageView ivBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_verification);
        btnLogin = findViewById(R.id.btnLogin);
        ivBack = findViewById(R.id.ivBack);
        etNumber = findViewById(R.id.etNumber);
        boolean b= CheckLocation.isLocationEnabled(NumberVerificationActivity.this);
        if(b==true)
        {
            //   Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Toast.makeText(getApplicationContext(), "disable", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(NumberVerificationActivity.this);
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


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.isBlankOrNull(etNumber.getText().toString()))
                {
                    if(etNumber.getText().toString().length()==10)
                    {
                        // callSendOtpApi(etNumber.getText().toString());
                        Intent intent = new Intent(NumberVerificationActivity.this, OTP_VerificationActivity.class);
                        intent.putExtra("countryCode","+91");
                        intent.putExtra("number",etNumber.getText().toString());
//                      intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY | intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Utils.ShowToast(NumberVerificationActivity.this,"Please Enter Correct Number");
                    }

                }
                else {
                    Utils.ShowToast(NumberVerificationActivity.this,"Please Enter Number");
                }
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }



    private void callSendOtpApi(String phone) {
        OTPRequest request = new OTPRequest();
        request.setCountryCode("+91");
        request.setPhoneNumber(phone);
        Connector connector = new Connector();
        ServerCommunicator.loginWithOtp(connector, request);
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }
// api response
    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof OTPResponseBody) {
            final OTPResponseBody responseBody = (OTPResponseBody) webResponse;

            if (responseBody.getStatus()) {
                Intent intent = new Intent(NumberVerificationActivity.this, OTP_VerificationActivity.class);
                intent.putExtra("countryCode","+91");
                intent.putExtra("mobile",etNumber.getText().toString());
//                intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY | intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK); // Adds the FLAG_ACTIVITY_NO_HISTORY flag
                startActivity(intent);
                finish();

            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(NumberVerificationActivity.this,""+responseBody.getMessage());
                    }
                });
            }
        }
    }

    @Override
    public void onUploadComplete(WebResponse webResponse) {

    }
// Exception from api
    @Override
    public void onVFRClientException(WebErrorResponse edErrorData) {
        AppLogger.e(Utils.getTag(), edErrorData.getMessage());
        runOnUiThread(new Runnable() {
            public void run() {
                Utils.ShowToast(NumberVerificationActivity.this, edErrorData.getMessage());
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
            dialogFragment.show(getSupportFragmentManager(),""+ Constants.incrementalID++);
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
