package com.example.harabazar.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.harabazar.Fragment.NoInternetFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.GetProfileRequest;
import com.example.harabazar.Service.request.VerifyOtpRequest;
import com.example.harabazar.Service.response.GetProfileResponse;
import com.example.harabazar.Service.response.VerifyResponse;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.CheckLocation;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OtpTextView;

public class OTP_VerificationActivity extends AppCompatActivity implements View.OnClickListener, OnRequestResponseListener {
    TextView  tvHeading;
    MaterialButton btnVerify ;
    String mVerificationId = "", otp;
    OtpTextView otp_view;
    ImageView ivBack;
    TextView resend, resendOtp;
    NoInternetFragment dialogFragment = new NoInternetFragment();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String countryCode = "";
    private String number = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);


        boolean b= CheckLocation.isLocationEnabled(OTP_VerificationActivity.this);
        if(b==true)
        {
            //   Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Toast.makeText(getApplicationContext(), "disable", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(OTP_VerificationActivity.this);
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

        getDataFromIntent();
        initView();
        setClickListener();
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                resend.setText("00:" + millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                resend.setText("Resend OTP");
                resend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sendVerificationCode();
                    }
                });

            }

        }.start();



/*
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OTP_VerificationActivity.this, ProfileCreateActivity.class);
                startActivity(intent);
            }
        });
*/
    }

    private void setClickListener() {
        btnVerify.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    private void getDataFromIntent() {
        number = getIntent().getStringExtra("number");
        countryCode = getIntent().getStringExtra("countryCode");
    }

    private void initView() {
        //Get Firebase auth instance

        btnVerify = findViewById(R.id.btnVerify);
        resend = findViewById(R.id.resend);
        resendOtp = findViewById(R.id.resendOtp);
        otp_view = findViewById(R.id.otp_view);
        tvHeading = findViewById(R.id.tvHeading);
        ivBack = findViewById(R.id.ivBack);
        String text = "<font color=#BDBDBD>Enter 6-digit code sent to </font> <font color=#F58433>(" + countryCode + ") " + number + "</font>";
        tvHeading.setText(Html.fromHtml(text));
        mAuth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    return;
                }
                // Get new Instance ID token
                String token = task.getResult();
                AppSettings.setUserToken(OTP_VerificationActivity.this, token);
                AppLogger.i(Utils.getTag(), "Refreshed token: " + token);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(final PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(Utils.getTag() + " ", " onVerificationCompleted:" + credential);

                String smsCode = credential.getSmsCode();
                Log.d(Utils.getTag() + " ", "smsCode =" + smsCode);

                if (smsCode != null && smsCode.length() == 6) {
                    AppLogger.i(Utils.getTag() + " ", " smsCode =" + smsCode);
                }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Do something after 5s = 5000ms
                        signInWithPhoneAuthCredential(credential);
                    }
                }, 100);

                Log.d(Utils.getTag(), "onVerificationCompleted:" + credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(Utils.getTag() + " ", " onVerificationFailed", e);
                //       Utils.hideLottieProgressBar();
                Utils.ShowToast(OTP_VerificationActivity.this, "OTP Verification Failed Click To ResendCode To Send OTP Again");

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }


            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(Utils.getTag() + " ", " onCodeSent:" + verificationId);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                Toast.makeText(getBaseContext(), "OTP Sent Successfully To Your Mobile",
                        //    String.format(getString(R.string.msg_enter_otp), mPhoneNumber),
                        Toast.LENGTH_LONG).show();
                // ...
//                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
//                signInWithPhoneAuthCredential(credential);
            }
        };
        sendVerificationCode();

    }

    private void sendVerificationCode() {
        AppLogger.i(Utils.getTag() + " ", " number =" + countryCode + number);
        //  Utils.showLottieProgressBar(getSupportFragmentManager());
        //  showLottieProgressBar(OTPVerificationActivity.this);
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                        .setPhoneNumber(
                                //   "+1 650-555-3434"
                                // +
                                countryCode + " " + number
                        )       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


        //  Utils.hideProgressBar();

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        //    showProgressHud();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            AppLogger.i(Utils.getTag(), "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();

                            AppLogger.i(Utils.getTag() + " ", " getUid =" + user.getUid());
                            AppLogger.i(Utils.getTag() + " ", " getEmail =" + user.getEmail());
                            AppLogger.i(Utils.getTag() + " ", " getPhoneNumber =" + user.getPhoneNumber());
                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    callVerifyOtpApi(otp_view.getOTP(), task.getResult());

                                }
                            });

                            //  AppSettings.setUId(OTPVerificationActivity.this, user.getUid());
                            //  checkUserData();

                        } else {
                            //   Utils.hideLottieProgressBar();
                            // Sign in failed, display a message and update the UI
                            Log.w(Utils.getTag() + " ", " signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                //    hideHud();
                                Toast.makeText(getBaseContext(), "The verification code entered was invalid!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack: {
                super.onBackPressed();
                break;
            }
            case R.id.btnVerify: {
                AppLogger.i(Utils.getTag() + " ", " OTP =" + otp);
                AppLogger.i(Utils.getTag() + " ", " otp_view OTP =" + otp_view.getOTP());
                AppLogger.i(Utils.getTag() + " ", " mVerificationId =" + mVerificationId);

                //    Utils.showProgressBar(OTPVerificationActivity.this);

                // showLottieProgressBar(OTPVerificationActivity.this);
                //     PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp + "");

                if (otp_view.getOTP().equals("")) {
                    Toast.makeText(OTP_VerificationActivity.this, "Please Enter OTP", Toast.LENGTH_SHORT).show();
                } else if (mVerificationId.equals("")) {
                    Toast.makeText(OTP_VerificationActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                } else {
                    //   Utils.showLottieProgressBar(getSupportFragmentManager());
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp_view.getOTP() + "");
                    signInWithPhoneAuthCredential(credential);
                }


                break;
            }
/*
            case R.id.tvResendCode: {
                AppLogger.i(TAGC + " ", " OTP =" + otp);
                AppLogger.i(TAGC + " ", " mVerificationId =" + mVerificationId);
                if (tvResendCode.getText().toString().equalsIgnoreCase(getResources().getString(R.string.don_t_received_the_code_resend_code))) {
                    //     Utils.showProgressBar(OTPVerificationActivity.this);
                    sendVerificationCode();
                }
                break;
            }
*/
        }

    }

    private void callVerifyOtpApi(String otp, String token) {
        VerifyOtpRequest request = new VerifyOtpRequest();
        request.setCountry_code(countryCode);
        request.setPhone_number(number);
        //   request.setOtp(otp);
        request.setDevice_type("2");
        request.setFcm_token(AppSettings.getUserToken(OTP_VerificationActivity.this));
        Connector connector = new Connector();
        ServerCommunicator.verifyOtp(connector, request);
        connector.setOnRequestResponseListener(this);
    }

    private void profileApi(String uid, String key) {
        Connector connector = new Connector();
        GetProfileRequest request = new GetProfileRequest();
        request.setUser_id(uid);
        ServerCommunicator.getProfile(connector, request, key);
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse) {

    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {

        if (webResponse instanceof VerifyResponse) {
            final VerifyResponse responseBody = (VerifyResponse) webResponse;

            if (responseBody.getStatus()) {
                AppSettings.setSessionKey(OTP_VerificationActivity.this, responseBody.getSessionkey());
                AppSettings.setUId(OTP_VerificationActivity.this, responseBody.getData().getUser_id());
                Log.d(Utils.getTag(), " sessionkey: " + responseBody.getSessionkey());
                Log.d(Utils.getTag(), " sessionkey: " + AppSettings.getUId(OTP_VerificationActivity.this));
                profileApi(AppSettings.getUId(getApplicationContext()), responseBody.getSessionkey());

            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(OTP_VerificationActivity.this, "" + responseBody.getMessage());
                    }
                });
            }
        }
        if (webResponse instanceof GetProfileResponse) {
            final GetProfileResponse responseBody = (GetProfileResponse) webResponse;

            if (responseBody.getStatus()) {

                if (!responseBody.getData().getEmail().isEmpty()) {
                    AppSettings.setUserName(OTP_VerificationActivity.this, responseBody.getData().getFull_name());
                    AppSettings.setUserEmail(OTP_VerificationActivity.this, responseBody.getData().getEmail());
                    AppSettings.setPhone(OTP_VerificationActivity.this, responseBody.getData().getPhone_number());
                    AppSettings.setLogin(OTP_VerificationActivity.this, true);
                    AppSettings.setUId(OTP_VerificationActivity.this, responseBody.getData().getUser_id());
                    Intent intent = new Intent(OTP_VerificationActivity.this, MainActivity.class);


                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(OTP_VerificationActivity.this, ProfileCreateActivity.class);
                    intent.putExtra("countryCode", countryCode);
                    intent.putExtra("number", number);
                    intent.putExtra("activityName", "Create Profile");
                    startActivity(intent);
                    finish();

                }
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(OTP_VerificationActivity.this, "" + responseBody.getMessage());
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
        runOnUiThread(new Runnable() {
            public void run() {
                Utils.ShowToast(OTP_VerificationActivity.this, edErrorData.getMessage());
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
}
