package com.example.harabazar.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import com.example.harabazar.Utilities.CheckLocation;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.harabazar.Fragment.HomeFragment;
import com.example.harabazar.Fragment.LoadingFragment;
import com.example.harabazar.Fragment.NoInternetFragment;
import com.example.harabazar.R;
import com.example.harabazar.Service.OnRequestResponseListener;
import com.example.harabazar.Service.communicator.Connector;
import com.example.harabazar.Service.communicator.ServerCommunicator;
import com.example.harabazar.Service.request.UpdateProfilePhotoRequest;
import com.example.harabazar.Service.request.UpdateProfileRequest;
import com.example.harabazar.Service.response.CreateProfilePhotoResponse;
import com.example.harabazar.Service.response.CreateProfileResponse;
import com.example.harabazar.Service.response.WebErrorResponse;
import com.example.harabazar.Service.response.WebResponse;
import com.example.harabazar.Utilities.AppLogger;
import com.example.harabazar.Utilities.AppSettings;
import com.example.harabazar.Utilities.Constants;
import com.example.harabazar.Utilities.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileCreateActivity extends AppCompatActivity implements View.OnClickListener, OnRequestResponseListener {
    MaterialButton btnSave;
    private LinearLayout header;
    private ImageView ivBack;
    private TextView tvText;
    private TextView tvTitle;
    private CircleImageView ivProfileImage;
    private ImageView ivCamera;
    private EditText etFullName;
    private EditText etEmail;
    private EditText etMobile;
    private RelativeLayout pic;
    private String countryCode = "";
    private String number = "";
    private String activityName = "Create Profile";
    private String picturePath;
    private static int GALLERY_CODE = 3;
    private  Uri selectedImageUri;
    LoadingFragment loadingFragment;
    RelativeLayout main;
    int count=0;
    String encodeString;


    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_create);
        getDataFromIntent();
        initView();
        setData();
        setClickListener();
        methods();
        boolean b= CheckLocation.isLocationEnabled(ProfileCreateActivity.this);
        if(b==true)
        {
            //   Toast.makeText(getApplicationContext(), "Enabled", Toast.LENGTH_SHORT).show();
        }
        else
        {
            // Toast.makeText(getApplicationContext(), "disable", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getApplicationContext())
                        .withPermissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, GALLERY_CODE);
                        count=1;

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                     permissionToken.continuePermissionRequest();
                    }
                }).check();
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            selectedImageUri = data.getData();
            Log.d("pathpath", "onActivityResult: " + data);
            picturePath = getPath(getApplicationContext(), selectedImageUri);
            String name = picturePath.substring(picturePath.lastIndexOf("/") + 1);
            // textView.setText(name);


            Log.d("Picture Path", picturePath);

            Bitmap bmp = null;
            ByteArrayOutputStream bos = null;
            byte[] bt = null;
            encodeString = null;
            try {
                bmp = BitmapFactory.decodeFile(picturePath);
                bos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bt = bos.toByteArray();
                encodeString = Base64.encodeToString(bt, Base64.DEFAULT);
                Log.d("base69", "onActivityResult: " + encodeString);


                if (count == 1)
                {
                    ivProfileImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    String e1 = Base64.encodeToString(bt, Base64.DEFAULT);
                }
                //  Api(encodeString,name);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void methods() {

    }

    private void setData() {
        tvTitle.setText(activityName);
    }

    private void setClickListener() {
        btnSave.setOnClickListener(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getDataFromIntent() {
        number = getIntent().getStringExtra("number");
        countryCode = getIntent().getStringExtra("countryCode");
        activityName = getIntent().getStringExtra("activityName");
    }

    private void initView() {
        header = (LinearLayout) findViewById(R.id.header);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        tvText = (TextView) findViewById(R.id.tvText);
        ivProfileImage = (CircleImageView) findViewById(R.id.ivProfileImage);
        tvTitle = findViewById(R.id.tvTitle);
        ivCamera = (ImageView) findViewById(R.id.ivCamera);
        etFullName = (EditText) findViewById(R.id.etFullName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etMobile = (EditText) findViewById(R.id.etMobile);
        btnSave = findViewById(R.id.btnSave);
        etMobile.setText(countryCode + " " + number);
        pic = findViewById(R.id.pic);
        if (AppSettings.getPhone(this) != null) {
            etMobile.setText("+91 " + AppSettings.getPhone(this));

        }
        if (AppSettings.getUserPic(this) != null) {
            try {
                Glide.with(this).load(AppSettings.getUserPic(this)).into(ivProfileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnSave) {
            if (Utils.isBlankOrNull(etFullName.getText().toString())) {
                etFullName.setFocusable(true);
                etFullName.setError("Enter name");
            }   else {
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        callUpdateProfileApi( task.getResult());
                    }
                });
               if(encodeString!=null)
               {
                   if(!encodeString.isEmpty())
                   {
                       Api(encodeString);
                   }
               }


                //api call code
            }
           /* Intent intent = new Intent(ProfileCreateActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);*/
        }
    }

    private void Api(String profile)
    {
        //loader code
        loadingFragment = new LoadingFragment();
        loadingFragment.show(getSupportFragmentManager(), "");
        UpdateProfilePhotoRequest request = new UpdateProfilePhotoRequest();
        request.setProfile_image_str(profile);
        Connector connector = new Connector();
        ServerCommunicator.updateProfilePhoto(connector, request, AppSettings.getSessionKey(ProfileCreateActivity.this));
        connector.setOnRequestResponseListener(this);

        Log.d("logres", "Api: "+profile);
    }


    private void callUpdateProfileApi(String token) {
        UpdateProfileRequest request = new UpdateProfileRequest();
        if (AppSettings.getPhone(this) != null) {
            request.setPhone_number(AppSettings.getPhone(this));
        }
        if (number != null) {
            request.setPhone_number(number);
        }

        request.setEmail(etEmail.getText().toString());
        //   request.setOtp(otp);
        request.setFull_name(etFullName.getText().toString());
        request.setFcm_token(AppSettings.getUserToken(ProfileCreateActivity.this));
        Connector connector = new Connector();
        ServerCommunicator.updateProfile(connector, request, AppSettings.getSessionKey(ProfileCreateActivity.this));
        connector.setOnRequestResponseListener(this);
    }

    @Override
    public void onAddMoreResponse(WebResponse webResponse)
    {


    }

    @Override
    public void onHttpResponse(WebResponse webResponse) {
        if (webResponse instanceof CreateProfileResponse) {
            final CreateProfileResponse responseBody = (CreateProfileResponse) webResponse;

            if (responseBody.getStatus()) {
                AppSettings.setUserEmail(ProfileCreateActivity.this, responseBody.getData().getEmail());
                AppSettings.setUserName(ProfileCreateActivity.this, responseBody.getData().getFull_name());
                AppSettings.setPhone(ProfileCreateActivity.this, responseBody.getData().getPhone_number());
                AppSettings.setLogin(ProfileCreateActivity.this, true);
                Intent intent = new Intent(ProfileCreateActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.ShowToast(ProfileCreateActivity.this, "" + responseBody.getMessage());
                    }
                });
            }
        }

        if(webResponse instanceof CreateProfilePhotoResponse)
        {
            final CreateProfilePhotoResponse responseBody = (CreateProfilePhotoResponse) webResponse;
            Log.d("getResponse", "onHttpResponse: "+responseBody.getData().getFull_path());
            runOnUiThread(new Runnable() {
                public void run() {
                 //   Utils.ShowToast(ProfileCreateActivity.this, "Profile Updated Successfully");
                    Dialog dialog = new Dialog(ProfileCreateActivity.this);
                    dialog.setContentView(R.layout.successdialog);
                    dialog.setCancelable(false);
                    dialog.getWindow().setLayout(1000, 1200);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    TextView cancel=dialog.findViewById(R.id.cancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
//                            FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
//                            fragmentTransaction.replace(R.id.main,new HomeFragment()).commit();
                            Intent i=new Intent(getApplicationContext(),MainActivity.class);
                            i.putExtra("key","home");
                            startActivity(i);

                            finish();


                        }
                    });
                    dialog.show();

                }
            });

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
                Utils.ShowToast(ProfileCreateActivity.this, edErrorData.getMessage());
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
