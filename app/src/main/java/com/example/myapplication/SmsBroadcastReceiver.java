package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public interface SmsListener {
        void onSmsReceived(String code);
    }

    private SmsListener listener;

    public void setListener(SmsListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);
                if (status != null) {
                    switch (status.getStatusCode()) {
                        case CommonStatusCodes.SUCCESS:
                            String message = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                            if (listener != null && message != null) {
                                // Extract the 6-digit code from the SMS message
                                Pattern pattern = Pattern.compile("\\b(\\d{6})\\b");
                                Matcher matcher = pattern.matcher(message);
                                if (matcher.find()) {
                                    String code = matcher.group(1);
                                    listener.onSmsReceived(code);
                                }
                            }
                            break;
                        case CommonStatusCodes.TIMEOUT:
                            // Waiting for SMS timed out
                            break;
                    }
                }
            }
        }
    }
}