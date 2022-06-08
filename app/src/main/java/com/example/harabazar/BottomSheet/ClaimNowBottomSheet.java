package com.example.harabazar.BottomSheet;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.harabazar.R;
import com.example.harabazar.Service.EventHandler;
import com.example.harabazar.Service.response.OffersResponseData;
import com.example.harabazar.Utilities.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;


public class ClaimNowBottomSheet extends BottomSheetDialogFragment {
    private final OffersResponseData offersResponseData;
    private final String offerText;
    BottomSheetBehavior bottomSheetBehavior;
    MaterialButton mb_claim;
    TextView tvTitle;
    TextView longText;
    EventHandler handler;

    public ClaimNowBottomSheet(OffersResponseData offersResponseData, String offerText, EventHandler handler) {
        this.offersResponseData = offersResponseData;
        this.offerText = offerText;
        this.handler = handler;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog bottomSheet = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View view = View.inflate(getContext(), R.layout.bottom_claim_now, null);
        bottomSheet.setContentView(view);
        bottomSheetBehavior = BottomSheetBehavior.from((View) (view.getParent()));
        mb_claim = view.findViewById(R.id.mb_claim);
        tvTitle = view.findViewById(R.id.tvTitle);
        longText = view.findViewById(R.id.longText);
        methods();
        return bottomSheet;
    }

    private void methods() {
        onCheckInventory();
        setData();
    }

    private void setData() {
        String code = offersResponseData.getCode();
        String discount = offersResponseData.getDiscount_rate();
        String unit = offersResponseData.getDiscount_unit();
        String minCartAmt = offersResponseData.getMin_cart_amount();
        String usesCount = offersResponseData.getUses_count();
        String validFrom = offersResponseData.getValid_from();
        String validTo = offersResponseData.getValid_to();
        tvTitle.setText(offerText);
        longText.setText("Flat " + discount + "%" + " off for new user only. T&C's applied \n  • Get upto " + discount + "%" + "\"+ off \n  • valid from " + validFrom + " to " + validTo + " \n  • valid for 1 time per user\n  • valid on minimum " + "₹" + minCartAmt + " cart value.");


        // longText.setText("Flat 50% off for new user only. T&C's applied \n  . Get upto 50% off \n  . valid from"+validFrom+"-"+validTo+" - \n    11:59 pm \n  . valid for 1 time per user\n  . valid on minimum ₹100 cart value.");

    }

    private void onCheckInventory() {
        mb_claim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("null", offersResponseData.getCode());
                clipboard.setPrimaryClip(clip);
                Utils.ShowToast(getActivity(), "Code Copied");
                if(handler!=null)
                handler.handle(offersResponseData.getCode());

                dismiss();
            }
        });
    }

    @Override
    public int getTheme() {
        return R.style.AppBottomSheetDialogTheme;
    }
}

