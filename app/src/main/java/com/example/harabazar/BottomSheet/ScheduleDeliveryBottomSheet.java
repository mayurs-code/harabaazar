package com.example.harabazar.BottomSheet;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.harabazar.Activity.CheckoutActivity;
import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.response.ApplyCouponResponseData;
import com.example.harabazar.Service.response.CartResponseData;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class ScheduleDeliveryBottomSheet extends BottomSheetDialogFragment {
    final Calendar myCalendar = Calendar.getInstance();
    BottomSheetBehavior bottomSheetBehavior;
    MaterialButton mbConfirm;
    TextView tvDate;
    TextView tvTime;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    private LatLng latLng;
    private EventHandler handler;
    private Address address;
    private ApplyCouponResponseData applyCouponResponseData;
    private CartResponseData cartResponseData;
    private String sabziwalaID;

    public ScheduleDeliveryBottomSheet(ApplyCouponResponseData applyCouponResponseData, CartResponseData cartResponseData) {
        this.applyCouponResponseData = applyCouponResponseData;
        this.cartResponseData = cartResponseData;
    }

    public ScheduleDeliveryBottomSheet(String id, Address address, LatLng latLng, EventHandler handler) {
        this.sabziwalaID = id;
        this.address = address;
        this.latLng = latLng;
        this.handler = handler;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_schedule, null);
        bottomSheet.setContentView(view);
        bottomSheetBehavior = BottomSheetBehavior.from((View) (view.getParent()));
        mbConfirm = view.findViewById(R.id.mbConfirm);
        tvDate = view.findViewById(R.id.tvDate);
        tvTime = view.findViewById(R.id.tvTime);
        updateLabel(Calendar.getInstance());
        methods();
        return bottomSheet;
    }

    private void methods() {
        onclickListners();
    }

    private void updateLabel(Calendar myCalendar) {
        String myFormat = "dd MMM, EEE "; //In which you need put here
        String myFormat2 = "h:mm a"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat2, Locale.ENGLISH);
        tvDate.setText(sdf.format(myCalendar.getTime().getTime()+86400000));
        tvTime.setText(sdf2.format(myCalendar.getTime()));
    }

    private void onclickListners() {
        mbConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartResponseData != null) {
                    Intent i = new Intent(getActivity(), CheckoutActivity.class);
                    i.putExtra("datetime", myCalendar);
                    if (applyCouponResponseData != null)
                        i.putExtra("applyCouponResponse", applyCouponResponseData);
                    i.putExtra("cartResponseData", cartResponseData);

                    startActivity(i);
                }
                if (sabziwalaID != null) {
                    ConfirmAddressBottomSheet bottomSheet = new ConfirmAddressBottomSheet(address, latLng, "S1 " + sabziwalaID, myCalendar, handler);
                    bottomSheet.show(getActivity().getSupportFragmentManager(), "ConfirmAddressBottomSheet");
                    dismiss();

                }
            }

        });
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(myCalendar);
            }

        };

        TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                updateLabel(myCalendar);
            }


        };
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                datePickerDialog = new DatePickerDialog(getContext(),R.style.DialogTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()+86400000);
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()+(86400000)*6);
                datePickerDialog.show();
            }
        });
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                timePickerDialog=new TimePickerDialog(getContext(),R.style.DialogTheme, time, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
            }
        });


    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }

//    private void openYourOrderHandpickedFragment() {
//        DialogFragment dialogFragment = new HandpickedFragment();
//        dialogFragment.show(getActivity().getSupportFragmentManager(), "Sabziwala2");
//    }
}

