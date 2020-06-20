package com.hussam.myapplication;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;


public class LocalSendSmsBroadcastReceiver extends BroadcastReceiver {
    public static final String PHONE = "phone";
    public static final String CONTENT = "content";
    private static final String TAG = "TAG";
    private static final String ERROR_MESSAGE = "You have no permission to send a SMS";


    @Override
    public void onReceive(final Context context, final Intent intent) {
        ThreadingHelper.runAsyncInBackground(new Runnable() {
            @Override
            public void run() {
                if (intent == null) {
                    Log.d(TAG, ERROR_MESSAGE);
                    return;
                }
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, ERROR_MESSAGE);
                    return;
                }
                assert intent.getAction() != null;
                if (intent.getAction().equals(ApplicationManager.broadCastFilter)) {
                    String phoneNumber = intent.getStringExtra(PHONE);
                    String msgContent = intent.getStringExtra(CONTENT);
                    if (phoneNumber == null || msgContent == null || phoneNumber.isEmpty()
                            || msgContent.isEmpty()) {
                        Log.d(TAG, ERROR_MESSAGE);
                        return;
                    }
                    sendMsg(msgContent, phoneNumber, context);
                    NotificationHelper nh = new NotificationHelper(context, "smsMessage");
                    nh.createNotification("NOTIFICATION", "Sending sms to " +
                            phoneNumber + ": " + msgContent);
                }
            }
        });

    }
    void sendMsg(String msgContent, String phoneNumber, Context context){

        Intent tempIntent = new Intent(context, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0,
                tempIntent, 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, msgContent, pIntent,
                null);
    }
}
