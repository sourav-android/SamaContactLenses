package com.sama.samacontactlenses.Helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

public class SMSListener extends BroadcastReceiver {

    private static Common.OTPListener mListener; // this listener will do the magic of throwing the extracted OTP to all the bound views.

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Log.e("messageSSS", "Enter ");
            Bundle extras = intent.getExtras();
            Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
            Log.e("extras",extras.toString());
            Log.e("status",status.toString());
            switch(status.getStatusCode()) {
                case CommonStatusCodes.SUCCESS:
                    // Get SMS message contents
                    String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                    Log.e("messageSSS",message);
                    //Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    String[] part = message.split("[-+*/=:,.!()\\n ]");
                    for (String split : part) {
                        if (MethodClass.isInteger(split) && split.length() == 6) {
                            mListener.onOTPReceived(split);
                        }
                    }
                    break;
                case CommonStatusCodes.TIMEOUT:
                    Log.e("messageSSS","TIMEOUT");
                   // Toast.makeText(context, "TIMEOUT", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
    public static void bindListener(Common.OTPListener listener) {
        mListener = listener;

    }

    public static void unbindListener() {
        mListener = null;
    }
}