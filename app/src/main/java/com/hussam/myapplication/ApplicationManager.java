package com.hussam.myapplication;

import android.app.Application;
import android.content.IntentFilter;

import androidx.work.Constraints;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class ApplicationManager extends Application {
    static final String broadCastFilter = "POST_PC.ACTION_SEND_SMS";
    AppStorage storage;

    @Override
    public void onCreate() {
        super.onCreate();
        storage = new AppStorage(this);
        IntentFilter iF = new IntentFilter(broadCastFilter);
        LocalSendSmsBroadcastReceiver smsBroadcastReceiver = new LocalSendSmsBroadcastReceiver();
        registerReceiver(smsBroadcastReceiver, iF);
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(WorkerClass.class, 15, TimeUnit.MINUTES)
                .setConstraints(Constraints.NONE)
                .build();
        WorkManager workManager = WorkManager.getInstance(this);
        workManager.enqueue(request);

    }

}
